<template>
    <p class="title">你的朋友</p>
    <div class="friends-list">

        <div v-if="friendList.length === 0" class="empty-text">
            目前沒有好友
        </div>
        <template v-else>
            <FriendItem 
            v-for="friend in friendList" 
            :key="friend.id" :friend="friend">
        </FriendItem>
    </template>
    </div>
</template>
<script setup>
import { onMounted} from 'vue';
import FriendItem from '@/components/friend/FriendItem.vue';
import { storeToRefs } from 'pinia';
import { useFriendStore } from '@/stores/friend';


const friendstore = useFriendStore();
const {friendList} = storeToRefs(friendstore);
const {getFriendList} = friendstore;



onMounted(()=>{
    getFriendList();
})

</script>
<style>
.empty-text {
    padding: 24px;
    text-align: center;
    color: var(--color-text);
    font-size: 14px;
}
.friends-list{
    padding-left: 24px;
    gap: 10px;
}
</style>