<template>
    <p>你的朋友</p>
    <div class="friends-list">
        <FriendItem 
            v-for="friend in friends" 
            :key="friend.id" :friend="friend">
        </FriendItem>
    </div>
</template>
<script setup>
import httpClient from '@/services/httpClient';
import { onMounted, ref } from 'vue';
import FriendItem from '@/components/friend/FriendItem.vue';

const friends = ref([])

onMounted(()=>{
    getFriendList()
})
function getFriendList(){
    httpClient({
        method: 'get',
    
        url: '/api/friendship/list'
    })    
        .then(response => {
            // console.log(response)
            friends.value = response.data
        })
    
        .catch(error => console.log(error));


    
}
</script>
<style>
</style>