<template>
    <div class="msg-input">
        <v-textarea v-model="text" placeholder="輸入訊息…" rows="1" max-rows="4" auto-grow hide-details density="compact"
            variant="solo-filled" flat @keydown.enter.exact.prevent="send" />
        <v-btn icon="mdi-send" variant="text" color="primary" :loading="messageStore.sending" :disabled="!text.trim()"
            @click="send" />
    </div>
</template>
<script setup>
import { ref } from 'vue';
import { useMessageStore } from '@/stores/message';

const props = defineProps({
    conversationId: {
        type: Number,
        required: true
    },
});

const messageStore = useMessageStore();
const text = ref("");

const send = () => {
    async () => {
        const content = text.value.trim();
        if (!content || messageStore.sending) return;
        text.value = "";
        try {
            await messageStore.sendMessage(props.conversationId, content);
        } catch {
            text.value = content;                // 失敗時還原，避免使用者重打
        }
    }
};

</script>
<style scoped>
.msg-input {
    display: flex;
    align-items: flex-end;
    gap: 4px;
    padding: 8px;
    flex-shrink: 0;
    border-top: 1px solid rgb(var(--v-border-color), var(--v-border-opacity));
}

.msg-input :deep(.v-field) {
    font-size: .9rem;
}
</style>
