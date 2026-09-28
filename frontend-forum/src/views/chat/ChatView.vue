<template>
    <div class="chat-page">
        <!-- 左：對話列表 -->
        <v-card class="chat-side auth-card" rounded="lg" elevation="6">
            <v-card-title class="text-center font-weight-bold pt-4 auth-title">訊息</v-card-title>
            <div class="chat-side-body">
                <ConversationList />
            </div>
        </v-card>
        <!-- 右：聊天室 -->
        <v-card class="chat-main auth-card" rounded="lg" elevation="6">
            <template v-if="cid">
                <div class="chat-main-head">
                    <DollAvatar v-if="conversation?.type === 'DIRECT'" :userId="conversation?.targetUserId" :scale="1.5"
                        :size="60">
                    </DollAvatar>
                    <v-avatar v-else size="48" color="primary" variant="tonal">
                        <v-icon>mdi-forum</v-icon>
                    </v-avatar>
                    <span class="font-weight-bold text-truncate">{{ conversation?.title ?? "對話" }}</span>
                    <v-spacer />
                    <v-btn icon="mdi-arrow-collapse" size="small" variant="text" title="回到小視窗" @click="collapse" />
                </div>
                <v-divider />
                <MessageList :key="cid" :conversationId="cid" />
                <MessageInput :conversationId="cid" :locked="conversation?.archived === true"  />
            </template>
            <div v-else class="chat-main-empty">
                <v-icon size="48">mdi-message-outline</v-icon>
                <span>選擇一個對話開始聊天</span>
            </div>
        </v-card>
    </div>
</template>
<script setup>
import ConversationList from '@/components/chat/ConversationList.vue';
import MessageInput from '@/components/chat/MessageInput.vue';
import MessageList from '@/components/chat/MessageList.vue';
import DollAvatar from '@/components/doll/DollAvatar.vue';
import { useChatStore } from '@/stores/conversation';
import { useMessageStore } from '@/stores/message';
import { computed, onUnmounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { subscribeConversation, unsubscribeConversation } from "@/services/socket";


const props = defineProps({
    conversationId: { type: String, default: "" },
});

const chatStore = useChatStore();
const messageStore = useMessageStore();
const router = useRouter();

const cid = computed(() => (props.conversationId ? Number(props.conversationId) : null));

const conversation = computed(
    () => chatStore.conversationList.find((c) => c.id === cid.value) ?? null
);
const isBoard = computed(() => conversation.value?.type === "BOARD");

watch(
    () => [cid.value, isBoard.value],
    ([id, board], [oldId]) => {
        if (oldId) unsubscribeConversation(oldId);
        if (id && board) subscribeConversation(id);
    }
);

const collapse = () => {
    const back = sessionStorage.getItem("chatReturnTo");
    sessionStorage.removeItem("chatReturnTo");
    router.push(back ?? "/");
};

onUnmounted(() => {
    if (cid.value) {
        unsubscribeConversation(cid.value);
        messageStore.markAsRead(cid.value);
    }
});
</script>
<style scoped>
.chat-page {
    display: grid;
    grid-template-columns: 340px 1fr;
    grid-template-rows: minmax(0, 1fr);
    gap: 24px;
    padding: 24px;
    background: var(--color-background);
    height: calc(100vh - var(--v-layout-top, 64px) - var(--v-layout-bottom, 0px));
    height: calc(100dvh - var(--v-layout-top, 64px) - var(--v-layout-bottom, 0px));
}

.chat-side,
.chat-main {
    min-height: 0;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.chat-side-body {
    flex: 1;
    min-height: 0;
    overflow-y: auto;
}

.chat-main-head {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    flex-shrink: 0;
}

.chat-main-empty {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 12px;
    color: rgba(var(--v-theme-on-surface), 0.55);
}

@media (max-width: 959px) {
    .chat-page {
        grid-template-columns: 1fr;
    }

    .chat-side {
        display: none;
        /* 手機直接用彈窗，不做兩欄 */
    }
}
</style>