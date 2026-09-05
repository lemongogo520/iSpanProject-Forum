<template>
  <v-container class="py-6">
    <!-- 上方標題與介紹 -->
    <v-row class="mb-6 text-center text-md-left">
      <v-col cols="12">
        <h1 class="text-h4 font-weight-bold mb-2">🎉 探索論壇分類</h1>
        <p class="text-subtitle-1 text-grey-darken-1">選擇你感興趣的主題，開始參與討論吧！</p>
      </v-col>
    </v-row>

    <!-- 骨架屏 (載入中動畫) -->
    <v-row v-if="loading">
      <v-col v-for="n in 4" :key="n" cols="12" sm="6" md="3">
        <v-skeleton-loader type="list-item-two-line" class="rounded-xl border"></v-skeleton-loader>
      </v-col>
    </v-row>

    <!-- 分類與討論版區塊 -->
    <template v-else-if="categories.length > 0">
      <!-- 第一層：大分類（如：🎮 遊戲、🎬 動漫） -->
      <div 
        v-for="parent in categories" 
        :key="parent.id" 
        class="mb-8"
      >
        <!-- 大分類標題 -->
        <h2 class="text-h5 font-weight-bold mb-4 d-flex align-center">
          <span class="me-2">{{ getCategoryIcon(parent.name) }}</span>
          {{ parent.name }}
        </h2>

        <!-- 第二層：該分類底下的討論版卡片網格 -->
        <v-row>
          <v-col 
            v-for="board in parent.boards" 
            :key="board.id" 
            cols="12" 
            sm="6" 
            md="3" 
          >
            <!-- 完美的扁平化小卡片樣式 -->
            <v-card 
              :to="`/boards/${board.id}`"
              class="board-card pa-4 rounded-xl"
              variant="outlined"
              hover
            >
              <div class="d-flex flex-column justify-center">
                <!-- 討論版名稱 -->
                <span class="text-subtitle-1 font-weight-bold text-grey-darken-4 text-truncate mb-1">
                  {{ board.name }}
                </span>
                <!-- 文章數量（若後端欄位名不同，請看下方說明修改） -->
                <span class="text-caption text-grey-darken-1 font-weight-medium">
                  文章：{{ board.postCount || board.post_count || 0 }}
                </span>
              </div>
            </v-card>
          </v-col>

          <!-- 如果該大分類下暫時沒有討論版 -->
          <v-col cols="12" v-if="!parent.boards || parent.boards.length === 0">
            <p class="text-caption text-grey-lighten-1 ps-2">目前此分類下暫無討論版</p>
          </v-col>
        </v-row>
      </div>
    </template>

    <!-- 完全無資料提示 -->
    <v-row v-else class="text-center py-12">
      <v-col cols="12">
        <v-icon size="64" color="grey-lighten-1" class="mb-4">mdi-folder-open-outline</v-icon>
        <p class="text-grey-darken-1 text-h6">目前還沒有任何分類，請稍後再試</p>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import httpClient from '@/api/httpClient' // 引入你在 main.js 中設定的 axios 實例

const categories = ref([]) // 存放大分類與其子看板的完整陣列
const loading = ref(true)

// 使用你指定的 httpClient 格式獲取資料
const fetchData = async () => {
  loading.value = true
  try {
    // 1. 先獲取所有大分類
    const resCategories = await httpClient({
      method: 'get',
      url: '/api/categories',
      data: {}
    })
    
    // 將大分類陣列進行權重排序
    const rawCategories = [...resCategories.data].sort((a, b) => (a.rank || 0) - (b.rank || 0))

    // 2. 遍歷每個大分類，對接你的 @GetMapping("/api/boards/category/{categoryId}")
    for (let cat of rawCategories) {
      const resBoards = await httpClient({
        method: 'get',
        url: `/api/boards/category/${cat.id}`, // 動態帶入大分類 ID
        data: {}
      })
      
      // 動態將查出來的子看板陣列掛載到該大分類物件底下
      cat.boards = resBoards.data
    }

    // 3. 把組裝好 boards 的完整資料丟給響應式變數渲染畫面
    categories.value = rawCategories

  } catch (error) {
    console.error('資料載入失敗，請檢查 API 路由或後端伺服器:', error)
  } finally {
    loading.value = false
  }
}

// 根據大分類名稱自動配對精美 Emoji 表情
const getCategoryIcon = (name) => {
  if (name.includes('遊戲')) return '🎮'
  if (name.includes('動漫')) return '🎬'
  if (name.includes('科技')) return '💻'
  if (name.includes('美食')) return '🍕'
  if (name.includes('休閒')) return '🏖️'
  return '📌'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* 復刻你設計圖中輕量、高質感的簡約邊框與懸浮微動效 */
.board-card {
  background-color: #ffffff !important;
  border: 1px solid #e0e0e0 !important;
  transition: all 0.2s ease-in-out;
}
.board-card:hover {
  transform: translateY(-2px);
  border-color: #1867c0 !important; /* 懸浮時邊框變成 Vuetify 主色 */
  box-shadow: 0 4px 12px rgba(24, 103, 192, 0.08) !important;
}
</style>
