<template>
    <v-btn class="friend-chat-button"
    icon="mdi-message"
    @click="openList"
    />
    <ConversationList
    v-if="condition"
    />

</template>
<script setup>
import { VBtn } from 'vuetify/components';
import ConversationList from './ConversationList.vue';

    
</script>
<style>
    
</style>