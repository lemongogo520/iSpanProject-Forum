<template>
    <div class="empty-text" v-if="messages.length==0">打聲招呼吧!</div>
    <div ref="messageList" class="message-list" v-else>
        <MessageItem 
            v-for="message in messages" 
            :key="message.id" 
            :message="message"/>
    </div>
</template>
<script setup>
import { ref, watch, nextTick } from 'vue';
import { useChatStore } from '@/stores/conversation.js';
import MessageItem from './MessageItem.vue';
import { storeToRefs } from 'pinia';
const chatStore = useChatStore();
const {messages} = storeToRefs(chatStore);
const messageList = ref(null);

const scrollToBottom = async () => {
    await nextTick()

    if (messageList.value) {
        messageList.value.scrollTop =
            messageList.value.scrollHeight
    }
}

// messages 改變時捲到底
watch(
    () => messages,
    () => {
        scrollToBottom()
    },
    { deep: true }
)

    
</script>
<style>
    .message-list{
        flex: 1;
        overflow-y: auto;
        padding: 16px;
        min-height: 0;

        display: flex;
        flex-direction: column;
        gap: 10px;
    }
</style>