<template>
    <v-card flat border>
        <v-tabs v-model="tab" density="compact" grow>
            <v-tab value="friends">好友</v-tab>
            <v-tab value="received">
                邀請
                <v-badge v-if="friendStore.requestList.length" :content="friendStore.requestList.length" color="error"
                    inline />
            </v-tab>
            <v-tab value="sent">已送出</v-tab>
            <AddFriendDialog/>
        </v-tabs>

        <v-window v-model="tab">
            <!-- 好友 -->
            <v-window-item value="friends">
                <v-list-item v-if="friendStore.friendList.length===0">尚無好友</v-list-item>
                <v-skeleton-loader v-else-if="friendStore.loading" type="list-item-avatar" />
                <v-list v-else nav>
                    <v-list-item v-for="f in friendStore.friendList" :key="f.userId"
                        :active="f.userId === friendStore.selectedFriendId" @click="friendStore.selectFriend(f.userId)">
                        <template #prepend>
                            <DollAvatar :userId="f.userId" :scale="1.5" :size="32" />
                        </template>
                        <v-list-item-title>{{ f.displayName }}</v-list-item-title>
                        <v-list-item-subtitle>@{{ f.account }}</v-list-item-subtitle>

                    </v-list-item>
                </v-list>
            </v-window-item>

            <!-- 收到的邀請 -->
            <v-window-item value="received">
                <v-list nav>
                    <v-list-item v-if="friendStore.requestList.length===0">尚無好友邀請</v-list-item>
                    <v-list-item v-for="r in friendStore.requestList" :key="r.id">
                        <template #prepend>
                            <DollAvatar :userId="r.friend.userId" :scale="1.5" :size="32" />
                        </template>
                        <v-list-item-title>{{ r.friend.displayName }}</v-list-item-title>
                        <template #append>
                            <v-btn size="small" color="primary" variant="text" :loading="friendStore.processing[r.id]"
                                @click="friendStore.acceptFriendRequest(r.id)">接受</v-btn>
                            <v-btn size="small" variant="text" :loading="friendStore.processing[r.id]"
                                @click="friendStore.rejectFriendRequest(r.id)">拒絕</v-btn>
                        </template>
                    </v-list-item>
                    
                </v-list>
            </v-window-item>

            <!-- 已送出 -->
            <v-window-item value="sent">
                <v-list nav>
                    <v-list-item v-for="r in friendStore.sendRequestList" :key="r.id">
                        <template #prepend>
                            <DollAvatar :userId="r.friend.userId" :scale="1.5" :size="32" />
                        </template>
                        <v-list-item-title>{{ r.friend.displayName }}</v-list-item-title>
                        <template #append>
                            <v-btn size="small" color="error" variant="text" :loading="friendStore.processing[r.id]"
                                @click="friendStore.cancelFriendRequest(r.id)">取消</v-btn>
                        </template>
                    </v-list-item>
                </v-list>
            </v-window-item>
        </v-window>
    </v-card>
</template>
<script setup>
import { ref } from 'vue';
import { useFriendStore } from '@/stores/friend';
import DollAvatar from "@/components/doll/DollAvatar.vue";
import AddFriendDialog from './AddFriendDialog.vue';
const friendStore = useFriendStore();
const tab = ref("friends");

</script>
<style>
.friend-list {
    display: flex;
    flex-direction: column;

    padding: 10px;

    max-height: 500px;
    overflow-y: auto;
}
</style>