<template>
  <div class="layout">
    <Sidebar />
    <div class="content">
      <div class="header">
        <h1>服裝素材管理</h1>
      </div>

      <!-- 分頁標籤 (利用 router to 跳轉) -->
      <v-tabs color="primary" class="tabs mb-6" model-value="categories">
        <v-tab to="/config/doll" value="categories">圖層管理</v-tab>
        <v-tab to="/config/doll/newitem" value="newitem">新增素材</v-tab>
        <v-tab to="/config/doll/adminitems" value="adminitems">商品上下架</v-tab>
      </v-tabs>
      <!-- 主選單卡片 -->
      <v-card class="bg-white pa-6" max-width="450" elevation="6" rounded="xl">
        <v-card-title class="text-h5 font-weight-bold mb-4 d-flex align-center text-primary-darken-1">
          <v-icon icon="mdi-layers-outline" class="mr-2"></v-icon>
            圖層資訊一覽
        </v-card-title>
        <v-card-text class="px-0">
          <!-- 列表選單 -->
          <v-list density="compact" class="px-0 pt-0">
            <v-list-item
              v-for="category in categories"
              :key="category.id"
              class="mb-1 rounded border category-list-item"
              variant="flat"
              style="
                background: var(--color-background-light);
                border-color: var(--color-border) !important;
              "
            >
              <template v-slot:title>
                <span class="font-weight-medium text-body-2">{{
                  category.typeName
                }}</span>
              </template>

              <template v-slot:append>
                <!-- Z 軸標籤 -->
                <v-chip
                  size="medium"
                  variant="tonal"
                  color="primary"
                  class="mr-3 px-4"
                >
                  圖層: {{ category.zIndex }}
                </v-chip>
                <!-- 編輯按鈕 -->
                <v-btn
                  :style="{ visibility: isEditMode ? 'visible' : 'hidden' }"
                  icon="mdi-pencil"
                  variant="text"
                  color="grey-darken-1"
                  size="small"
                  @click="openEditModal(category)"
                ></v-btn>
              </template>
            </v-list-item>
          </v-list>
        </v-card-text>

        <v-card-actions class="px-0 pt-3 border-t mt-2">
          <v-switch
            v-model="isEditMode"
            color="primary"
            label="啟用編輯模式"
            hide-details
            density="compact"
            inset
          ></v-switch>
        </v-card-actions>
      </v-card>

      <!-- Vuetify 內建彈窗 -->
      <v-dialog v-model="isModalVisible" max-width="420" persistent>
        <DollCategoriesEditView
          v-if="isModalVisible"
          :category="selectedCategory"
          @close="isModalVisible = false"
          @save="saveCategory"
        />
      </v-dialog>
    </div>
  </div>
</template>
<script setup>
import DollCategoriesEditView from "./DollCategoriesEditView.vue";
import { useDollCategories } from "@/services/dollSystemApi/dollCategoryViewApi.js";
import { onMounted, ref } from "vue";
import Sidebar from "@/components/config/ConfigSidebar.vue";

const isEditMode = ref(false);
const {
  categories,
  fetchCategories,
  isModalVisible,
  selectedCategory,
  openEditModal,
  saveCategory,
} = useDollCategories();

onMounted(() => {
  fetchCategories();
});
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.content {
  flex-grow: 1;
  padding: 40px;
  background-color: #ffffff;
  max-width: 1000px;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.header h1 {
  margin: 0;
}

.tabs {
  border-bottom: 1px solid #ddd;
}

/* 方案三專用 Hover 樣式 */
.category-list-item {
  transition: background-color 0.2s ease;
}

.category-list-item:hover {
  background-color: #f5f5f5 !important;
}
</style>
