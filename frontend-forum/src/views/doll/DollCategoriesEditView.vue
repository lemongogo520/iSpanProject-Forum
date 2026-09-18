<template>
  <v-card class="bg-white" rounded="lg" elevation="6">
    <v-card-title
      class="d-flex align-center pa-4 border-b font-weight-bold text-primary-darken-1"
    >
      <v-icon start icon="mdi-pencil-outline" color="primary"></v-icon>
      編輯圖層資訊
    </v-card-title>

    <v-card-text class="pt-6">
      <v-text-field
        v-model="formData.typeName"
        label="分類名稱 (TypeName)"
        variant="outlined"
        color="primary"
        bg-color="grey-lighten-4"
        density="comfortable"
        class="mb-4"
        hide-details
      ></v-text-field>

      <v-text-field
        v-model.number="formData.zIndex"
        label="圖層 (Z-Index)"
        type="number"
        variant="outlined"
        color="primary"
        bg-color="grey-lighten-4"
        density="comfortable"
        hide-details
      ></v-text-field>
    </v-card-text>

    <v-card-actions class="px-4 pb-4 pt-0">
      <v-spacer></v-spacer>
      <v-btn
        variant="outlined"
        color="grey-darken-1"
        class="px-4"
        @click="emit('close')"
      >
        取消
      </v-btn>
      <v-btn
        color="primary-darken-1"
        variant="flat"
        class="px-4 text-white font-weight-bold"
        @click="handleSave"
      >
        儲存
      </v-btn>
    </v-card-actions>
  </v-card>
</template>
<script setup>
import { ref, watch } from "vue";

const props = defineProps({
  category: {
    type: Object,
    required: true,
  },
});

// 定義回傳給父組件的事件
const emit = defineEmits(["close", "save"]);

const formData = ref({ ...props.category });

// 當父層傳入的 category 改變時，同步更新表單內容
watch(
  () => props.category,
  (newVal) => {
    formData.value = { ...newVal };
  },
  //深度監聽
  { deep: true },
);

const handleSave = () => {
  // 將修改後的資料往上傳遞
  emit("save", formData.value);
};
</script>
<style scoped></style>
