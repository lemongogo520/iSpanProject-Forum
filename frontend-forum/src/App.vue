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
  <v-app class="app-layout">
    <HomeHeader v-if="showHeader && authStore.checked && authStore.isLogin" />

    <v-main class="main-content" :class="{'open':isFriendOpen}">
      <RouterView />
    </v-main>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000" location="top">
      {{ snackbar.message }}

      <template #actions>
        <v-btn variant="text" @click="snackbar.show = false">關閉</v-btn>
      </template>
    </v-snackbar>

    <!-- 好友側邊欄 -->

    <FriendSidebar v-if="
      showHeader && authStore.checked && authStore.isLogin && isFriendOpen
    " :isOpen="isFriendOpen" />
    <button class="friend-button" @click="isFriendOpen = !isFriendOpen">
      {{ isFriendOpen ? ">" : "<" }} </button>

  </v-app>
</template>

<style>
.main-content{
  transition: margin-right 0.3s ease;
}

.main-content.open {
  margin-right: 352px;
}

.friend-button {
  position: fixed;
  right: 0;
  top: 0;
  width: 32px;
  height: 100%;
  align-items: center;
  justify-content: center;

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

</style>