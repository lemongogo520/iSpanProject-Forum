<template>
  <v-navigation-drawer class="friend-sidebar" location="right" permanent expand-on-hover rail :rail-width="70"
    :width="240">
    <v-list-subheader class="sidebar-title">好友</v-list-subheader>

    <v-list density="compact" nav>
      <v-list-item v-for="f in sortedFriends" :key="f.userId" @click="startChat(f)">
        <template #prepend>
          <FriendAvatar :userId="f.userId" :scale="1.0" :size="32" />
        </template>
        <v-list-item-title class="ml-2">{{ f.displayName }}</v-list-item-title>
        <v-list-item-subtitle v-if="convByUser[f.userId]?.lastMessage" class="ml-3">
          {{ convByUser[f.userId].lastMessage }}
        </v-list-item-subtitle>
        <template #append>
          <v-badge v-if="convByUser[f.userId]?.unreadCount" :content="convByUser[f.userId].unreadCount" color="#e8806f"
            inline />
        </template>
      </v-list-item>

      <v-list-item v-if="!friendStore.friendList.length">
        <v-list-item-subtitle>尚無好友</v-list-item-subtitle>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>
<script setup>
import { useChatStore } from "@/stores/conversation";
import { useFriendStore } from "@/stores/friend";
import DollAvatar from "@/components/doll/DollAvatar.vue";
import FriendAvatar from '@/components/friend/FriendAvatar.vue';
import { computed } from "vue";

const friendStore = useFriendStore();
const chatStore = useChatStore();

const convByUser = computed(() => {
  const map = {};
  for (const c of chatStore.conversationList) {
    if (c.type === "DIRECT" && c.targetUserId) {
      map[c.targetUserId] = c;
    }
  }
  return map;
});

const sortedFriends = computed(() =>
  [...friendStore.friendList].sort((a, b) => {
    const ta = convByUser.value[a.userId]?.updateTime ?? "";
    const tb = convByUser.value[b.userId]?.updateTime ?? "";
    return tb.localeCompare(ta);
  })
);

const startChat = async (friend) => {
  try {
    const c = await chatStore.directConversation(friend.userId);
    chatStore.openChat(c.id);
  } catch (e) {
    console.error("開啟私訊失敗", e);
  }
};
</script>
<style scoped>
.friend-sidebar {
  position: fixed;
  right: 0;
  top: 64px;
  bottom: 0;
  width: 240px;
  overflow-y: auto;
  background: var(--color-background-light);
  border-left: 1px solid var(--color-border);
  z-index: 900;
}

.sidebar-title {
  padding: 12px 16px;
  color: var(--color-title);
  font-weight: 700;
  letter-spacing: 0.02em;
}

.friend-sidebar :deep(.v-list-item) {
  border-radius: 10px;
  color: var(--color-title);
}

.friend-sidebar :deep(.v-list-item:hover) {
  background: var(--color-primary-soft);
}

.friend-sidebar :deep(.v-list-item--active) {
  background: var(--color-primary-soft);
  border-left: 3px solid var(--color-primary);
}

/* 最後訊息的預覽文字 */
.friend-sidebar :deep(.v-list-item-subtitle) {
  color: var(--color-text);
}
</style>
