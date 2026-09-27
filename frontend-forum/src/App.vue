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
import { useNotificationCenter } from "@/composables/useNotificationCenter";

const route = useRoute();
const authStore = useAuthStore();
const userStore = useUserStore();
const chatStore = useChatStore();
const friendStore = useFriendStore();
const messageStore = useMessageStore();
const { snackbar, showMessage } = useNotification();
const { push, clear } = useNotificationCenter();
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
// 訊息
const handleIncoming = (msg) => {
  msg.mine = msg.sender?.userId === userStore.uid;
  msg.status = "sent";

  messageStore.addMessage(msg);

  const isOpen = chatStore.currentConversationId === msg.conversationId && !chatStore.isMinimized;
  chatStore.applyIncoming(msg, { isCurrent: isOpen });

  const conversation = chatStore.findConversation(msg.conversationId);
  if (isOpen && !msg.mine && conversation?.type === "DIRECT") {
    messageStore.markAsRead(msg.conversationId);
  }
};
const handleNotification = (n) => {
  const who = n.name || "某位使用者";
  switch (n.type) {
    // 好友邀請
    case "FRIEND_REQUEST":
      showMessage(`${who} 向你送出好友邀請`);
      push({ kind: "friend", icon: "mdi-account-plus", text: `${who} 向你送出好友邀請`, to: "/friends" ,color: "#8bbce0",});
      friendStore.refreshFriendData();
      break;

    case "FRIEND_ACCEPTED":
      showMessage(`${who} 接受了你的好友邀請`);
      push({ kind: "friend", icon: "mdi-account-check", text: `${who} 接受了你的好友邀請`, to: "/friends" ,color: "#8bbce0",});
      friendStore.refreshFriendData();
      break;

    case "FRIEND_REJECTED":
    case "FRIEND_CANCELED":
      friendStore.refreshFriendData();
      break;

    case "FRIEND_REMOVED":
      friendStore.refreshFriendData();
      chatStore.getConversations();
      break;
    // 發文
    case "BOARD_POST":
      push({
        kind: "board",
        icon: "mdi-note-text",
        text: `${who} 有新文章`,
        to: `/post/${n.postId}`,
        color: "#7fb89b",
      });
      break;
    // 文章留言
    case "POST_REPLIED":
      push({
        kind: "post",
        icon: "mdi-comment-text-outline",
        text: n.title
          ? `${who} 回覆了你的文章「${n.title}」`
          : `${who} 回覆了你的文章`,
        to: `/post/${n.postId}`,
        color: "#c9a97e",
      });
      break;
    // 留言留言
    case "COMMENT_REPLIED":
      push({
        kind: "post",
        icon: "mdi-comment-arrow-left-outline",
        text: `${who} 回覆了你的留言`,
        to: `/post/${n.postId}`,
        color: "#c9a97e",
      });
      break;

    default:
      return;

  }
};

const resync = async () => {
  await chatStore.getConversations();
  await friendStore.refreshFriendData();
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
        onNotification: handleNotification,
        onReconnect: resync,
        onGiveUp: () => {
          showMessage("連線中斷,請重新整理", "error");
          // authStore.isLogin = false;
        },
      });
    } else {
      disconnectSocket();
      friendStore.reset();
      chatStore.reset();
      messageStore.reset();
      clear();
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