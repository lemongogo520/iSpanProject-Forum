<template>
    <div class="message-input">
        <v-text-field placeholder="輸入文字..." variant="outlined"
        v-model="message"

        prepend-inner-icon="mdi-plus"
        @click:prepend-inner="addFile"

        append-inner-icon="mdi-send" 
        @click:append-inner="sendMessage"
        />

    </div>
</template>
<script setup>
import { ref } from 'vue';
import { VTextField } from 'vuetify/components';
const message = ref('');

const emit = defineEmits(['send']);

const addFile = ()=>{
    console.log('點擊+');
}
const sendMessage = ()=>{
    const content = message.value.trim();
    console.log('送出:',content);
    if(!content) return;
    emit('send',content);
    message.value = '';
}
    
</script>
<style>
    .message-input{
        gap: 10px;
    }
</style>