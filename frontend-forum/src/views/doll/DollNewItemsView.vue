<template>
  <v-snackbar
    v-model="isSnackbarVisible"
    :timeout="2000"
    :color="snackbarColor"
    location="top"
  >
    {{ snackbarMessage }}
  </v-snackbar>
  <div class="layout">
    <Sidebar />
    <div class="content">
      <div class="header">
        <h1>服裝素材管理</h1>
      </div>

      <!-- 分頁標籤 (利用 router to 跳轉) -->
      <v-tabs color="primary" class="tabs mb-6" model-value="newitem">
        <v-tab to="/config/doll" value="categories">圖層管理</v-tab>
        <v-tab to="/config/doll/newitem" value="newitem">新增素材</v-tab>
        <v-tab to="/config/doll/adminitems" value="adminitems">商品上下架</v-tab>
      </v-tabs>
    <v-card
      class="bg-white pa-6"
      min-width="450"
      width="500"
      elevation="6"
      rounded="xl"
    >
      <v-card-title class="text-h5 font-weight-bold mb-4 d-flex align-center text-primary-darken-1">
        <v-icon icon="mdi-plus-box-outline" class="mr-2"></v-icon>
        新增圖層素材
      </v-card-title>

      <v-form @submit.prevent="submitItem">
        <v-select
          v-model="formData.categoryId"
          :items="categories"
          item-title="typeName"
          item-value="id"
          label="選擇圖層分類"
          variant="outlined"
          color="primary"
          bg-color="grey-lighten-4"
          class="mb-3"
        ></v-select>

        <v-text-field
          v-model="formData.name"
          label="部件名稱"
          variant="outlined"
          color="primary"
          bg-color="grey-lighten-4"
          class="mb-3"
        ></v-text-field>

        <v-text-field
          v-model.number="formData.price"
          label="販售價格 (點數)"
          type="number"
          min="0"
          variant="outlined"
          color="primary"
          bg-color="grey-lighten-4"
          class="mb-3"
        ></v-text-field>

        <v-file-input
          v-model="selectedFile"
          accept="image/png"
          label="上傳部件圖檔 (.png)"
          prepend-icon=""
          prepend-inner-icon="mdi-camera"
          variant="outlined"
          color="primary"
          bg-color="grey-lighten-4"
          show-size
          class="mb-4"
        ></v-file-input>

        <v-btn
          type="submit"
          color="primary-darken-1"
          variant="flat"
          block
          size="large"
          rounded="lg"
          class="font-weight-bold text-white mt-2"
          :loading="isSubmit"
          elevation="2"
        >
          確認新增
        </v-btn>
      </v-form>
    </v-card>
    </div>
  </div>
</template>
<script setup>
import { addNewDollItem } from '@/services/dollSystemApi/dollNewItemApi';
import Sidebar from "@/components/config/ConfigSidebar.vue";

const { 
  categories, isSubmit, formData, submitItem, selectedFile,
  isSnackbarVisible, snackbarMessage, snackbarColor
} = addNewDollItem();
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
</style>
