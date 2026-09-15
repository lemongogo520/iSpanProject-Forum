<template>
  <v-dialog :model-value="modelValue" max-width="420" persistent>
    <v-card>
      <v-card-title class="text-h6">
        {{ title }}
      </v-card-title>

      <v-card-text>
        <div class="mb-4">
          {{ message }}
        </div>

        <v-select
          v-if="reasonRequired"
          :model-value="reason"
          :items="deleteReasons"
          label="請選擇刪除原因"
          variant="outlined"
          density="comfortable"
          hide-details="auto"
          @update:model-value="emit('update:reason', $event)"
        />
      </v-card-text>

      <v-card-actions>
        <v-spacer />

        <v-btn
          variant="text"
          :disabled="loading || (reasonRequired && !reason?.trim())"
          @click="emit('cancel')"
        >
          取消
        </v-btn>

        <v-btn
          color="error"
          variant="flat"
          :loading="loading"
          :disabled="loading"
          @click="emit('confirm')"
        >
          確定刪除
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },

  title: {
    type: String,
    default: "確認刪除",
  },

  message: {
    type: String,
    required: true,
  },

  loading: {
    type: Boolean,
    default: false,
  },
  reasonRequired: {
    type: Boolean,
    default: false,
  },
  reason: {
    type: String,
    default: "",
  },
});
const deleteReasons = [
  "廣告或垃圾內容",
  "人身攻擊或不當言論",
  "色情或暴力內容",
  "洩漏個人資料",
  "與討論版主題無關",
  "其他違反規章的內容",
];

const emit = defineEmits(["cancel", "confirm", "update:reason"]);
</script>
