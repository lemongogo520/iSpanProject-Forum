<template>
  <v-container class="pa-6" style="background: var(--color-background); min-height: 100vh;">
    <v-card class="mx-auto pa-6" max-width="600" elevation="6" rounded="lg">
      <v-card-title class="text-h5 font-weight-bold mb-4">
        <v-icon icon="mdi-card-account-details-outline" class="mr-2" color="primary"></v-icon>
        大頭貼展示測試
      </v-card-title>
      
      <v-card-text>
        <p class="mb-4 text-medium-emphasis">請輸入你想查看的玩家 UUID (可用資料庫裡的測試帳號)：</p>
        <v-text-field
          v-model="targetUserId"
          label="玩家 UUID"
          variant="outlined"
          density="comfortable"
          placeholder="例如：550e8400-e29b-41d4-a716-446655440000"
          clearable
        ></v-text-field>

        <v-btn
          color="primary"
          block
          size="large"
          class="mb-6 font-weight-bold"
          @click="fetchAvatar"
          :loading="isLoading"
          prepend-icon="mdi-magnify"
        >
          載入玩家大頭貼
        </v-btn>

        <!-- 展示區塊 -->
        <v-divider class="mb-6"></v-divider>

        <div v-if="hasLoaded" class="text-center">
          <h3 class="mb-4">大頭貼渲染結果</h3>
          
          <div class="d-flex justify-center gap-6">
            <!-- 展示縮放 1.5 倍 (中頭像) -->
            <div>
              <div class="mb-2 text-caption">Scale: 1.5</div>
              <v-sheet 
                class="d-inline-flex align-center justify-center pa-2 rounded-circle border"
                style="background: var(--color-background-light); border-color: var(--color-border) !important; width: 100px; height: 100px; overflow: hidden;"
              >
                <!-- 這裡是精華！引用你的元件並縮小 -->
                <DollConvas :equippedItems="avatarItems" :scale="1.5" />
              </v-sheet>
            </div>

            <!-- 展示縮放 1 倍 (小頭像) -->
            <div>
              <div class="mb-2 text-caption">Scale: 1.0</div>
              <v-sheet 
                class="d-inline-flex align-center justify-center pa-1 rounded-circle border"
                style="background: var(--color-background-light); border-color: var(--color-border) !important; width: 68px; height: 68px; overflow: hidden;"
              >
                <DollConvas :equippedItems="avatarItems" :scale="1.0" />
              </v-sheet>
            </div>
          </div>
          
          <div class="mt-6 text-caption text-medium-emphasis">
            這個圓形外框模擬了未來討論區文章旁邊的大頭貼樣式
          </div>
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref } from 'vue';
import httpClient from '@/services/httpClient';
import DollConvas from '@/components/doll/DollConvas.vue';

const targetUserId = ref('');
const isLoading = ref(false);
const hasLoaded = ref(false);
const avatarItems = ref({});

const fetchAvatar = () => {
  if (!targetUserId.value) {
    alert("請先輸入 UUID！");
    return;
  }

  isLoading.value = true;
  
  httpClient.get(`/api/dollitem/equipped/${targetUserId.value.trim()}`)
    .then(response => {
      const items = response.data;
      const newEquipped = {};
      
      // 將陣列轉換成畫布需要的字典格式
      if (items && items.length > 0) {
        items.forEach((item) => {
          if (item && item.category) {
            newEquipped[item.category.id] = item;
          }
        });
      }
      
      avatarItems.value = newEquipped;
      hasLoaded.value = true;
    })
    .catch(error => {
      console.error("無法取得大頭貼", error);
      alert("找不到該玩家，或 UUID 格式錯誤！");
    })
    .finally(() => {
      isLoading.value = false;
    });
};
</script>

<style scoped>
.gap-6 {
  gap: 24px;
}
</style>
