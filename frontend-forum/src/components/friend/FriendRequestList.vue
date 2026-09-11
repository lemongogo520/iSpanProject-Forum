<template>
    <p>交友邀請</p>
    <div class="requests_list">
        <FriendItem class="request-item"
            v-for="friend in friendrequests" 
            :key="friend.id" :friend="friend">
        </FriendItem>
        <div class="action-btn">
            <v-btn class="accept-btn"
            @click="acceptRequest(friend.id)">接受</v-btn>
            <v-btn class="reject-btn"
            @click="rejectRequest(friend.id)">拒絕</v-btn>
        </div>
    </div>
</template>
<script setup>
import httpClient from '@/services/httpClient';
import FriendItem from '@/components/friend/FriendItem.vue';
import { onMounted, ref } from 'vue';

const friendrequests = ref([])

onMounted(()=>{
    getFriendRequest()
})
function getFriendRequest(){
    httpClient({
        method: 'get',
        url: '/api/friendship/request/received/list'
    })    
        .then(response => {
            // console.log(response)
            friendrequests.value = response.data
        })
    
        .catch(error => console.log(error));   
}

function acceptRequest(friendshipId){
    console.log(friendshipId);
//    httpClient({
   
//     method: 'patch',
   
//     url: `/api/friendship/request/${friendshipId}/accept`
   
//    })
//     .then(response => console.log(response))
   
//     .catch(error => console.log(error)); 
}

function rejectRequest(friendshipId){
    console.log(friendshipId);
    httpClient({
        method: 'delete',
    
        url: `/api/friendship/request/${friendshipId}`,
    })
        .then(response => console.log(response))
        .catch(error => console.log(error));
}
</script>

<style>
p{
    padding-left: 20px;
    font-size:x-large;
    color: var(--color-title);
    font-weight: 700;
}

.requests_list{
    display: flex;
    gap:20px;
}

.action-btn {
    display: flex;
    align-items: center;
    gap: 8px;
}

/* 接受 */
.accept-btn {

    background: var(--color-primary);
    color: white;
}

/* 拒絕 */
.reject-btn {
    color: #d97979;
    border-color: #e8b5b5;
}

.reject-btn:hover {
    background: #fff1f1;
}
</style>