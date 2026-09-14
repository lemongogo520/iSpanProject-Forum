<template>
    <div class="message-list">
        <MessageItem v-for="message in messages[1]" :key="message.id" :message="message"></MessageItem>
    </div>
</template>
<script setup>
import { useChatStore } from '@/stores/chat.js';
import MessageItem from './MessageItem.vue';
import { storeToRefs } from 'pinia';
const chatStore = useChatStore();
const {messages} = storeToRefs(chatStore);
defineProps({
    friend:{
        type: Object,
        required:true
    }
})
    
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