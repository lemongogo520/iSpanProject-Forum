<template>
    <div class="chat-room">
        <ChatHeader :friend="friend"></ChatHeader>
        <MessageList :friend="friend"></MessageList>
        <MessageInput @send="sendMessage"/>
    </div>
</template>
<script setup>
import { useMessageStore } from '@/stores/message.js';
import ChatHeader from './ChatHeader.vue';
import MessageInput from './MessageInput.vue';
import MessageList from './MessageList.vue';
defineProps({
    friend:{
        type: Object,
        required:true
    }
})

const messageStore = useMessageStore();  
const sendMessage = (content)=>{
    console.log('Chatroom收到:',content);
    messageStore.sendMessage(content);
}
</script>
<style>
.chat-room{
    display: flex;
    flex-direction: column;
    height: 100%;
    min-height: 0;
}
.chat-header {
    flex-shrink: 0;
}


.message-input {
    flex-shrink: 0;
}
</style>