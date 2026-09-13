<template>
    <p class="title">交友邀請</p>
    <div class="requests_list">
        <div v-if="requestList.length === 0" class="empty-text">
            目前沒有好友邀請
        </div>
        <div v-else class="request-scroll">
            <div v-for="request in requestList" :key="request.id" class="request-item">
                <FriendItem  :friend="request.friend">
                </FriendItem>
                <div class="action-btn">
                    <span v-if="processing[request.id] === 'accepted'" class="result-text">已接受</span>
                    <span v-else-if="processing[request.id] === 'rejected'" class="result-text">已拒絕</span>

                    <template v-else>
                        <v-btn class="accept-btn" :disabled="!!processing[request.id]" 
                        @click="acceptRequest(request.id)">
                            {{ processing[request.id] === 'accepting' ? '處理中...' : '接受' }}
                        </v-btn>
                        <v-btn class="reject-btn" :disabled="!!processing[request.id]" 
                        @click="rejectRequest(request.id)">
                            {{ processing[request.id] === 'rejecting' ? '處理中...' : '拒絕' }}
                        </v-btn>
                    </template>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup>
import FriendItem from '@/components/friend/FriendItem.vue';
import { onMounted, ref } from 'vue';
import { useFriendStore } from '@/stores/friend';
import { storeToRefs } from 'pinia';

const friendStore = useFriendStore();
const { requestList } = storeToRefs(friendStore);
const { getRequestList, acceptFriendRequest, rejectFriendRequest ,getFriendList} = friendStore;

const processing = ref({});
const acceptRequest = async (friendshipId) => {
    processing.value[friendshipId] = 'accepting';
    try {
        await acceptFriendRequest(friendshipId);
        await getFriendList();
        processing.value[friendshipId] = 'accepted';
    } catch (error) {
        delete processing.value[friendshipId];
        console.error('接受好友邀請失敗：', error);
    }
}

const rejectRequest = async (friendshipId) => {
    processing.value[friendshipId] = 'rejecting';

    try {
        await rejectFriendRequest(friendshipId);
        await getFriendList();
        processing.value[friendshipId] = 'rejected';
    } catch (error) {
        delete processing.value[friendshipId];
        console.error('拒絕好友邀請失敗：', error);
    }
}


onMounted(() => {
    getRequestList();
})

</script>

<style>
.empty-text {
    padding: 24px;
    text-align: center;
    color: var(--color-text);
    font-size: 14px;
}

.requests_list {
    width: 100%;
}

.request-scroll {
    display: flex;
    gap: 12px;
    overflow-x: auto;
    width: 100%;
    padding: 4px 2px 12px;
    box-sizing: border-box;
}


.request-item{
    flex: 0 0 200px;
    padding: 12px;
    border: 1px solid var(--color-border);
    border-radius: 8px;
    box-sizing: border-box;
    background: var(--color-card);
}

.result-text {
    margin-right: 20px;
    align-items: center;
    justify-content: center;
    color: var(--color-text);
    font-weight: 600;
}
.action-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
}

/* 接受 */
.accept-btn {
    background: var(--color-primary);
    color: white;
}

/* 拒絕 */
.reject-btn {
    background-color: #d97979;
    color: white;
}

.accept-btn:hover,
.reject-btn:hover {
    background: #fff1f1;
    color: #d97979;
}

</style>