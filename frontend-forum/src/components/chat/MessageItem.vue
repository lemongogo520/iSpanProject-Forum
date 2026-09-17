<template>
    <div class="message-row" 
    :class="{'mine': props.message.mine}"
    >
        <div class="message-content">
            {{ message.content }}
        </div>
        <div class="message-time">{{ formatDate(message.createTime) }}</div>
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
    .message-row{
        display: flex;
        align-items: flex-end;
        gap: 8px;
        margin: 8px 0;
        color: var(--color-title);
        font-weight: 500;
    }

    /* 自己 */
    .message-row.mine{
        justify-content: flex-end;
    }
    .message-row.mine .message-content{
        order: 2;
    }
    .message-row.mine .message-time {
        order: 1;
    }

    /* 對方 */
    .message-row:not(.mine) {
        justify-content: flex-start;
    }

    .message-row:not(.mine) .message-content{
        order: 1;
    }

    .message-row:not(.mine) .message-time {
        order: 2;
    }


    .message-content {
        padding: 8px 12px;
        border-radius: 12px;
        background-color: var(--color-card);
    }
    
    .message-time{
        color: var(--color-text);
        font-weight: 400;
        font-size: small;
        margin-top: auto;
    }

</style>