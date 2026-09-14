<template>
  <v-dialog
    :model-value="modelValue"
    max-width="700"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <v-card color="white">
      <v-card-title class="d-flex align-center">
        <span>留言編輯紀錄</span>

        <v-spacer />

        <v-btn
          icon="mdi-close"
          variant="text"
          @click="emit('update:modelValue', false)"
        />
      </v-card-title>

      <v-divider />

      <v-card-text>
        <!-- 載入中 -->
        <div v-if="loading" class="text-center py-8">
          <v-progress-circular
            indeterminate
            color="primary"
          />
        </div>

        <!-- 錯誤訊息 -->
        <v-alert
          v-else-if="errorMessage"
          type="error"
          variant="tonal"
        >
          {{ errorMessage }}
        </v-alert>

        <!-- 沒有紀錄 -->
        <v-alert
          v-else-if="histories.length === 0"
          type="info"
          variant="tonal"
        >
          目前沒有留言編輯紀錄
        </v-alert>

        <!-- 編輯紀錄 -->
        <v-card
          v-for="history in histories"
          v-else
          :key="history.id"
          color="white"
          class="history-card mb-4"
        >
          <v-card-text>
            <div class="font-weight-bold mb-1">
              修改前
            </div>

            <div class="history-content">
              {{ history.oldContent }}
            </div>

            <div class="history-time">
              修改時間：{{ formatDate(history.modifyTime) }}
            </div>
          </v-card-text>
        </v-card>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup>
defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },

  histories: {
    type: Array,
    default: () => [],
  },

  loading: {
    type: Boolean,
    default: false,
  },

  errorMessage: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["update:modelValue"]);

const formatDate = (date) => {
  if (!date) return "";

  return new Date(date).toLocaleString("zh-TW", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  });
};
</script>

<style scoped>
.history-content {
  padding: 10px 12px;
  border-radius: 6px;
  background: #f5f5f5;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.history-time {
  margin-top: 8px;
  color: #757575;
  font-size: 12px;
  text-align: right;
}

.history-card {
  color: #212121 !important;
  border: none !important;
  box-shadow: none !important;
}
</style>