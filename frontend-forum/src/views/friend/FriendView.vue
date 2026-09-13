<template>
    <div>
        <FriendRequestList></FriendRequestList>
        <FriendList></FriendList>
    </div>
</template>
<script setup>
import FriendList from '@/components/friend/FriendList.vue';
import FriendRequestList from '@/components/friend/FriendRequestList.vue';
import { useFriendStore } from '@/stores/friend';
import { onMounted } from 'vue';

const friendStore = useFriendStore();

onMounted(()=>{
    friendStore.refreshFriendData()
})
    
</script>
<style src="@/assets/css/common.css"></style>
<style>
    .title{
    padding-left: 40px;
    color: var(--color-title);
    font-weight: 900;
}
</style>