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
    <v-tabs v-model="activeTab" color="primary" class="mb-5">
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
import { ref } from "vue";
import BoardManagement from "@/components/admin/BoardManagement.vue";
import BoardModeratorManagement from "@/components/admin/BoardModeratorManagement.vue";
import CategoryManagement from "@/components/admin/CategoryManagement.vue";

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
  // BoardManagement 尚未建立時，回傳已完成的 Promise
  return boardManagementRef.value?.fetchBoards() ?? Promise.resolve();
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
</script>