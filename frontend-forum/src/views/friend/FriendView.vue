<template>
    <div class="friend-view">
        <FriendRequestList></FriendRequestList>
        <div class="friend-page">
            <div class="friend-sidebar">
                <FriendList @select-friend=selectFriend></FriendList>
            </div>
            <div class="friend-detail">
                <ChatRoom v-if="selectedFriend && currentConversation" 
                :friend=selectedFriend />
                <div v-else class="empty-text">請選擇一位好友</div>
            </div>
        </div>
    </div>
</template>
<script setup>
import ChatRoom from '@/components/chat/ChatRoom.vue';
import FriendList from '@/components/friend/FriendList.vue';
import FriendRequestList from '@/components/friend/FriendRequestList.vue';
import { useChatStore } from '@/stores/chat';
import { useFriendStore } from '@/stores/friend';
import { onMounted, ref } from 'vue';
import { storeToRefs } from 'pinia';

const friendStore = useFriendStore();
const chatStore = useChatStore();

const {getConversation, getMessages} = chatStore;
const {currentConversation} = storeToRefs(chatStore);

const selectedFriend = ref(null);

const selectFriend = (friend) => {
    selectedFriend.value = friend;
    currentConversation.value = null;
    getConversation(friend.userId);
}
onMounted(() => {
    friendStore.refreshFriendData();
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

}

.friend-detail {
    width: 70%;
    min-height: 0;
    min-width: 0;
}
</style>