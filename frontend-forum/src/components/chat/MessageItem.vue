<template>
    <div class="message-item">
        {{ message.content }}
    </div>
</template>
<script setup>
defineProps({
    message: {
        type: Object,
        required: true
    }
})
</script>
<style>
    .message-item{
        color: var(--color-title);
        font-weight: 500;
    }
</style>