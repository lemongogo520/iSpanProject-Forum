<template>
  <v-dialog v-model="open" max-width="520">
    <template #activator="{ props: activatorProps }" class="justify-center">
      <v-btn v-bind="activatorProps" prepend-icon="mdi-account-plus" variant="tonal" size="small">
        新增好友
      </v-btn>
    </template>

    <v-card class="pa-4" :loading="sending">
      <GetAllUsers ref="picker" :types="[0]" :exclude-ids="excludeIds" @select="onSelect" />
    </v-card>
  </v-dialog>
</template>
<script setup>
import { computed, ref } from "vue";
import { useFriendStore } from "@/stores/friend";

import GetAllUsers from "@/components/user/GetAllUsers.vue";
import { useUserStore } from "@/stores/user";

import { useNotification } from "@/composables/useNotification";   // 依實際路徑

const { showMessage } = useNotification();

const friendStore = useFriendStore();
const userStore = useUserStore();

const open = ref(false);
const picker = ref(null);
const sending = ref(false);

const excludeIds = computed(() =>
  [
    userStore.uid,
    ...(friendStore.friendList ?? []).map((f) => f.userId),
    ...(friendStore.sendRequestList ?? []).map((r) => r.friend?.userId),
    ...(friendStore.requestList ?? []).map((r) => r.friend?.userId),
  ].filter(Boolean)
);

const onSelect = async (user) => {
  if (!user || sending.value) return;
  sending.value = true;
  try {
    console.log(user)
    await friendStore.sendFriendRequest(user.uid);
    picker.value?.clearUser();
    open.value = false;
    showMessage(`已送出邀請給 ${user.name || user.account}`);
  } catch (e) {
    console.error("onSelect 失敗", e);
    showMessage(e.response?.data?.message ?? "送出失敗", "error");
  } finally {
    sending.value = false;
  }
};
</script>