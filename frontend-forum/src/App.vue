<script setup>
import { computed, ref } from "vue";
import { RouterView, useRoute } from "vue-router";
import HomeHeader from "@/components/home/HomeHeader.vue";
import { useNotification } from "@/composables/useNotification";
import { useAuthStore } from "@/stores/auth";
import FriendSidebar from "./components/friend/FriendSidebar.vue";

const route = useRoute();
const { snackbar } = useNotification();
const authStore = useAuthStore();

const hideHeaderPaths = ["/login", "/register"];
const showHeader = computed(() => {
  return (
    !hideHeaderPaths.includes(route.path) && !route.path.startsWith("/config")
  );
});

const isFriendOpen = ref(false);
</script>

<template>
  <v-app class="app" :class="{ open: isFriendOpen }">
    <HomeHeader v-if="showHeader && authStore.checked && authStore.isLogin" />

    <v-main class="main">
      <RouterView />
    </v-main>

    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
      location="top"
    >
      {{ snackbar.message }}

      <template #actions>
        <v-btn variant="text" @click="snackbar.show = false">關閉</v-btn>
      </template>
    </v-snackbar>

    <!-- 好友側邊欄 -->
    <div class="friend-wrapper">
      <FriendSidebar
        v-if="
          showHeader && authStore.checked && authStore.isLogin && isFriendOpen
        "
        :isOpen="isFriendOpen"
      />
      <button class="friend-button" @click="isFriendOpen = !isFriendOpen">
        {{ isFriendOpen ? ">" : "<" }}
      </button>
    </div>
  </v-app>
</template>

<style>
.main {
  margin-right: 32px;
  width: calc(100vh-352px);
}

.friend-wrapper {
  position: fixed;
  top: 64px;
  right: 0;

  width: 352px;
  height: 100%;

  transform: translateX(320px);
  z-index: 1000;
}
.friend-wrapper.open {
  transform: translateX(0);
}

.friend-button {
  position: absolute;
  right: 0;
  width: 100%;
  height: 100%;

  background: white;
  color: #8bbce0;
  font-size: 15px;
  font-weight: 700;
  border: none;
  cursor: pointer;

  transition: 0.1s;
}

.friend-button:hover {
  background: #9fc9e5;
}
.app.open .main {
  margin-right: 350px;
}
</style>
