<template>
  <HomeHeader />
  <v-container class="py-5">
    <!-- 主選單卡片 -->
    <v-card class="pa-5 mx-auto" max-width="600" elevation="2" rounded="lg">
      <v-card-title class="text-h5 mb-4 d-flex align-center">
        <v-icon
          start
          icon="mdi-layers-outline"
          color="primary"
          class="mr-2"
        ></v-icon>
        服裝圖層管理
      </v-card-title>

      <v-card-text class="px-0">
        <!-- 列表選單 -->
        <v-list class="px-0 pt-0">
          <v-list-item
            v-for="category in categories"
            :key="category.id"
            class="mb-3 rounded-lg border"
            variant="flat"
            @click="openEditModal(category)"
            style="
              background: var(--color-background-light);
              border-color: var(--color-border) !important;
            "
            hover
          >
            <template v-slot:title>
              <span class="font-weight-bold text-subtitle-1">{{
                category.typeName
              }}</span>
            </template>

            <template v-slot:append>
              <v-chip size="small" variant="tonal" color="primary">
                Z: {{ category.zIndex }}
              </v-chip>
            </template>
          </v-list-item>
        </v-list>
      </v-card-text>
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
  </v-container>
</template>
<script setup>
import DollCategoriesEditView from "./DollCategoriesEditView.vue";
import { useDollCategories } from "@/services/dollSystemApi/dollCategoryViewApi.js";
import { onMounted } from "vue";
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

<style scoped></style>
