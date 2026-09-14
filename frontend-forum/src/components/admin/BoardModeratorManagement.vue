<template>
  <!-- 指定討論版版主 -->
  <v-card class="admin-card pa-4 mb-5" elevation="0">
    <v-card-title class="px-0"> 指定討論版版主 </v-card-title>

    <v-card-text class="px-0">
      <v-select
        v-model="moderatorBoardId"
        :items="boards"
        item-title="name"
        item-value="id"
        label="選擇討論版"
        variant="outlined"
        density="comfortable"
        @update:model-value="handleBoardChange"
      />

      <!-- 選擇要指派為版主的使用者 -->
      <div class="moderator-user-selector">
        <GetAllUsers
          :key="userSelectorKey"
          :types="[3]"
          immediate-select
          @select="handleUserSelect"
        />
      </div>
      <v-btn
        color="success"
        prepend-icon="mdi-account-plus"
        block
        :loading="moderatorSaving"
        :disabled="!moderatorBoardId || !moderatorUserId"
        @click="addModerator"
      >
        設為版主
      </v-btn>
    </v-card-text>
  </v-card>

  <!-- 目前版主列表 -->
  <v-card class="admin-card pa-4" elevation="0">
    <div class="d-flex justify-space-between align-center mb-3">
      <v-card-title class="px-0"> 目前版主 </v-card-title>

      <v-btn
        size="small"
        variant="outlined"
        prepend-icon="mdi-refresh"
        :disabled="!moderatorBoardId"
        @click="fetchModerators"
      >
        重新整理
      </v-btn>
    </div>

    <!-- 載入進度條 -->
    <v-progress-linear
      v-if="moderatorLoading"
      indeterminate
      color="primary"
      class="mb-4"
    />

    <v-list v-if="moderators.length > 0">
      <v-list-item
        v-for="moderator in moderators"
        :key="moderator.id"
        border
        class="mb-2 rounded"
      >
        <v-list-item-title>
          {{ moderator.account }}
        </v-list-item-title>

        <v-list-item-subtitle>
          討論版：{{ moderator.boardName }}
        </v-list-item-subtitle>

        <template #append>
          <v-btn
            size="small"
            color="error"
            variant="tonal"
            :loading="removingModeratorId === moderator.userId"
            @click="removeModerator(moderator)"
          >
            移除
          </v-btn>
        </template>
      </v-list-item>
    </v-list>

    <!-- 已選擇討論版，但目前沒有版主 -->
    <div
      v-else-if="moderatorBoardId && !moderatorLoading"
      class="text-medium-emphasis text-center py-5"
    >
      此討論版目前沒有版主
    </div>

    <!-- 尚未選擇討論版 -->
    <div
      v-else-if="!moderatorBoardId"
      class="text-medium-emphasis text-center py-5"
    >
      請先選擇討論版
    </div>
  </v-card>
</template>

<script setup>
import { ref } from "vue";
import httpClient from "@/services/httpClient";
import { useNotification } from "@/composables/useNotification";
import GetAllUsers from "@/components/user/GetAllUsers.vue";

// ======================
// Props
// ======================

// 接收父元件傳入的討論版清單
defineProps({
  boards: {
    type: Array,
    default: () => [],
  },
});

// ======================
// 通知功能
// ======================

const { showMessage } = useNotification();

// ======================
// 版主表單
// ======================

// 目前選擇的討論版 ID
const moderatorBoardId = ref(null);

// 準備設為版主的使用者 UUID
const moderatorUserId = ref(null);
const selectedUser = ref(null);
const userSelectorKey = ref(0);

// ======================
// 版主清單
// ======================

// 目前選取討論版的版主清單
const moderators = ref([]);

// ======================
// 載入狀態
// ======================

// 查詢版主清單的載入狀態
const moderatorLoading = ref(false);

// 新增版主的載入狀態
const moderatorSaving = ref(false);

// 目前正在移除的版主使用者 ID
const removingModeratorId = ref(null);

// ======================
// 錯誤訊息處理
// ======================

// 取得後端錯誤訊息，沒有訊息時使用預設內容
const getErrorMessage = (error, fallback) => {
  const data = error.response?.data;

  if (typeof data === "string") {
    return data;
  }

  return data?.message ?? fallback;
};

// ======================
// 查詢版主
// ======================

const fetchModerators = () => {
  // 切換討論版時先清空原本的版主清單
  moderators.value = [];

  // 尚未選擇討論版時不發送請求
  if (!moderatorBoardId.value) {
    return Promise.resolve();
  }

  moderatorLoading.value = true;

  return httpClient({
    method: "get",
    url: `/api/boards/${moderatorBoardId.value}/moderators`,
  })
    .then((response) => {
      moderators.value = response.data;
    })
    .catch((error) => {
      showMessage(getErrorMessage(error, "取得版主列表失敗"), "error");
    })
    .finally(() => {
      moderatorLoading.value = false;
    });
};

// ======================
// 新增版主
// ======================

const handleUserSelect = (user) => {
  selectedUser.value = user;
  moderatorUserId.value = user?.uid ?? null;
};

const addModerator = () => {
  // 移除 UUID 前後的空白
  const userId = moderatorUserId.value;

  // 驗證討論版與使用者 UUID
  if (!moderatorBoardId.value || !userId) {
    showMessage("請選擇討論版與使用者", "warning");
    return;
  }

  moderatorSaving.value = true;

  return httpClient({
    method: "post",
    url: `/api/boards/${moderatorBoardId.value}/moderators`,
    data: {
      userId,
    },
  })
    .then(() => {
      showMessage("版主新增成功", "success");
      moderatorUserId.value = null;
      selectedUser.value = null;

      // 重新建立 GetAllUsers，清除元件內原本的選擇
      userSelectorKey.value += 1;

      // 重新取得最新的版主清單
      return fetchModerators();
    })
    .catch((error) => {
      showMessage(getErrorMessage(error, "新增版主失敗"), "error");
    })
    .finally(() => {
      moderatorSaving.value = false;
    });
};

// ======================
// 移除版主
// ======================

const removeModerator = (moderator) => {
  // 記錄正在移除的版主，控制該按鈕的載入狀態
  removingModeratorId.value = moderator.userId;

  return httpClient({
    method: "delete",
    url: `/api/boards/${moderatorBoardId.value}/moderators/${moderator.userId}`,
  })
    .then(() => {
      showMessage("版主移除成功", "success");

      // 重新取得最新的版主清單
      return fetchModerators();
    })
    .catch((error) => {
      showMessage(getErrorMessage(error, "移除版主失敗"), "error");
    })
    .finally(() => {
      removingModeratorId.value = null;
    });
};

const handleBoardChange = () => {
  moderatorUserId.value = null;
  selectedUser.value = null;
  userSelectorKey.value += 1;
  fetchModerators();
};
</script>

<style scoped>
.admin-card {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
}
.moderator-user-selector
  :deep(.user-selector-actions .v-btn:first-child) {
  display: none;
}
</style>
