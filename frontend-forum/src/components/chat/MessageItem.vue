<template>
    <div class="message-item"
        :class="{
            'my-message': props.message.mine,
            'other-message': !props.message.mine
        }">
        <div class="time-stamp">{{ formatDate(message.createTime) }}</div>
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
const formatDate = (dateString) => {
    if(!dateString) return'';
    const date = new Date(dateString);
    const now = new Date();

    const year = String(date.getFullYear())
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')

    const isToday = 
        date.getFullYear() === now.getFullYear()&&
        date.getMonth() === now.getMonth()&&
        date.getDate() === now.getDate();
    if(isToday){
        return `${hour}:${minute}`
    }

    const yesterday = new Date();
    yesterday.setDate(now.getDate()-1);
    const isYesterday = 
        date.getFullYear() === yesterday.getFullYear()&&
        date.getMonth() === yesterday.getMonth()&&
        date.getDate() === yesterday.getDate();
    if(isYesterday){
        return `昨天 ${hour}:${minute}`
    }

    return `${year}/${month}/${day}, ${hour}:${minute}`
}



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

    .time-stamp{
        color: var(--color-text);
        font-weight: 400;
        font-size: small;
        margin-top: auto;
    }

    .message-content {
        padding: 8px 12px;
        border-radius: 12px;
        background-color: var(--color-card);
    }
</style>