<script setup>
import { computed, onMounted, ref } from "vue";
import {
  getTodayTasks,
  checkInToday,
  claimTaskReward,
} from "@/services/dailyTaskApi";

const tasks = ref([]);
const loading = ref(false);
const actionKey = ref(null);

const message = ref("");
const messageType = ref("info");
const needsLogin = ref(false);
const loaded = ref(false);

// 重新查詢成功前，避免拿舊狀態繼續操作。
const stateUncertain = ref(false);

const taskDefinitions = {
  LOGIN: {
    title: "每日登入",
    description: () => "今天成功登入網站，完成每日登入任務。",
  },
  CHECK_IN: {
    title: "每日簽到",
    description: () => "按下簽到，完成後再領取獎勵。",
  },
  POST: {
    title: "發表文章",
    description: (task) =>
      `今天成功新增 ${task.targetCount} 篇文章，編輯既有文章不計入。`,
  },
  COMMENT: {
    title: "回覆／留言",
    description: (task) =>
      `今天成功新增 ${task.targetCount} 則回覆或留言，編輯不計入。`,
  },
  GIVE_LIKE: {
    title: "每日按讚",
    description: (task) =>
      `對其他會員的不同文章或留言，有效按讚累計 ${task.targetCount} 次。`,
  },
  RECEIVE_LIKE: {
    title: "每日被按讚",
    description: (task) =>
      `自己的文章與留言，今天合計收到 ${task.targetCount} 次有效按讚。`,
  },
};

const taskOrder = [
  "LOGIN",
  "CHECK_IN",
  "POST",
  "COMMENT",
  "GIVE_LIKE",
  "RECEIVE_LIKE",
];

const orderedTasks = computed(() =>
  [...tasks.value].sort((a, b) => {
    const aIndex = taskOrder.indexOf(a.taskType);
    const bIndex = taskOrder.indexOf(b.taskType);

    return (aIndex < 0 ? 99 : aIndex) - (bIndex < 0 ? 99 : bIndex);
  })
);

const busy = computed(
  () => loading.value || actionKey.value !== null
);

const claimedCount = computed(
  () => tasks.value.filter((task) => task.status === "CLAIMED").length
);

const claimableCount = computed(
  () => tasks.value.filter((task) => task.status === "CLAIMABLE").length
);

const claimedPoints = computed(() =>
  tasks.value
    .filter((task) => task.status === "CLAIMED")
    .reduce((sum, task) => sum + Number(task.rewardAmount), 0)
);

function taskTitle(task) {
  return taskDefinitions[task.taskType]?.title || task.taskName;
}

function taskDescription(task) {
  return (
    taskDefinitions[task.taskType]?.description(task) ||
    `完成 ${task.targetCount} 次指定活動。`
  );
}

function statusText(status) {
  return {
    IN_PROGRESS: "進行中",
    CLAIMABLE: "可領取",
    CLAIMED: "已領取",
  }[status] || "未知狀態";
}

function statusColor(status) {
  return {
    IN_PROGRESS: "grey",
    CLAIMABLE: "primary",
    CLAIMED: "success",
  }[status] || "grey";
}

function progressPercent(task) {
  if (!task.targetCount) return 0;

  return Math.min(
    100,
    Math.max(0, (task.progressCount / task.targetCount) * 100)
  );
}

function isCheckInAction(task) {
  return (
    task.taskType === "CHECK_IN" &&
    task.status === "IN_PROGRESS"
  );
}

function canAct(task) {
  return isCheckInAction(task) || task.status === "CLAIMABLE";
}

function buttonText(task) {
  if (isCheckInAction(task)) return "立即簽到";
  if (task.status === "CLAIMABLE") return "領取獎勵";
  if (task.status === "CLAIMED") return "已領取";
  return "尚未完成";
}

function taskActionKey(task) {
  return isCheckInAction(task)
    ? `check-in:${task.id}`
    : `claim:${task.id}`;
}

function showMessage(type, text) {
  messageType.value = type;
  message.value = text;
}

function isUnauthorized(error) {
  return error.response?.status === 401;
}

function errorMessage(error) {
  return error.response?.data?.message ||
    error.message ||
    "操作失敗，請稍後再試";
}

function markLoggedOut() {
  needsLogin.value = true;
  tasks.value = [];
  loaded.value = false;

  showMessage("error", "請先登入後查看每日任務");
}

function replaceTask(updatedTask) {
  const index = tasks.value.findIndex(
    (task) => task.id === updatedTask.id
  );

  if (index >= 0) {
    tasks.value[index] = updatedTask;
  } else {
    tasks.value.push(updatedTask);
  }
}

async function refreshTasks() {
  tasks.value = await getTodayTasks();
  needsLogin.value = false;
  stateUncertain.value = false;
  loaded.value = true;
}

async function loadTasks() {
  if (busy.value) return;

  loading.value = true;
  message.value = "";

  try {
    await refreshTasks();
  } catch (error) {
    stateUncertain.value = true;

    if (isUnauthorized(error)) {
      markLoggedOut();
    } else {
      showMessage("error", errorMessage(error));
    }
  } finally {
    loading.value = false;
  }
}

/**
 * POST 發生錯誤時，後端仍可能已成功完成操作，
 * 因此先重新查詢狀態，不直接再次發送。
 */
async function reconcileAfterError(error, task, action) {
  if (isUnauthorized(error)) {
    markLoggedOut();
    return;
  }

  stateUncertain.value = true;

  try {
    await refreshTasks();

    const current = tasks.value.find(
      (item) => item.id === task.id
    );

    if (action === "claim" && current?.status === "CLAIMED") {
      showMessage(
        "info",
        "已確認這項任務已領取，詳細入帳請查看點數交易紀錄。"
      );
      return;
    }

    if (
      action === "check-in" &&
      current &&
      ["CLAIMABLE", "CLAIMED"].includes(current.status)
    ) {
      showMessage(
        "info",
        current.status === "CLAIMED"
          ? "今天已完成簽到並領取獎勵。"
          : "今天已完成簽到，現在可以領取獎勵。"
      );
      return;
    }

    showMessage("warning", errorMessage(error));
  } catch (refreshError) {
    if (isUnauthorized(refreshError)) {
      markLoggedOut();
    } else {
      showMessage(
        "warning",
        "暫時無法確認操作結果，請按重新整理後再操作。"
      );
    }
  }
}

async function performTaskAction(task) {
  if (busy.value || stateUncertain.value || !canAct(task)) {
    return;
  }

  const action = isCheckInAction(task) ? "check-in" : "claim";

  actionKey.value = `${action}:${task.id}`;
  message.value = "";

  try {
    if (action === "check-in") {
      const result = await checkInToday();

      replaceTask(result);

      showMessage(
        "success",
        result.status === "CLAIMED"
          ? "今天已完成簽到並領取獎勵。"
          : "簽到完成！請再按「領取獎勵」取得點數。"
      );
    } else {
      const result = await claimTaskReward(task.id);

      replaceTask({
        ...task,
        status: result.status,
      });

      showMessage(
        "success",
        `成功領取 ${result.rewardAmount} 點！`
      );
    }
  } catch (error) {
    await reconcileAfterError(error, task, action);
    return;
  } finally {
    // 在下方重新查詢完成前，仍維持操作鎖定。
    // 發生錯誤的分支則在 reconcile 完成後解除。
    if (messageType.value !== "success") {
      actionKey.value = null;
    }
  }

  try {
    await refreshTasks();
  } catch (error) {
    stateUncertain.value = true;

    if (isUnauthorized(error)) {
      markLoggedOut();
    } else {
      showMessage(
        "warning",
        `${message.value} 任務列表更新失敗，請按重新整理。`
      );
    }
  } finally {
    actionKey.value = null;
  }
}

onMounted(loadTasks);
</script>

<template>
  <v-container class="daily-tasks-page py-8">
    <div
      class="d-flex align-center justify-space-between flex-wrap ga-3 mb-6"
    >
      <div>
        <h1 class="text-h5 font-weight-bold">每日任務</h1>

        <p class="text-body-2 text-medium-emphasis mt-2">
          完成每日活動，手動領取點數獎勵。
        </p>
      </div>
      <v-btn
           color="primary"
           variant="outlined"
           :to="{ name: 'CoinTransactions' }">
            點數交易紀錄</v-btn>


      <v-btn
        color="primary"
        variant="tonal"
        :loading="loading"
        :disabled="actionKey !== null"
        @click="loadTasks"
      >
        重新整理
      </v-btn>
    </div>

    <v-alert
      v-if="message"
      :type="messageType"
      variant="tonal"
      class="mb-5"
      role="status"
      aria-live="polite"
    >
      {{ message }}
    </v-alert>

    <v-btn
      v-if="needsLogin"
      to="/login"
      color="primary"
      class="mb-5"
    >
      前往登入
    </v-btn>

    <template v-if="!needsLogin">
      <v-card
        v-if="loaded"
        color="primary"
        variant="tonal"
        rounded="lg"
        class="mb-6"
      >
        <v-card-text>
          <v-row>
            <v-col cols="12" sm="4">
              <div class="text-body-2">今日已領取</div>
              <div class="text-h5 font-weight-bold mt-1">
                {{ claimedCount }}／{{ tasks.length }} 項
              </div>
            </v-col>

            <v-col cols="12" sm="4">
              <div class="text-body-2">待領取任務</div>
              <div class="text-h5 font-weight-bold mt-1">
                {{ claimableCount }} 項
              </div>
            </v-col>

            <v-col cols="12" sm="4">
              <div class="text-body-2">今日任務已領點數</div>
              <div class="text-h5 font-weight-bold mt-1">
                {{ claimedPoints }} 點
              </div>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>

      <v-progress-linear
        v-if="loading"
        indeterminate
        color="primary"
        class="mb-5"
      />

      <v-row>
        <v-col
          v-for="task in orderedTasks"
          :key="task.id"
          cols="12"
          md="6"
        >
          <v-card
            rounded="lg"
            variant="outlined"
            class="h-100"
          >
            <v-card-text class="d-flex flex-column h-100">
              <div
                class="d-flex align-center justify-space-between ga-3"
              >
                <h2 class="text-h6">
                  {{ taskTitle(task) }}
                </h2>

                <v-chip
                  :color="statusColor(task.status)"
                  variant="tonal"
                  size="small"
                >
                  {{ statusText(task.status) }}
                </v-chip>
              </div>

              <p class="task-description text-body-2 text-medium-emphasis mt-4">
                {{ taskDescription(task) }}
              </p>

              <div class="text-h5 text-primary font-weight-bold mt-4">
                +{{ task.rewardAmount }} 點
              </div>

              <div class="d-flex justify-space-between mt-5 mb-2">
                <span class="text-body-2">任務進度</span>
                <span class="text-body-2">
                  {{ task.progressCount }}／{{ task.targetCount }}
                </span>
              </div>

              <v-progress-linear
                :model-value="progressPercent(task)"
                :color="
                  task.status === 'CLAIMED' ? 'success' : 'primary'
                "
                height="8"
                rounded
              />

              <v-spacer />

              <v-btn
                block
                color="primary"
                :variant="isCheckInAction(task) ? 'tonal' : 'flat'"
                class="mt-6"
                :loading="actionKey === taskActionKey(task)"
                :disabled="busy || stateUncertain || !canAct(task)"
                @click="performTaskAction(task)"
              >
                {{ buttonText(task) }}
              </v-btn>

              <p
                v-if="task.taskType === 'CHECK_IN'"
                class="text-caption text-medium-emphasis mt-2"
              >
                簽到只完成任務，點數需另外領取。
              </p>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <v-alert
        v-if="loaded && !loading && tasks.length === 0"
        type="info"
        variant="tonal"
        class="mt-4"
      >
        今天暫時沒有任務。
      </v-alert>
    </template>

    <v-divider class="mt-8 mb-4" />

    <p class="text-caption text-medium-emphasis">
      每日以台灣時間 00:00 換日，每項任務每天限領一次，
      獎勵需於當天領取。
    </p>

    <p class="text-caption text-medium-emphasis mt-2">
      自己按讚自己不計入；取消後重新按讚不重複計次。
      完成其他頁面的活動後，可按重新整理查看進度。
    </p>
  </v-container>
</template>

<style scoped>
.daily-tasks-page {
  max-width: 1040px;
}

.task-description {
  min-height: 44px;
}
</style>