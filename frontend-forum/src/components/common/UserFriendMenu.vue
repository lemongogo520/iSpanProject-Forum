<template>
    <v-menu v-model="open" location="bottom end" origin="top start" :close-on-content-click="false" max-width="280">
        <template #activator="{ props: menuProps }">
            <slot :props="menuProps"></slot>
        </template>
        <v-card class="quick-card" rounded="lg" elevation="8">

            <v-list v-if="relation.status !== 'self'" density="compact" nav>
                <v-list-item v-if="relation.status === 'none'" prepend-icon="mdi-account-plus" title="加好友"
                    :disabled="busy" @click="addFriend" />
                <v-list-item v-else-if="relation.status === 'sent'" prepend-icon="mdi-clock-outline" title="已送出邀請"
                    :disabled="busy" @click="cancel" />
                <v-list-item v-else-if="relation.status === 'received'" prepend-icon="mdi-account-check" title="接受好友邀請"
                    :disabled="busy" @click="accept" />
                <v-list-item v-else-if="relation.status === 'friend'" prepend-icon="mdi-message-text" title="傳送私訊"
                    :disabled="busy" @click="startChat" />
            </v-list>
        </v-card>
    </v-menu>
</template>
<script setup>
import { useChatStore } from '@/stores/conversation';
import { useFriendStore } from '@/stores/friend';
import { useUserStore } from '@/stores/user';
import { computed, ref, watch } from 'vue';

const props = defineProps({
    userId: { type: String, required: true },
    name: { type: String, default: "" },
});

const friendStore = useFriendStore();
const chatStore = useChatStore();
const userStore = useUserStore();

const open = ref(false);
const busy = ref(false);
const snackbar = ref({ show: false, text: "", color: "success" });

const relation = computed(() => friendStore.relationOf?.(props.userId, userStore.uid)) ?? { status: "unknown" };

// 打開時重新撈邀請列表
watch(open, (v) => {
    if (v) {
        friendStore.getSendRequestList();
        friendStore.getRequestList();
    }
});

const notify = (text, color = "success") => {
    snackbar.value = { show: true, text, color };
}

const run = async (fn, okText) => {
    if (busy.value) return;
    busy.value = true;
    try {
        await fn();
        if (okText) notify(okText);
    } catch (error) {
        notify(error.response?.data?.message ?? "操作失敗", "error");
    } finally {
        busy.value = false;
    }
}

const addFriend = () =>
    run(() => friendStore.sendFriendRequest(props.userId), "已送出好友邀請");
const accept = () =>
    run(() => friendStore.acceptFriendRequest(relation.value.friendshipId), "已接受好友邀請");
const cancel = () =>
    run(() => friendStore.cancelFriendRequest(relation.value.friendshipId), "已取消好友邀請");
const startChat = () =>
    run(() => {
        const c = chatStore.directConversation(props.userId);
        chatStore.openChat(c);
        open.value = false;
    });
</script>
<style></style>