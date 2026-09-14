<script setup>
import { computed, onMounted, ref } from "vue";
import { getTodayTasks, claimTaskReward } from "@/services/dailyTaskApi";

const tasks = ref([]);
const loading = ref(false);
const claimingId = ref(null);

const message = ref("");
const messageType = ref("info");
const needsLogin = ref(false);
const balanceAfter = ref(null);

const claimedCount = computed(
  () => tasks.value.filter((task) => task.status === "CLAIMED").length,
);

function progressPercent(task) {
  if (!task.targetCount) return 0;

  return Math.min(
    100,
    Math.max(0, (task.progressCount / task.targetCount) * 100),
  );
}

function statusText(status) {
  return (
    {
      IN_PROGRESS: "進行中",
      CLAIMABLE: "可領取",
      CLAIMED: "已領取",
    }[status] || "未知狀態"
  );
}

function statusColor(status) {
  return (
    {
      IN_PROGRESS: "grey",
      CLAIMABLE: "primary",
      CLAIMED: "success",
    }[status] || "grey"
  );
}

function taskDescription(task) {
  if (task.taskType === "GIVE_LIKE") {
    return `幫其他會員的文章或留言按讚 ${task.targetCount} 次`;
  }

  if (task.taskType === "RECEIVE_LIKE") {
    return `自己的文章或留言累計收到 ${task.targetCount} 次有效按讚`;
  }

  return `完成 ${task.targetCount} 次指定活動`;
}

async function refreshTasks() {
  const result = await getTodayTasks();

  if (!Array.isArray(result)) {
    throw new Error("任務資料格式不正確");
  }

  tasks.value = result;
  needsLogin.value = false;
}

async function loadTasks() {
  loading.value = true;
  message.value = "";

  try {
    await refreshTasks();
  } catch (error) {
    messageType.value = "error";
    message.value = error.message || "無法讀取任務";

    if (error.status === 401) {
      needsLogin.value = true;
      tasks.value = [];
    }
  } finally {
    loading.value = false;
  }
}

async function claim(task) {
  if (claimingId.value || loading.value || task.status !== "CLAIMABLE") {
    return;
  }

  claimingId.value = task.id;
  message.value = "";

  try {
    const result = await claimTaskReward(task.id);

    // API 確認成功後，立即更新畫面。
    task.status = result.status;
    balanceAfter.value = result.balanceAfter;

    messageType.value = "success";
    message.value = `成功領取 ${result.rewardAmount} 點！`;

    try {
      await refreshTasks();
    } catch {
      messageType.value = "warning";
      message.value =
        `已成功領取 ${result.rewardAmount} 點，` +
        "但任務列表更新失敗，請按重新整理。";
    }
  } catch (error) {
    if (error.status === 401) {
      needsLogin.value = true;
      tasks.value = [];
      messageType.value = "error";
      message.value = "登入已失效，請重新登入";
    } else {
      // 網路中斷時，後端可能已完成入帳，先重新確認狀態。
      let refreshed = false;

      try {
        await refreshTasks();
        refreshed = true;
      } catch {
        // 保留原始領獎錯誤，讓會員稍後重新查詢。
      }

      const currentTask = tasks.value.find((item) => item.id === task.id);

      if (refreshed && currentTask?.status === "CLAIMED") {
        messageType.value = "info";
        message.value = "已確認這項任務已領取，請至點數紀錄查看。";
      } else {
        messageType.value = "warning";
        message.value = refreshed
          ? error.message || "領取失敗，請稍後再試"
          : "暫時無法確認領取結果，請重新整理任務或查看點數紀錄。";
      }
    }
  } finally {
    claimingId.value = null;
  }
}

onMounted(loadTasks);
</script>

<template>
  <v-container class="py-8" style="max-width: 1000px">
    <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-6">
      <div>
        <h1 class="text-h5 font-weight-bold">每日任務</h1>
        <p class="text-body-2 text-medium-emphasis mt-2">
          完成任務後，記得手動領取點數。
        </p>
      </div>

      <v-btn
        variant="tonal"
        color="primary"
        :loading="loading"
        :disabled="claimingId !== null"
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
    >
      {{ message }}
    </v-alert>

    <v-btn v-if="needsLogin" to="/login" color="primary" class="mb-5">
      前往登入
    </v-btn>

    <v-card
      v-if="!needsLogin"
      rounded="lg"
      variant="tonal"
      color="primary"
      class="mb-6"
    >
      <v-card-text>
        <div class="text-subtitle-1 font-weight-bold">
          今日已領取 {{ claimedCount }}／{{ tasks.length }} 項任務
        </div>

        <div v-if="balanceAfter !== null" class="mt-2">
          最近一次領獎後餘額：{{ balanceAfter }} 點
        </div>

        <div class="text-body-2 mt-2">
          按讚與被按讚任務各獎勵 10 點，每項每天限領一次。
        </div>
      </v-card-text>
    </v-card>

    <v-progress-linear
      v-if="loading"
      indeterminate
      color="primary"
      class="mb-5"
    />

    <v-row v-if="!needsLogin">
      <v-col v-for="task in tasks" :key="task.id" cols="12" md="6">
        <v-card rounded="lg" variant="outlined" class="h-100">
          <v-card-text>
            <div class="d-flex align-center justify-space-between ga-3">
              <h2 class="text-h6">{{ task.taskName }}</h2>

              <v-chip
                :color="statusColor(task.status)"
                size="small"
                variant="tonal"
              >
                {{ statusText(task.status) }}
              </v-chip>
            </div>

            <p class="text-body-2 text-medium-emphasis mt-4">
              {{ taskDescription(task) }}
            </p>

            <div class="text-h5 text-primary font-weight-bold mt-5">
              +{{ task.rewardAmount }} 點
            </div>

            <div class="d-flex justify-space-between mt-5 mb-2">
              <span>任務進度</span>
              <span>{{ task.progressCount }}／{{ task.targetCount }}</span>
            </div>

            <v-progress-linear
              :model-value="progressPercent(task)"
              :color="task.status === 'CLAIMED' ? 'success' : 'primary'"
              height="8"
              rounded
            />

            <v-btn
              block
              color="primary"
              variant="flat"
              class="mt-6"
              :loading="claimingId === task.id"
              :disabled="
                task.status !== 'CLAIMABLE' || claimingId !== null || loading
              "
              @click="claim(task)"
            >
              {{
                task.status === "CLAIMED"
                  ? "已領取"
                  : task.status === "CLAIMABLE"
                    ? "領取獎勵"
                    : "尚未完成"
              }}
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-alert
      v-if="!loading && !needsLogin && !message && tasks.length === 0"
      type="info"
      variant="tonal"
      class="mt-4"
    >
      今天暫時沒有任務。
    </v-alert>

    <p class="text-caption text-medium-emphasis mt-6">
      每日以台灣時間 00:00 換日，獎勵需於當天領取。
      自己按讚自己不計入，取消後重新按讚不重複計次。
    </p>
  </v-container>
</template>
