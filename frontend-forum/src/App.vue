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
  return !hideHeaderPaths.includes(route.path) 
  && !route.path.startsWith("/config"); 
  });

const isFriendSidebarOpen = ref(false)

</script>

<template>
  <v-app>
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
    <div
        class="friend-sidebar-wrapper"
        :class="{ open: isFriendSidebarOpen }"
    >
        <button v-if="!isFriendSidebarOpen"
          class="friend-button"
          @click="isFriendSidebarOpen = !isFriendSidebarOpen">
        好友
        </button>
        <FriendSidebar v-if="showHeader && authStore.checked && authStore.isLogin"
          :isOpen="isFriendSidebarOpen" @close="isFriendSidebarOpen = false"
        />
    </div>
  </v-app>
</template>

<style>
.friend-sidebar-wrapper {
    position: fixed;
    top: 64px;
    right: 0;

    width: 280px;
    height: calc(100vh - 64px);

    transform: translateX(100%);
    transition: transform 0.25s ease;

    z-index: 900;
}

.friend-sidebar-wrapper.open {
    transform: translateX(0);
}

.friend-button {
    position: absolute;

    left: -64px;
    bottom: 20px;

    width: 64px;
    height: 52px;

    border: 3px solid #5f91b5;
    border-right: none;
    border-radius: 10px 0 0 10px;

    background: #8bbce0;
    color: white;

    font-size: 15px;
    font-weight: 700;

    box-shadow: 0 4px 0 #5f91b5;

    cursor: pointer;

    transition: 0.1s;
}

.friend-button:hover {
    background: #9fc9e5;
}
</style>