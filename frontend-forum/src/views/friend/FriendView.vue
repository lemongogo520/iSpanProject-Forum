<template>
    <div>
        <FriendRequestList></FriendRequestList>
        <FriendList></FriendList>
    </div>
</template>
<script setup>
import FriendList from '@/components/friend/FriendList.vue';
import FriendRequestList from '@/components/friend/FriendRequestList.vue';

    
</script>
<style src="@/assets/css/common.css"></style>
<style>
    p{
    padding-left: 20px;
    color: var(--color-title);
    font-weight: 700;
}
</style>