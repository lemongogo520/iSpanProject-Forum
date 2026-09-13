<template>
    <div class="friend-view">
        <FriendRequestList></FriendRequestList>
        <div class="friend-page">
            <div class="friend-sidebar">
                <FriendList @select-friend=selectFriend></FriendList>
            </div>
            <div class="chat-area">
                <ChatRoom v-if="selectedFriend" :friend=selectedFriend></ChatRoom>
                <div v-else class="empty-text">請選擇一位好友開始聊天</div>
            </div>
        </div>
    </div>
</template>
<script setup>
import ChatRoom from '@/components/chat/ChatRoom.vue';
import FriendList from '@/components/friend/FriendList.vue';
import FriendRequestList from '@/components/friend/FriendRequestList.vue';
import { useFriendStore } from '@/stores/friend';
import { onMounted, ref } from 'vue';

const friendStore = useFriendStore();
const selectedFriend = ref(null);

const selectFriend = (friend) => {
    selectedFriend.value = friend
    // console.log(friend)
}
onMounted(() => {
    friendStore.refreshFriendData()
})

</script>
<style src="@/assets/css/common.css"></style>
<style>
.title {
    padding-left: 40px;
    color: var(--color-title);
    font-weight: 900;
}

.empty-text {
    padding: 24px;
    text-align: center;
    color: var(--color-text);
    font-size: 14px;
}

.friend-view{
    display: flex;
    flex-direction: column;
    height: 100vh;
}

.friend-page {
    display: flex;
    flex: 1;
    min-height: 0;
}

.friend-sidebar {
    width: 30%;
    background-color: rosybrown;
}

.chat-area {
    width: 70%;
    background-color: aquamarine;
    min-height: 0;
    min-width: 0;
}
</style>