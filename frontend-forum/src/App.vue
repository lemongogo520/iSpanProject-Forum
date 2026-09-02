<script setup></script>
import { RouterView } from 'vue-router'
<template>
   <v-app>
    <!-- v-main 是 Vuetify 的主內容區塊，會自動幫你計算導覽列、側邊欄的推擠空間 -->
    <v-main>
      <!-- RouterView 負責把你在 router/index.js 設定的頁面（如 Home.vue）渲染在這裡 -->
      <RouterView />
    </v-main>
  </v-app>
</template>


