<template>
  <v-dialog
    :model-value="modelValue"
    max-width="760"
    @update:model-value="closeDialog"
  >
    <v-card>
      <v-card-title class="d-flex align-center">
        <v-icon icon="mdi-history" class="mr-2" />
        文章編輯紀錄
      </v-card-title>

      <v-divider />

      <v-card-text>
        <!-- 載入中 -->
        <div v-if="loading" class="text-center py-8">
          <v-progress-circular indeterminate color="primary" />
        </div>

        <!-- 錯誤 -->
        <v-alert v-else-if="errorMessage" type="error" variant="tonal">
          {{ errorMessage }}
        </v-alert>

        <!-- 沒有紀錄 -->
        <v-alert v-else-if="histories.length === 0" type="info" variant="tonal">
          這篇文章目前沒有編輯紀錄
        </v-alert>

        <!-- 編輯紀錄 -->
        <v-expansion-panels v-else>
          <v-expansion-panel
            v-for="(history, index) in histories"
            :key="history.id"
          >
            <v-expansion-panel-title>
              第 {{ histories.length - index }} 次編輯

              <v-spacer />

              <span class="text-caption text-medium-emphasis">
                {{ formatHistoryTime(history.modifyTime) }}
              </span>
            </v-expansion-panel-title>

            <v-expansion-panel-text>
              <div class="text-subtitle-2 mb-1">修改前標題</div>

              <div class="history-box mb-4">
                {{ history.oldTitle || "無" }}
              </div>

              <div class="text-subtitle-2 mb-1">修改後標題</div>

              <div class="history-box mb-4">
                {{ history.newTitle || "無" }}
              </div>

              <div class="text-subtitle-2 mb-1">修改前內容</div>

              <div class="history-box mb-4">
                {{ htmlToPlainText(history.oldContent) }}
              </div>

              <div class="text-subtitle-2 mb-1">修改後內容</div>

              <div class="history-box">
                {{ htmlToPlainText(history.newContent) }}
              </div>
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-card-text>

      <v-card-actions>
        <v-spacer />

        <v-btn variant="text" @click="closeDialog(false)"> 關閉 </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script setup>
defineProps({
  modelValue: {
    type: Boolean,
    required: true,
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

const closeDialog = (value) => {
  emit("update:modelValue", value);
};
// 將舊歷史 HTML 轉成純文字，避免直接執行 HTML
const htmlToPlainText = (html) => {
  if (!html) return "無";

  const document = new DOMParser().parseFromString(
    html,
    "text/html",
  );

  return document.body.textContent?.trim() || "無";
};

const formatHistoryTime = (time) => {
  if (!time) return "";

  return new Date(time).toLocaleString("zh-TW");
};
</script>

<style scoped>
.history-box {
  padding: 12px;
  border: 1px solid
    rgba(var(--v-border-color), var(--v-border-opacity));
  border-radius: 6px;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
  background: rgba(var(--v-theme-surface-variant), 0.35);
}
</style>
