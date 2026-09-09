<template>
  <v-container class="d-flex justify-center mt-8">
    <!-- 主選單卡片 -->
    <v-card
      width="320"
      rounded="lg"
      elevation="6"
      theme="dark"
      class="bg-grey-darken-4"
    >
      <v-card-item class="pb-2">
        <v-card-title
          class="text-h6 font-weight-bold d-flex align-center letter-spacing-1"
        >
          <v-icon
            start
            icon="mdi-layers-outline"
            color="cyan-accent-3"
          ></v-icon>
          服裝圖層管理
        </v-card-title>
      </v-card-item>

      <v-divider class="mb-2"></v-divider>

      <!-- 列表選單 -->
      <v-list bg-color="transparent" class="px-3 pb-3">
        <v-list-item
          v-for="category in categories"
          :key="category.id"
          class="mb-3 rounded-lg"
          variant="tonal"
          color="cyan-accent-3"
          base-color="grey-lighten-1"
          @click="openEditModal(category)"
        >
          <template v-slot:title>
            <span class="font-weight-bold text-subtitle-1">{{
              category.typeName
            }}</span>
          </template>

          <template v-slot:append>
            <v-chip
              size="small"
              variant="flat"
              color="grey-darken-3"
              class="text-grey-lighten-1"
            >
              Z: {{ category.zIndex }}
            </v-chip>
          </template>
        </v-list-item>
      </v-list>
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
import { useDollCategories } from "@/service/DollSystemApi/DollCategoryViewApi.js";
import { onMounted } from "vue";
const {
  categories,
  fetchCategories,
  isModalVisible,
  selectedCategory,
  openEditModal,
  saveCategory,
} = useDollCategories();

onMounted(()=>{
  fetchCategories();
})
</script>

<style scoped></style>
