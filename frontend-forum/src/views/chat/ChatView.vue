<template>
    <div class="chat-layout">
        <aside v-if="!route.params.conversationId" class="chat-sidebar">
            <ConversationList />
        </aside>

        <!-- <section class="chat-content">
            <MessageList/>
        </section> -->
    </div>
</template>
<script setup>
import { onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useChatStore } from '@/stores/conversation';
import ConversationList from "@/components/chat/ConversationList.vue";
import MessageList from '@/components/chat/MessageList.vue';

const chatStore = useChatStore();
const route = useRoute();
onMounted(() => {
    chatStore.getConversations();
});
</script>
<style>
.chat-layout {
    display: flex;
    height: 100%;
    overflow: hidden;
}
</style>