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
                <div class="request-actions">
                    <span v-if="processing[request.id] === 'accepted'" class="btn result">已接受</span>
                    <span v-else-if="processing[request.id] === 'rejected'" class="btn result">已拒絕</span>

                    <template v-else>
                        <v-btn class="btn accept" :disabled="!!processing[request.id]" 
                        @click="acceptRequest(request.id)">
                            {{ processing[request.id] === 'accepting' ? '處理中...' : '接受' }}
                        </v-btn>
                        <v-btn class="btn reject" :disabled="!!processing[request.id]" 
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

.requests_list {
    width: 100%;
    flex-direction: column;
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
    border: 2px solid var(--color-border);
    border-radius: 8px;
    box-sizing: border-box;
    background: var(--color-card);
    /* background: #62a6d3; */
}

.request-actions{
    display: flex;
    align-items: center;
    justify-content: center;
}

.btn {
    padding: 8px 16px;
    margin: 8px;

    border-radius: 10px;
    border: 2px solid;
    color: white;

    font-weight: 700;
    cursor: pointer;

    transition: 0.1s;
}
/* 接受 */
.btn.accept{
    background: #8bbce0;
    box-shadow: 0 3px 0 #5f91b5;
    border-color: #5f91b5; 
}
.btn.accept:hover{
    background: #a6cce8;
}

/* 拒絕  */
.btn.reject {
    background: #d99a9a;
    box-shadow: 0 3px 0 #b97878;
    border-color: #b97878;
}

.btn.reject:hover{
    background: #e2aaaa;
}
/* 按下 */
.btn:active{
    transform: translateY(3px);
    box-shadow: 0 0 0 currentColor;
}

/* 已處理 */
.btn.result {
    background: #b8b8b8;
    border-color: #8f8f8f;
    box-shadow: 0 3px 0 #8f8f8f;

    cursor: default;
}




</style>