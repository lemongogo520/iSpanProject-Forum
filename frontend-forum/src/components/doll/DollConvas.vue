<template>
  <div class="doll-canvas-container">
    <!-- 防呆：如果沒有任何裝備，顯示佔位符 -->
    <div v-if="sortedLayers.length === 0" class="empty-state">
      <v-icon icon="mdi-account" size="80" color="grey-darken-2"></v-icon>
      <div class="text-grey-darken-1 mt-2">尚未穿備任何物品</div>
    </div>

    <!-- 依序渲染疊加圖層 -->
    <div
      v-for="layer in sortedLayers"
      :key="layer.id"
      class="doll-layer"
      :style="{ backgroundImage: `url(${baseUrl}${layer.filePath})` }"
    ></div>
  </div>
</template>
<script setup>
import { computed } from "vue";

const props = defineProps({
  // 存放目前穿在身上的裝備物件
  equippedItems: {
    type: Object,
    default: () => ({}),
  },
  // 圖片基底網址
  baseUrl: {
    type: String,
    default: "http://localhost:8080/sprites/",
  },
});

//將物件轉為陣列，並依照分類的 zIndex (圖層高度) 進行排序
const sortedLayers = computed(() => {
  return Object.values(props.equippedItems)
    .filter((item) => item && item.category) // 防呆：過濾掉空值
    .sort((a, b) => a.category.zIndex - b.category.zIndex);
});
</script>
<style scoped>
/* 畫布外框：相對定位，作為所有圖層的對齊基準 */
.doll-canvas-container {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

/* 獨立圖層：絕對定位，全部疊在一起 */
.doll-layer {
  position: absolute;
  top: 50%;
  left: 50%;
  /* 根據 LPC 雪碧圖單個動作的框格大小調整，例如 64x64 */
  width: 64px;
  height: 64px;
  /* 將圖片移回中心點並放大，保持像素邊緣清晰 */
  transform: translate(-50%, -50%) scale(3);

  background-position: 0px -128px; /* 如果需要指定顯示雪碧圖的第一格正面，可在此微調 */
  background-repeat: no-repeat;
  image-rendering: pixelated;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 1;
}
</style>
