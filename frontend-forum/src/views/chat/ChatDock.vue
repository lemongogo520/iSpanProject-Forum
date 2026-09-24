<template>
    <div class="chat-dock">
        <ChatRoom v-if="chatStore.currentConversationId" :key="chatStore.currentConversationId"
            :conversationId="chatStore.currentConversationId" />
    </div>
</template>
<script setup>
import { useChatStore } from "@/stores/conversation";
import ChatRoom from "@/components/chat/ChatRoom.vue";

const chatStore = useChatStore();
</script>
<style scoped>
.chat-dock {
    position: fixed;
    right: 256px;
    /* 避開 240px 的好友側邊欄 */
    bottom: 0;
    z-index: 1000;
    pointer-events: none;
}

.chat-dock>* {
    pointer-events: auto;
}

@media (max-width: 959px) {
    .chat-dock {
        right: 0;
        left: 0;
    }
}
</style>