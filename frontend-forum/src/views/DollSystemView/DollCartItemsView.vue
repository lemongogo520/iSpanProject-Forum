<template>
  <v-container fluid class="fill-height bg-grey-darken-4 pa-6">
    <v-row class="fill-height">
      <!-- 左側：紙娃娃預覽區 (佔 4 欄) -->
      <v-col cols="12" md="4" class="d-flex flex-column">
        <v-card
          theme="dark"
          class="flex-grow-1 bg-grey-darken-3 d-flex flex-column"
          rounded="lg"
          elevation="6"
        >
          <v-card-title
            class="text-center font-weight-bold text-cyan-accent-3 pt-4"
          >
            外觀預覽
          </v-card-title>

          <v-sheet
            class="flex-grow-1 mx-4 mb-4 bg-grey-darken-4 rounded-lg d-flex align-center justify-center border"
            style="border-color: #333 !important"
          >
            <div class="text-center text-grey-darken-1">
              <v-icon icon="mdi-account" size="120"></v-icon>
              <div class="mt-2">紙娃娃渲染區</div>
            </div>
          </v-sheet>

          <v-card-actions class="px-4 pb-4 justify-center gap-2">
            <v-btn
              variant="tonal"
              color="grey-lighten-1"
              prepend-icon="mdi-refresh"
              >重置</v-btn
            >
            <v-btn
              variant="tonal"
              color="cyan-accent-3"
              prepend-icon="mdi-dice-multiple"
              >隨機</v-btn
            >
            <v-btn
              variant="flat"
              color="cyan-accent-3"
              class="text-grey-darken-4 font-weight-bold"
              prepend-icon="mdi-content-save"
              >儲存套裝</v-btn
            >
          </v-card-actions>
        </v-card>
      </v-col>

      <!-- 右側：部位分類與裝備選擇 (佔 8 欄) -->
      <v-col cols="12" md="8" class="d-flex flex-column">
        <v-card
          theme="dark"
          class="flex-grow-1 bg-grey-darken-3 d-flex flex-column"
          rounded="lg"
          elevation="6"
        >
          <!-- 頂部分類頁籤：綁定 API 回傳的分類清單 -->
          <v-tabs
            v-model="activeCategoryId"
            color="cyan-accent-3"
            align-tabs="center"
            show-arrows
            class="border-b border-grey-darken-2"
            @update:model-value="itemsByCategory"
          >
            <v-tab
              v-for="category in categories"
              :key="category.id"
              :value="category.id"
            >
              {{ category.typeName }}
            </v-tab>
          </v-tabs>

          <!-- 裝備網格區域 -->
          <v-card-text
            class="flex-grow-1 overflow-y-auto pa-4"
            style="max-height: calc(100vh - 160px)"
          >
            <v-row v-if="dollItems.length > 0">
              <!-- 渲染真實裝備資料 -->
              <v-col
                v-for="item in dollItems"
                :key="item.id"
                cols="4"
                sm="3"
                md="2"
              >
                <v-card
                  theme="dark"
                  class="bg-grey-darken-4 pa-2 hover-card"
                  rounded="lg"
                  ripple
                >
                  <v-sheet
                    class="bg-grey-darken-3 rounded mb-1 d-flex align-center justify-center overflow-hidden"
                    height="80"
                  >
                    <!-- 使用雪碧圖技巧顯示裝備縮圖 -->
                    <div
                      class="pixel-sprite-thumbnail"
                      :style="{
                        backgroundImage: `url(${baseUrl}${item.filePath})`,
                      }"
                    ></div>
                  </v-sheet>
                  <div
                    class="text-caption text-center text-grey-lighten-1 text-truncate"
                  >
                    {{ item.name }}
                  </div>
                </v-card>
              </v-col>
            </v-row>

            <!-- 無裝備時的防呆提示 -->
            <v-row v-else class="fill-height align-center justify-center">
              <div class="text-grey-darken-1 text-center mt-10">
                <v-icon icon="mdi-hanger" size="48" class="mb-2"></v-icon>
                <p>此分類目前沒有裝備</p>
              </div>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
<script setup>
import { listUserItems } from "@/service/DollSystemApi/DollUserItemApi";
import { onMounted } from "vue";
const {
  baseUrl,
  itemsByCategory,
  dollItems,
  categories,
  activeCategoryId,
  loadCategories,
} = listUserItems();

onMounted(() => {
  loadCategories();
});
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
.gap-2 {
  gap: 8px;
}

/* 裝備縮圖的雪碧圖設定 */
.pixel-sprite-thumbnail {
  width: 64px;
  height: 64px;
  background-position: 0px -128px;
  background-repeat: no-repeat;
  image-rendering: pixelated;
  /* 縮圖不需要放太大，1.5倍剛好塞進卡片 */
  transform: scale(1.5) translateY(-4px);
}
</style>
