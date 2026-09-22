<template>
    <div class="friends-page">
        <v-card class="friends-card auth-card" rounded="lg" elevation="6">
            <FriendList />
        </v-card>
        <v-card class="friends-card auth-card" rounded="lg" elevation="6">
            <v-card-title class="text-center font-weight-bold pt-4 auth-title">好友資訊</v-card-title>
            <FriendDetail />
        </v-card>
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
    display: grid;
    grid-template-columns: 360px 1fr;
    grid-template-rows: minmax(0, 1fr);
    gap: 24px;
    padding: 24px;
    background: var(--color-background);
    height: calc(100vh - var(--v-layout-top, 64px) - var(--v-layout-bottom, 0px));
    height: calc(100dvh - var(--v-layout-top, 64px) - var(--v-layout-bottom, 0px));
}

.friends-card {
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

@media (max-width: 959px) {
  .friends-page {
    grid-template-columns: 1fr;
    grid-template-rows: none;
    grid-auto-rows: minmax(420px, auto);
    height: auto;
  }
}
</style>