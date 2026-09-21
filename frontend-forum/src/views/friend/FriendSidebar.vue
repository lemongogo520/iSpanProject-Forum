< <template>
  <aside class="friend-sidebar">
    <div class="sidebar-title">好友</div>

    <v-list density="compact" nav>
      <v-list-item v-for="f in friendStore.friendList" :key="f.userId" @click="startChat(f)">
        <template #prepend>
          <DollAvatar :userId="f.userId" :scale="1.5" :size="32" />
        </template>
        <v-list-item-title>{{ f.displayName }}</v-list-item-title>
      </v-list-item>

      <v-list-item v-if="!friendStore.friendList.length">
        <v-list-item-subtitle>尚無好友</v-list-item-subtitle>
      </v-list-item>
    </v-list>
  </aside>
</template>
  <script setup>
  import { useChatStore } from "@/stores/conversation";
  import { useFriendStore } from "@/stores/friend";

  const friendStore = useFriendStore();
  const chatStore = useChatStore();
  const startChat = async (friend) => {
    try {
      const c = await chatStore.directConversation(friend.userId);
      chatStore.openChat(c.id);
    } catch (e) {
      console.error("開啟私訊失敗", e);
    }
  };
</script>
  <style>
  .friend-sidebar {
    position: fixed;
    right: 0;
    top: 64px;
    bottom: 0;
    width: 240px;
    overflow-y: auto;
    border-left: 1px solid rgb(var(--v-border-color), var(--v-border-opacity));
    background: rgb(var(--v-theme-surface));
    z-index: 900;
  }

  .sidebar-title {
    padding: 12px 16px;
    font-weight: 600;
  }
</style>
