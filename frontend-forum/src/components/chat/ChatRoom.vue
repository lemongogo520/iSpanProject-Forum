<template>
    <div class="chat-room">
        <ChatHeader :friend="friend"></ChatHeader>
        <MessageList :friend="friend" :conversation="conversations"></MessageList>
        <MessageInput @send="sendMessage"/>
    </div>
</template>
<script setup>
import { useChatStore } from '@/stores/chat.js';
import ChatHeader from './ChatHeader.vue';
import MessageInput from './MessageInput.vue';
import MessageList from './MessageList.vue';
import { storeToRefs } from 'pinia';
defineProps({
    friend:{
        type: Object,
        required:true
    }
})

const chatStore = useChatStore();  
const {conversations} = storeToRefs(chatStore);
const sendMessage = (content)=>{
    console.log('Chatroom收到:',content);
    chatStore.sendMessage(content);
}
</script>
<style>
.chat-room{
    display: flex;
    flex-direction: column;
    border: 3px solid var(--color-text);
    border-radius: 8px;
    box-sizing: border-box;
    background: var(--color-card);
    position: fixed;
    right: 24px;
    bottom: 24px;
    width: 360px;
    height: 500px;
    z-index: 1000;
    background-color: var(--color-background);
    
}
.chat-header {
    flex-shrink: 0;
}


.message-input {
    flex-shrink: 0;
    padding: 0px 15px;
}
</style>