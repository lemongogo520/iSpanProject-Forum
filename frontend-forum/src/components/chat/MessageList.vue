<template>
  <div ref="scroller" class="msg-list" @scroll.passive="onScroll">
    <div v-if="chatStore.loadingOlder" class="loading-older">
      <v-progress-circular size="20" width="2" indeterminate class="mt-1" />
    </div>
    <template v-for="(m, i) in messages" :key="m.clientMsgId ?? m.id">
      <div v-if="!isSameDay(messages[i - 1]?.createTime, m.createTime)" class="date-divider">
        <span>{{ dateLabel(m.createTime) }}</span>
      </div>
      <MessageItem :message="m"
        :conversation-id="conversationId"
        :show-sender="showSender"
        :show-avatar="isFirstOfGroup(i)"/>
    </template>

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
import { isSameDay, dateLabel } from "@/utils/time"
const chatStore = useChatStore();

const props = defineProps({
  conversationId: {
    type: Number,
    required: true,
  },
  showSender: {
    type: Boolean,
    default: false
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

const isFirstOfGroup = (i) => {
  const cur = messages.value[i];
  const prev = messages.value[i - 1];
  if (!prev) return true;                                    // 第一則
  if (prev.sender?.userId !== cur.sender?.userId) return true;   // 換人了
  if (!isSameDay(prev.createTime, cur.createTime)) return true;  // 跨日（中間有分隔線）
  return false;
};

// ==========
// 訊息捲動 
// ==========
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
    if (len <= prev) return;
    const last = messages.value[len - 1];
    if (last?.status === 'sending' || nearBottom()) scrollToBottom();
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
  background: var(--color-background-light);
}

/* 載入舊訊息的指示器：浮在頂端，不佔高度 */
.loading-older {
  position: sticky;
  top: 0;
  display: flex;
  justify-content: center;
  height: 0;
  overflow: visible;
  z-index: 1;
}

/* 沒有對話 */
.empty {
  margin: auto;
  color: var(--color-text);
  font-size: 0.85rem;
}

/* 日期分隔線 */
.date-divider {
  display: flex;
  justify-content: center;
  margin: 8px 0;
}

.date-divider span {
   font-size: 0.7rem;
  padding: 2px 12px;
  border-radius: 999px;
  background: rgba(var(--v-theme-on-surface), 0.06);
  color: var(--color-text);
}
</style>
