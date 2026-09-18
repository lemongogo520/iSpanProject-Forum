<template>
    <div class="chat-room">
        <ChatHeader :friend="friend"></ChatHeader>
        <MessageList></MessageList>
        <MessageInput/>
    </div>
</template>
<script setup>
import { onMounted } from 'vue';
import { storeToRefs } from 'pinia';

import ChatHeader from './ChatHeader.vue';
import MessageInput from './MessageInput.vue';
import MessageList from './MessageList.vue';

import { useChatStore } from '@/stores/conversation.js';

defineProps({
    friend:{
        type: Object,
        required:true
    },
})

const chatStore = useChatStore();  
const {currentConversation} = storeToRefs(chatStore);
const {getMessages} = chatStore;

onMounted(()=>{
    // console.log(currentConversation.value)
    getMessages(currentConversation.value.id);
})


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
    height: 80%;
    z-index: 1000;
    background-color: var(--color-background);
    
}
.chat-header {
    flex-shrink: 0;
    border-radius: 8px;
}


.message-input {
    flex-shrink: 0;
    padding: 0px 15px;
    margin-top: auto;
}
</style>