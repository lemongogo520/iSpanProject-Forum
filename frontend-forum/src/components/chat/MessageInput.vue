<template>
    <div class="message-input">
        <v-text-field placeholder="輸入文字..." variant="outlined"
        v-model="content"
        @keyup.enter="send"

        prepend-inner-icon="mdi-plus"
        @click:prepend-inner="addFile"

        append-inner-icon="mdi-send" 
        @click:append-inner="send"
        />

    </div>
</template>
<script setup>
import { useChatStore } from '@/stores/chat';
import { storeToRefs } from 'pinia';
import { ref } from 'vue';
import { VTextField } from 'vuetify/components';
const chatStore = useChatStore();
const {sendMessage} = chatStore;
const {currentConversation} = storeToRefs(chatStore);
const content = ref('');

const send = ()=>{
    console.log('送出:',content);
    
    if(!content.value.trim()) return;
    sendMessage(currentConversation.value.id,content.value);
    content.value = '';
}
const addFile = ()=>{
    console.log('點擊+');
}
    
</script>
<style>
    .message-input{
        gap: 10px;
    }
</style>