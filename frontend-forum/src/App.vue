<script setup>
import { computed } from "vue";
import { RouterView, useRoute } from "vue-router";
import HomeHeader from "@/components/home/HomeHeader.vue";
import { useNotification } from "@/composables/useNotification";

const route = useRoute();
const { snackbar } = useNotification();

const hideHeaderPaths = ["/login", "/register"];
const showHeader = computed(() => !hideHeaderPaths.includes(route.path));
</script>

<template>
  <v-app>
    <HomeHeader v-if="showHeader" />

    <v-main>
      <RouterView />
    </v-main>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000" location="top">
      {{ snackbar.message }}

      <template #actions>
        <v-btn variant="text" @click="snackbar.show = false">關閉</v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>