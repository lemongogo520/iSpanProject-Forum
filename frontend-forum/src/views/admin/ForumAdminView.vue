<template>
  <v-container class="py-6">
    <!-- 頁面標題 -->
    <div class="mb-6">
      <h2 class="mb-1">系統管理</h2>
      <div class="text-body-2 text-medium-emphasis">
        管理論壇類別、討論版與各版版主
      </div>
    </div>

    <!-- 管理功能分頁 -->
    <div class="d-flex align-center mb-5">
      <v-tabs v-model="activeTab" color="primary" class="flex-grow-1">
        <v-tab value="categories">
          <v-icon icon="mdi-shape-outline" class="me-2" />
          類別管理
        </v-tab>
        <v-tab value="boards">
          <v-icon icon="mdi-forum-outline" class="me-2" />
          討論版管理
        </v-tab>
        <v-tab value="moderators">
          <v-icon icon="mdi-account-tie" class="me-2" />
          版主管理
        </v-tab>
      </v-tabs>
      <v-btn
        v-if="isSystemAdmin"
        color="secondary"
        variant="elevated"
        prepend-icon="mdi-database-plus-outline"
        class="ms-4 flex-shrink-0"
        :loading="generating"
        :disabled="generating"
        @click="generateTestData"
      >
        產生假資料
      </v-btn>
    </div>

    <!-- 管理功能內容 -->
    <v-window v-model="activeTab">
      <!-- 類別管理 -->
      <v-window-item value="categories">
        <CategoryManagement
          @categories-updated="handleCategoriesUpdated"
          @categories-changed="refreshBoards"
        />
      </v-window-item>

      <!-- 討論版管理 -->
      <v-window-item value="boards">
        <BoardManagement
          ref="boardManagementRef"
          :categories="categories"
          @boards-updated="handleBoardsUpdated"
        />
      </v-window-item>

      <!-- 版主管理 -->
      <v-window-item value="moderators">
        <BoardModeratorManagement :boards="boards" />
      </v-window-item>
    </v-window>
  </v-container>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import BoardManagement from "@/components/admin/BoardManagement.vue";
import BoardModeratorManagement from "@/components/admin/BoardModeratorManagement.vue";
import CategoryManagement from "@/components/admin/CategoryManagement.vue";
import httpClient from "@/services/httpClient";
import { useUserStore } from "@/stores/user";
import { useNotification } from "@/composables/useNotification";

const userStore = useUserStore();
const { showMessage } = useNotification();
const generating = ref(false);
const isSystemAdmin = computed(() => {
  return Number(userStore.permission) === 1;
});
// ======================
// 管理頁籤狀態
// ======================

// 目前顯示的管理頁籤
const activeTab = ref("categories");

// ======================
// 類別與討論版資料
// ======================

// 從 CategoryManagement 接收的類別清單
const categories = ref([]);

// 從 BoardManagement 接收的討論版清單
const boards = ref([]);
const fetchBoards = () => {
  return httpClient({
    method: "get",
    url: "/api/boards",
  })
    .then((response) => {
      boards.value = response.data;
    })
    .catch((error) => {
      console.error("取得討論版失敗：", error);
      boards.value = [];
    });
};

// ======================
// 子元件參照
// ======================

// BoardManagement 元件參照
// 用來呼叫子元件公開的 fetchBoards 函式
const boardManagementRef = ref(null);

// ======================
// 重新取得討論版
// ======================

const refreshBoards = () => {
  // 子元件已掛載：由子元件查詢，並透過 boards-updated 同步父層
  if (boardManagementRef.value) {
    return boardManagementRef.value?.fetchBoards();
  }
  // 子元件尚未掛載：由父元件直接查詢
  return fetchBoards();
};

// ======================
// 更新討論版清單
// ======================

const handleBoardsUpdated = (updatedBoards) => {
  boards.value = updatedBoards;
};

// ======================
// 更新類別清單
// ======================

const handleCategoriesUpdated = (updatedCategories) => {
  categories.value = updatedCategories;
};
const generateTestData = async () => {
  generating.value = true;
  try {
    const response = await httpClient.post("/api/dev/seed/school-forum");
    const postsCreated = response.data.postsCreated ?? 0;
    const commentsCreated = response.data.commentsCreated ?? 0;
    if (postsCreated === 0) {
      showMessage("假資料已存在，沒有新增重複文章", "info");
      return;
    }
    showMessage(
      `建立完成：${postsCreated} 篇文章、${commentsCreated} 則留言`,
      "success",
    );
  } catch (error) {
    showMessage(error.response?.data?.message || "產生假資料失敗", "error");
  } finally {
    generating.value = false;
  }
};
onMounted(() => {
  fetchBoards();
});
</script>
