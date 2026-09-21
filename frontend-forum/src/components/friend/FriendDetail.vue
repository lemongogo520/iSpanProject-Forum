<template>
  <v-card v-if="selected" flat border class="pa-6">
    <div class="d-flex flex-column align-center">

      <DollAvatar :userId="selected.userId" :scale="1.5" :size="96" />

      <div class="text-h6 mt-4">{{ selected.displayName }}</div>
      <div class="text-body-2 text-medium-emphasis">
        @{{ selected.account }}
      </div>

      <v-btn color="primary" prepend-icon="mdi-message-text" @click="startChat(selected)">
        私訊
      </v-btn>
    </div>
  </v-card>

  <v-card v-else flat border class="pa-6 text-center text-medium-emphasis">
    選擇一位好友查看資訊
  </v-card>
</template>
<script setup>
import { computed } from "vue";
import { useFriendStore } from "@/stores/friend";
import DollAvatar from "@/components/doll/DollAvatar.vue";
import { useChatStore } from "@/stores/conversation";

const friendStore = useFriendStore();
const chatStore = useChatStore();

const selected = computed(() =>
  friendStore.friendList.find(f => f.userId === friendStore.selectedFriendId) ?? null
);

const startChat = async (friend) => {
  const c = await chatStore.directConversation(friend.userId);
  chatStore.openChat(c.id);
};
</script>