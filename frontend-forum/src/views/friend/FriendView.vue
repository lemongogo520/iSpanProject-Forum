<template>
    <div class="friends-page">
        <div class="friends-left">
            <FriendList />
        </div>
        <div class="friends-right">
            <FriendDetail />
        </div>
    </div>
</template>
<script setup>
import { onMounted } from 'vue';
import { useFriendStore } from '@/stores/friend';
import FriendList from '@/components/friend/FriendList.vue';
import FriendDetail from '@/components/friend/FriendDetail.vue';

const friendStore = useFriendStore();

onMounted(() => {
    friendStore.refreshFriendData();
})

</script>
<style src="@/assets/css/common.css"></style>
<style scoped>
.friends-page {
    display: flex;
    flex-direction: row;
    height: 100%;
    gap: 16px;
    padding: 16px;
}

.friends-left {
    width: 500px;
    flex-shrink: 0;
    overflow-y: auto;
}

.friends-right {
    flex: 1;
    flex-shrink: 0;
    min-width: 0;
}
</style>