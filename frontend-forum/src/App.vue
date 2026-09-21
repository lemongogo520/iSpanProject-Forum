<script setup>
import { computed, onMounted, ref } from "vue";
import { RouterView, useRoute } from "vue-router";
import { useNotification } from "@/composables/useNotification";
import { useAuthStore } from "@/stores/auth";
import { useChatStore } from "./stores/conversation";
import { useFriendStore } from "./stores/friend";
import HomeHeader from "@/components/home/HomeHeader.vue";
import FriendSidebar from "@/views/friend/FriendSidebar.vue";
import ChatDock from "@/views/chat/ChatDock.vue";

const route = useRoute();
const authStore = useAuthStore();
const chatStore = useChatStore();
const friendStore = useFriendStore();
const { snackbar } = useNotification();

const hideHeaderPaths = ["/login", "/register"];
const showHeader = computed(() => {
  return (
    !hideHeaderPaths.includes(route.path) && !route.path.startsWith("/config")
  );
});

onMounted(() => {
  friendStore.getFriendList();
  chatStore.getConversations();
  chatStore.restoreWindow();
});

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
    <FriendSidebar v-if="showHeader && authStore.checked && authStore.isLogin"/>
    <ChatDock v-if="showHeader && authStore.checked && authStore.isLogin"/>
  </v-app>
</template>

<style></style>