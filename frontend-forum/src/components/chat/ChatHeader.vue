<template>
    <FriendItem :friend="friend"></FriendItem>
</template>
<script setup>
import FriendItem from '@/components/friend/FriendItem.vue';

defineProps({
    friend:{
        type: Object,
        required:true
    }
})
</script>
<style>
    
</style>