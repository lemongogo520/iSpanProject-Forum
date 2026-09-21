<template>
  <div ref="scroller" class="msg-list" @scroll.passive="onScroll">
    <div v-if="chatStore.loadingOlder" class="loading-older">
      <v-progress-circular size="20" width="2" indeterminate class="mt-1"  />
    </div>

    <MessageItem v-for="message in messages" :key="message.id ?? message.clientMsgId" :message="message" />

    <div v-if="!messages.length && !messageStore.loading" class="empty">
      開始對話吧
    </div>
  </div>
</template>
<script setup>
import { computed, nextTick, ref, watch, onMounted } from "vue";
import { useMessageStore } from "@/stores/message";
import { useChatStore } from "@/stores/conversation.js";
import MessageItem from "@/components/chat/MessageItem.vue";

const props = defineProps({
  conversationId: {
    type: Number,
    required: true,
  },
});

const messageStore = useMessageStore();
const scroller = ref(null);

const messages = computed(
  () => messageStore.messagesMap[props.conversationId] ?? [],
);
const hasMore = computed(
  () => messageStore.cursorMap[props.conversationId]?.hasMore ?? false,
);

const chatStore = useChatStore();

const scrollToBottom = async () => {
  await nextTick();
  const el = scroller.value;
  if (el) el.scrollTop = el.scrollHeight;
};

// 只有使用者已經在底部附近才自動捲，否則會打斷閱讀
const nearBottom = () => {
  const el = scroller.value;
  return !el || el.scrollHeight - el.scrollTop - el.clientHeight < 100;
};

onMounted(scrollToBottom);

// messages 改變時捲到底
watch(
  () => messages.value.length,
  (len, prev) => {
    if (len > prev && nearBottom()) scrollToBottom();
  },
);

const onScroll = async () => {
  const el = scroller.value;
  if (!el || el.scrollTop > 80 || !hasMore.value) return;

  const prevHeight = el.scrollHeight;
  const prevTop = el.scrollTop;
  await messageStore.loadOlderMessages(props.conversationId);
  await nextTick();
  el.scrollTop = prevTop + (el.scrollHeight - prevHeight);
};
</script>
<style scoped>
.msg-list {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-anchor: none;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  position: relative;
}

.loading-older {
  position: sticky;
  top: 0;
  display: flex;
  justify-content: center;
  height: 0;               
  overflow: visible;
  z-index: 1;
}

.empty {
  margin: auto;
  color: rgb(var(--v-theme-on-surface-variant));
  font-size: 0.85rem;
}
</style>
