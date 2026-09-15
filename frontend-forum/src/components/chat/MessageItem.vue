<template>
    <div class="message-item"
        :class="{
            'my-message': props.message.mine,
            'other-message': !props.message.mine
        }">
        <div class="message-content">
            {{ message.content }}
        </div>
    </div>
</template>
<script setup>
const props = defineProps({
    message: {
        type: Object,
        required: true
    }
})
</script>
<style>
    .message-item{
        display: flex;
        margin: 8px 0;
        color: var(--color-title);
        font-weight: 500;
    }
    .my-message {
        justify-content: flex-end;
    }

    .other-message {
        justify-content: flex-start;
    }

    .message-content {
        padding: 8px 12px;
        border-radius: 12px;
        background-color: var(--color-card);
    }
</style>