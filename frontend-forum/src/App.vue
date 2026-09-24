<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { RouterView, useRoute } from "vue-router";
import { useNotification } from "@/composables/useNotification";
import { useAuthStore } from "@/stores/auth";
import { useChatStore } from "./stores/conversation";
import { useFriendStore } from "./stores/friend";
import HomeHeader from "@/components/home/HomeHeader.vue";
import FriendSidebar from "@/views/friend/FriendSidebar.vue";
import ChatDock from "@/views/chat/ChatDock.vue";
import { useMessageStore } from "./stores/message";
import { connectSocket, disconnectSocket } from "./services/socket";
import { useUserStore } from "./stores/user";
import { useConfigStore } from '@/stores/config'

const route = useRoute();
const authStore = useAuthStore();
const userStore = useUserStore();
const chatStore = useChatStore();
const friendStore = useFriendStore();
const messageStore = useMessageStore();
const { snackbar, showMessage } = useNotification();
const configStore = useConfigStore()

const hideHeaderPaths = ["/login", "/register"];
const showHeader = computed(() => {
  return (
    !hideHeaderPaths.includes(route.path) && !route.path.startsWith("/config")
  );
});

// =========== 載入index.html title =================
onMounted(async () => {
  await configStore.loadSettings()
})

watch(
  () => configStore.settings.web_site_name,
  name => {
    if (name) {
      document.title = name
    }
  },
  { immediate: true }
)

// =========== websocket =================
const handleIncoming = (msg) => {
  msg.mine = msg.sender?.userId === userStore.uid;
  msg.status = "sent";

  messageStore.addMessage(msg);

  const isOpen = chatStore.currentConversationId === msg.conversationId && !chatStore.isMinimized;
  chatStore.applyIncoming(msg, { isCurrent: isOpen });

  if (isOpen && !msg.mine) messageStore.markAsRead(msg.conversationId);
};

const resync = async () => {
  await chatStore.getConversations();
  if (chatStore.currentConversationId) {
    messageStore.getMessages(chatStore.currentConversationId);
  }
};


watch(
  () => authStore.isLogin,
  async (loggedIn) => {
    if (loggedIn) {
      friendStore.getFriendList();
      await chatStore.getConversations();
      chatStore.restoreWindow();
      connectSocket({
        onMessage: handleIncoming,
        onReconnect: resync,
        onGiveUp: () => {
          showMessage("連線中斷,請重新整理", "error");
          // authStore.isLogin = false;
        },
      });
    } else {
      disconnectSocket();
      console.log("reset");
      friendStore.reset();
      chatStore.reset();
      messageStore.reset();
    }
  },
  { immediate: true }
);

</script>

<template>
  <v-app class="app-layout">
    <HomeHeader v-if="showHeader && authStore.checked && authStore.isLogin" />

    <v-main>
      <RouterView />
    </v-main>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000" location="top">
      {{ snackbar.message }}

      <template #actions>
        <v-btn variant="text" @click="snackbar.show = false">關閉</v-btn>
      </template>
    </v-snackbar>

    <!-- 好友側邊欄 -->
    <FriendSidebar v-if="showHeader && authStore.checked && authStore.isLogin" />
    <ChatDock v-if="showHeader && authStore.checked && authStore.isLogin" />
  </v-app>
</template>

<style></style>