<template>
  <!-- 滿版容器，深色背景 -->
  <v-container fluid class="fill-height bg-grey-darken-4 pa-6">
    <v-row class="fill-height">
      
      <!-- ========================================== -->
      <!-- 左側：紙娃娃預覽區 (佔 4 欄)                -->
      <!-- ========================================== -->
      <v-col cols="12" md="4" class="d-flex flex-column">
        <v-card theme="dark" class="flex-grow-1 bg-grey-darken-3 d-flex flex-column" rounded="lg" elevation="6">
          <v-card-title class="text-center font-weight-bold text-cyan-accent-3 pt-4">
            外觀預覽
          </v-card-title>
          
          <!-- 預覽畫面框 (未來這裡會放 Canvas 或多層 div 疊加) -->
          <v-sheet 
            class="flex-grow-1 mx-4 mb-4 bg-grey-darken-4 rounded-lg d-flex align-center justify-center border"
            style="border-color: #333 !important;"
          >
            <div class="text-center text-grey-darken-1">
              <v-icon icon="mdi-account" size="120"></v-icon>
              <div class="mt-2">紙娃娃渲染區</div>
            </div>
          </v-sheet>

          <!-- 動作按鈕 -->
          <v-card-actions class="px-4 pb-4 justify-center gap-2">
            <v-btn variant="tonal" color="grey-lighten-1" prepend-icon="mdi-refresh">重置</v-btn>
            <v-btn variant="tonal" color="cyan-accent-3" prepend-icon="mdi-dice-multiple">隨機</v-btn>
            <v-btn variant="flat" color="cyan-accent-3" class="text-grey-darken-4 font-weight-bold" prepend-icon="mdi-content-save">儲存套裝</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>

      <!-- ========================================== -->
      <!-- 右側：部位分類與裝備選擇 (佔 8 欄)           -->
      <!-- ========================================== -->
      <v-col cols="12" md="8" class="d-flex flex-column">
        <v-card theme="dark" class="flex-grow-1 bg-grey-darken-3 d-flex flex-column" rounded="lg" elevation="6">
          
          <!-- 頂部分類頁籤 -->
          <v-tabs v-model="activeTab" color="cyan-accent-3" align-tabs="center" show-arrows class="border-b border-grey-darken-2">
            <v-tab v-for="category in mockCategories" :key="category.id" :value="category.id">
              {{ category.name }}
            </v-tab>
          </v-tabs>

          <!-- 裝備網格區域 (設定 overflow-y-auto 讓內容過多時可向下捲動) -->
          <v-card-text class="flex-grow-1 overflow-y-auto pa-4" style="max-height: calc(100vh - 160px);">
            <v-row>
              <!-- 假裝備列表迴圈 -->
              <v-col v-for="item in mockItems" :key="item" cols="4" sm="3" md="2">
                <v-card 
                  theme="dark" 
                  class="bg-grey-darken-4 pa-2 hover-card" 
                  rounded="lg"
                  ripple
                >
                  <v-sheet class="bg-grey-darken-3 rounded mb-1 d-flex align-center justify-center" height="80">
                    <v-icon icon="mdi-tshirt-crew" color="grey-darken-1" size="36"></v-icon>
                  </v-sheet>
                  <div class="text-caption text-center text-grey-lighten-1">裝備 {{ item }}</div>
                </v-card>
              </v-col>
            </v-row>
          </v-card-text>

        </v-card>
      </v-col>

    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from 'vue';

// 假資料：部位分類
const mockCategories = ref([
  { id: 1, name: '身體 (Body)' },
  { id: 2, name: '頭髮 (Hair)' },
  { id: 3, name: '眼睛 (Eyes)' },
  { id: 4, name: '上衣 (Top)' },
  { id: 5, name: '褲子 (Bottom)' },
  { id: 6, name: '鞋子 (Shoes)' }
]);

const activeTab = ref(1);

// 假資料：產生 24 個假裝備 ID 來撐開畫面
const mockItems = ref(Array.from({ length: 24 }, (_, i) => i + 1));
</script>

<style scoped>
.hover-card {
  transition: all 0.2s ease;
  cursor: pointer;
  border: 1px solid transparent;
}
.hover-card:hover {
  transform: translateY(-2px);
  border-color: #00d2ff;
  box-shadow: 0 4px 12px rgba(0, 210, 255, 0.2) !important;
}
/* Flexbox 輔助 class */
.gap-2 {
  gap: 8px;
}
</style>