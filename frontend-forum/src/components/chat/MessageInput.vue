<template>
  <div v-if="locked" class="msg-locked">
    <v-icon size="18">mdi-lock-outline</v-icon>
    <span>已不是好友，無法繼續傳送訊息</span>
  </div>
  <div v-else class="msg-input">
    <v-textarea v-model="text" placeholder="輸入訊息…" rows="1" max-rows="4" auto-grow hide-details density="compact"
      variant="solo-filled" flat @keydown.enter.exact.prevent="send" />
    <v-btn icon="mdi-send" variant="text" color="primary" :disabled="!text.trim()" 
    @click="send()" />
  </div>
</template>
<script setup>
import { ref } from "vue";
import { useMessageStore } from "@/stores/message";
import { useNotification } from "@/composables/useNotification";
const { showMessage } = useNotification();

const props = defineProps({
  conversationId: {
    type: Number,
    required: true,
  },
  locked: { type: Boolean, default: false },
});

const messageStore = useMessageStore();
const text = ref("");

const send = async () => {
  const content = text.value.trim();
  if (!content) return;
  text.value = "";
  messageStore.sendMessage(props.conversationId, content)
  .catch((e) => { 
      showMessage(e.response?.data?.message ?? "送出失敗", "error");
    });
};
</script>
<style scoped>
.msg-input {
  display: flex;
  align-items: flex-end;
  gap: 4px;
  padding: 8px;
  flex-shrink: 0;
  background: var(--color-card);
  border-top: 1px solid var(--color-border);
}

.msg-input :deep(.v-field) {
  font-size: 0.9rem;
}

.msg-locked {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  flex-shrink: 0;
  border-top: 1px solid var(--color-border);
  background: var(--color-background-light);
  color: var(--color-text);
  font-size: 0.85rem;
}
</style>
