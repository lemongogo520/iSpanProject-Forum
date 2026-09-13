<template>
    <p class="title">你的朋友</p>
    <div class="friends-list">

        <div v-if="friendList.length === 0" class="empty-text">
            目前沒有好友
        </div>
        <template v-else>
            <FriendItem 
            v-for="friend in friendList" 
            :key="friend.userId" :friend="friend"
            @click="emit('select-friend',friend)">
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

const emit = defineEmits(['select-friend'])

onMounted(()=>{
    getFriendList();
})

</script>
<style>

.friends-list{
    padding-left: 24px;
    gap: 10px;
}
</style>