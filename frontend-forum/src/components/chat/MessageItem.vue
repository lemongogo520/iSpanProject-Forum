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
                    {{ timeOf(message.createTime) }}
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
import { timeOf } from "@/utils/time";

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
    background: var(--color-card);
    color: var(--color-title);
    border: 1px solid var(--color-border);
    word-break: break-word;
}

.msg-row.mine .bubble {
    background: var(--color-primary-deep);
    color: #ffffff;
    border-color: transparent;
}

.bubble-text {
    font-size: .9rem;
    white-space: pre-wrap;
}

.bubble-time {
    font-size: .7rem;
    opacity: 0.75;
    text-align: right;
    margin-top: 2px;
}
</style>