<script setup>
import { RouterView } from 'vue-router'
import { useNotification } from "@/composables/useNotification";

const { snackbar } = useNotification();
</script>

<template>
   <v-app>
    <!-- v-main 是 Vuetify 的主內容區塊，會自動幫你計算導覽列、側邊欄的推擠空間 -->
    <v-main>
      <!-- RouterView 負責把你在 router/index.js 設定的頁面（如 Home.vue）渲染在這裡 -->
      <RouterView />
    </v-main>
        <!-- 共用通知，不會因切換頁面而消失 -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
      location="top"
    >
      {{ snackbar.message }}

      <template #actions>
        <v-btn variant="text" @click="snackbar.show = false">
          關閉
        </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>


