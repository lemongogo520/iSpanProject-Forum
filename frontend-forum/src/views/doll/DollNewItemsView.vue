<template>
  <v-container class="fill-height d-flex align-center justify-center">
    <v-card
      theme="dark"
      class="bg-grey-darken-4 pa-4"
      min-width="450"
      width="500"
    >
      <v-card-title class="text-h6 font-weight-bold mb-4">
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
          color="cyan-accent-3"
          class="mb-3"
        ></v-select>

        <v-text-field
          v-model="formData.name"
          label="部件名稱"
          variant="outlined"
          color="cyan-accent-3"
          class="mb-3"
        ></v-text-field>

        <v-file-input
          v-model="selectedFile"
          accept="image/png"
          label="上傳部件圖檔 (.png)"
          prepend-icon="mdi-camera"
          variant="outlined"
          color="cyan-accent-3"
          show-size
          class="mb-4"
        ></v-file-input>

        <v-btn
          type="submit"
          color="cyan-accent-3"
          block
          class="text-grey-darken-4 font-weight-bold"
          :loading="isSubmit"
        >
          確認新增
        </v-btn>
      </v-form>
    </v-card>
  </v-container>
</template>
<script setup>
import { addNewDollItem } from '@/services/dollSystemApi/dollNewItemApi';

const { categories, isSubmit, formData, submitItem, selectedFile } =
  addNewDollItem();
</script>
<style></style>
