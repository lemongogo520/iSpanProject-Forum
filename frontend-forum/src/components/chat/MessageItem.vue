<template>
    <div class="msg-row" :class="{ 'mine': props.message.mine }">
        <!-- 左側頭像（看板聊天室、別人的訊息） -->
        <template v-if="showSender && !message.mine">
            <DollAvatar v-if="showAvatar"
                :user-id="message.sender?.userId"
                :size="32"
                :scale="0.9"
                class="msg-avatar"/>
            <div v-else class="msg-avatar-placeholder"></div>
        </template>
        <div class="msg-body">
            <div v-if="showSender && !message.mine && showAvatar" class="msg-sender">
                {{ message.sender?.displayName ?? message.sender?.account }}
            </div>
            <div class="bubble" :class="{
                failed: message.status === 'failed',
                pending: message.status === 'sending'
            }">
                <div class="bubble-text">{{ message.content }}</div>
                <div class="bubble-time">
                    <v-icon v-if="message.status === 'sending'" size="12">mdi-clock-outline</v-icon>
                    <template v-else>
                        {{ timeOf(message.createTime) }}
                    </template>
                </div>
            </div>
        </div>
        <div v-if="message.status === 'failed'" class="failed-actions">
            <v-btn icon="mdi-refresh" size="x-small" variant="text" color="error"
                @click="messageStore.retryMessage(conversationId, message.clientMsgId)" />
            <v-btn icon="mdi-close" size="x-small" variant="text"
                @click="messageStore.discardMessage(conversationId, message.clientMsgId)" />
        </div>
    </div>
</template>
<script setup>
import { useMessageStore } from '@/stores/message';
import { timeOf } from "@/utils/time";
import DollAvatar from '@/components/doll/DollAvatar.vue';

const messageStore = useMessageStore();
const props = defineProps({
    message: { type: Object, required: true },
    conversationId: { type: Number, required: true },
    showSender: { type: Boolean, default: false },
    showAvatar: { type: Boolean, default: true },   // 連續訊息時可關掉
})

</script>
<style scoped>
.msg-row {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}
.msg-row.mine {
  justify-content: flex-end;
  align-items: center;
}

.msg-avatar,
.msg-avatar-placeholder {
  flex-shrink: 0;
  width: 32px;
}

.msg-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
  max-width: 75%;
}

.msg-sender {
  font-size: 0.7rem;
  color: var(--color-text);
  padding-left: 4px;
}

.bubble {
    padding: 6px 10px;
    border-radius: 12px;
    background: var(--color-card);
    color: var(--color-title);
    border: 1px solid var(--color-border);
    word-break: break-word;
    overflow-wrap: anywhere;
}

.msg-row.mine .bubble {
  background: var(--color-primary-deep);
  color: #ffffff;
  border-color: transparent;
}
.bubble.pending { opacity: 0.6; }
.bubble.failed {
  outline: 1px solid rgb(var(--v-theme-error));
  opacity: 0.8;
}

.bubble-text {
  font-size: 0.9rem;
  white-space: pre-wrap;
}
.bubble-time {
  font-size: 0.7rem;
  opacity: 0.75;
  text-align: right;
  margin-top: 2px;
}

/* 失敗時的重送／刪除按鈕，放在氣泡左邊 */
.failed-actions {
    order: -1;
    display: flex;
    flex-shrink: 0;
}

</style>