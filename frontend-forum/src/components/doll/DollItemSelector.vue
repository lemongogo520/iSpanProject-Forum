<template>
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
            @click="handleClick(item)"
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
</template>

<script setup>
import { onMounted } from "vue";
import { listUserItems } from "@/services/dollSystemApi/dollUserItemApi";

const {
  baseUrl,
  itemsByCategory,
  dollItems,
  categories,
  activeCategoryId,
  loadCategories,
} = listUserItems();

// 宣告傳遞給父層 (DollCartItemsView) 的事件
const emit = defineEmits(['equip-item']);

const handleClick = (item) => {
  emit('equip-item', item);
};

// 元件載入時，發送 API 取資料
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
