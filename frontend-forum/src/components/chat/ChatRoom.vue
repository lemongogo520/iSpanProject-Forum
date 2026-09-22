<template>
    <div class="chat-window"
        :class="{ minimized: chatStore.isMinimized, fullscreen: smAndDown && !chatStore.isMinimized }">
        <header class="cw-header" @click="chatStore.toggleMinimize()">
            <span class="cw-title">{{ conversation?.title ?? "對話" }}</span>

            <v-badge v-if="chatStore.isMinimized && conversation?.unreadCount" :content="conversation.unreadCount"
                color="error" inline />

            <v-btn :icon="chatStore.isMinimized ? 'mdi-chevron-up' : 'mdi-minus'" size="x-small" variant="text"
                @click.stop="chatStore.toggleMinimize()" />
            <v-btn icon="mdi-close" size="x-small" variant="text" @click.stop="chatStore.closeChat()" />
        </header>

        <template v-if="!chatStore.isMinimized">
            <MessageList :conversationId="conversationId" />
            <MessageInput :conversationId="conversationId" />
        </template>
    </div>
</template>

<script setup>
import { computed, onMounted, watch } from "vue";
import { useDisplay } from "vuetify";
import { useChatStore } from "@/stores/conversation";
import { useMessageStore } from "@/stores/message";
import MessageList from "@/components/chat/MessageList.vue";
import MessageInput from "@/components/chat/MessageInput.vue";

const props = defineProps({
    conversationId: { type: Number, required: true },
});

const chatStore = useChatStore();
const messageStore = useMessageStore();
const { smAndDown } = useDisplay();

const conversation = computed(
    () => chatStore.conversationList.find((c) => c.id === props.conversationId) ?? null
);

onMounted(async () => {
    await messageStore.getMessages(props.conversationId);
    if (!chatStore.isMinimized) {
        await messageStore.markAsRead(props.conversationId);
    }
});

// 從最小化展開時才標已讀
watch(
    () => chatStore.isMinimized,
    (min) => {
        if (!min) messageStore.markAsRead(props.conversationId);
    }
);
</script>
<style scoped>
.chat-window {
    width: 340px;
    height: 500px;
    display: flex;
    flex-direction: column;
    background: rgb(var(--v-theme-surface));
    border: 1px solid rgb(var(--v-border-color), var(--v-border-opacity));
    border-radius: 8px 8px 0 0;
    box-shadow: 0 4px 16px rgba(0, 0, 0, .18);
    overflow: hidden;
}

.chat-window.minimized {
    height: 44px;
}

.chat-window.fullscreen {
    position: fixed;
    inset: 0;
    width: 100%;
    height: 100%;
    border-radius: 0;
}

.cw-header {
    display: flex;
    align-items: center;
    gap: 8px;
    height: 44px;
    flex-shrink: 0;
    padding: 0 4px 0 12px;
    cursor: pointer;
    background: rgb(var(--v-theme-primary));
    color: rgb(var(--v-theme-on-primary));
}

.cw-title {
    flex: 1;
    min-width: 0;
    font-size: .9rem;
    font-weight: 600;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
</style>
