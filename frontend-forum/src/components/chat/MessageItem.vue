<template>
    <div class="msg-row" :class="{ 'mine': props.message.mine }">
        <div class="bubble" :class="{
            failed: message.status === 'failed',
            pending: message.status === 'sending'
        }">
            <div class="bubble-text">
                {{ message.content }}
            </div>
            <div class="bubble-time">
                <v-icon v-if="message.status === 'sending'" size="12">mdi-clock-outline</v-icon>
                <template v-else>
                    {{ formatDate(message.createTime) }}
                </template>
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

const messageStore = useMessageStore();
const props = defineProps({
    message: {
        type: Object,
        required: true
    },
    conversationId: {
        type: Number,
        required: true
    }
})
const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const now = new Date();

    const year = String(date.getFullYear())
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')

    const isToday =
        date.getFullYear() === now.getFullYear() &&
        date.getMonth() === now.getMonth() &&
        date.getDate() === now.getDate();
    if (isToday) {
        return `${hour}:${minute}`
    }

    const yesterday = new Date();
    yesterday.setDate(now.getDate() - 1);
    const isYesterday =
        date.getFullYear() === yesterday.getFullYear() &&
        date.getMonth() === yesterday.getMonth() &&
        date.getDate() === yesterday.getDate();
    if (isYesterday) {
        return `昨天 ${hour}:${minute}`
    }

    return `${year}/${month}/${day}, ${hour}:${minute}`
}



</script>
<style>
.msg-row {
    display: flex;
}

.msg-row.mine {
    justify-content: flex-end;
    align-items: center;
    gap: 2px;
}

.msg-row.mine .failed-actions {
    order: -1;
    display: flex;
}

.bubble.pending {
    opacity: .6;
}

.bubble.failed {
    outline: 1px solid rgb(var(--v-theme-error));
    opacity: .8;
}

.bubble {
    max-width: 75%;
    padding: 6px 10px;
    border-radius: 12px;
    background: rgb(var(--v-theme-surface-light));
    color: rgb(var(--v-theme-on-surface-light));
    word-break: break-word;
}

.msg-row.mine .bubble {
    background: rgb(var(--v-theme-primary));
    color: rgb(var(--v-theme-on-primary));
}

.bubble-text {
    font-size: .9rem;
    white-space: pre-wrap;
}

.bubble-time {
    font-size: .7rem;
    opacity: .7;
    text-align: right;
    margin-top: 2px;
}
</style>