<template>
    <div class="message-list">
        <MessageItem v-for="message in messages" :key="message.id" :message="message"></MessageItem>
    </div>
</template>
<script setup>
import { useMessageStore } from '@/stores/message.js';
import MessageItem from './MessageItem.vue';
import { storeToRefs } from 'pinia';
const messageStore = useMessageStore();
const {messages} = storeToRefs(messageStore);
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