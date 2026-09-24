<template>
  <div class="doll-canvas-container" :style="{ minHeight: `${64 * scale}px` }">
    <!-- 防呆：如果沒有任何裝備，顯示佔位符 -->
    <div v-if="sortedLayers.length === 0" class="empty-state">
      <v-icon icon="mdi-account" size="80" color="grey-darken-2"></v-icon>
      <div class="text-grey-darken-1 mt-2">尚未穿備任何物品</div>
    </div>

    <!-- 不再使用 Pixi 掛載點，直接使用一般 <img> 顯示渲染結果 -->
    <img
      v-else-if="renderedImage"
      :src="renderedImage"
      class="rendered-doll-img"
      :style="{ width: `${64 * scale}px`, height: `${64 * scale}px` }"
    />
  </div>
</template>

<script>
// 使用一般 <script> 宣告全域變數，確保所有 DollConvas 共用
import * as PIXI from "pixi.js";

// 全域共用的唯一 WebGL 引擎 (離屏渲染用)
let sharedApp = null;
// 全域共用的 Texture 快取，換分頁也不用重新下載圖片
const textureCache = {};

function initSharedApp() {
  if (!sharedApp) {
    sharedApp = new PIXI.Application({
      width: 256, // 引擎底層畫布大小不重要，我們只用 renderer 來截圖
      height: 256,
      backgroundAlpha: 0,
    });
    // 重點：不再呼叫 appendChild，讓它永遠在背景默默工作
  }
}
</script>

<script setup>
import { ref, computed, watch, onMounted } from "vue";

const props = defineProps({
  equippedItems: { type: Object, default: () => ({}) },
  baseUrl: { type: String, default: "http://localhost:8080/sprites/" },
  scale: { type: Number, default: 3 },
  avatarMode: { type: Boolean, default: false },
});

const sortedLayers = computed(() => {
  return Object.values(props.equippedItems)
    .filter((item) => item && item.category)
    .sort((a, b) => a.category.zIndex - b.category.zIndex);
});

// 存放最後產出的 Base64 圖片
const renderedImage = ref(null);

onMounted(() => {
  initSharedApp(); // 確保全域引擎已經啟動
  renderSprites(); // 進行首次「拍照」
});

// 監聽裝備或顏色變化，重新「拍照」
watch(
  () => props.equippedItems,
  () => {
    renderSprites();
  },
  { deep: true },
);

// 核心繪製邏輯改為「虛擬佈置 -> 拍照 -> 銷毀」
async function renderSprites() {
  if (sortedLayers.value.length === 0) {
    renderedImage.value = null;
    return;
  }

  // 1. 下載並裁切這一次需要的所有圖片到快取中
  for (const layer of sortedLayers.value) {
    if (!layer.filePath) continue;
    if (!textureCache[layer.id]) {
      try {
        const baseTex = await PIXI.Texture.fromURL(
          `${props.baseUrl}${layer.filePath}`,
        );
        baseTex.baseTexture.scaleMode = PIXI.SCALE_MODES.NEAREST; // 保持像素風格
        const frame = new PIXI.Rectangle(0, 128, 64, 64);
        textureCache[layer.id] = new PIXI.Texture(baseTex.baseTexture, frame);
      } catch (error) {
        console.error("圖片載入失敗", layer.filePath, error);
      }
    }
  }

  // 2. 建立紙娃娃主體容器
  const avatarContainer = new PIXI.Container();
  avatarContainer.scale.set(props.scale);
  avatarContainer.sortableChildren = true;
  if (props.avatarMode) {
    avatarContainer.y = 64 * props.scale * 0.15;
  }

  // 3. 佈置 Sprites 到臨時容器上
  for (const layer of sortedLayers.value) {
    if (textureCache[layer.id]) {
      const sprite = new PIXI.Sprite(textureCache[layer.id]);
      sprite.zIndex = layer.category.zIndex;

      // 處理顏色 Tint
      if (layer.color !== undefined && layer.color !== null) {
        const filter = new PIXI.ColorMatrixFilter();
        filter.desaturate();
        filter.brightness(1.2, true);
        filter.tint(Number(layer.color), true);
        sprite.filters = [filter];
      }

      avatarContainer.addChild(sprite);
    }
  }

  try {
    // 4. 建立指定大小的虛擬畫布，完美還原舊版 Canvas 的裁切效果
    const rt = PIXI.RenderTexture.create({
      width: 64 * props.scale,
      height: 64 * props.scale,
    });

    // 5. 將佈置好的 avatarContainer 畫到這個虛擬畫布上
    sharedApp.renderer.render(avatarContainer, { renderTexture: rt });

    // 6. 針對這個畫布拍照 (PixiJS v7 會回傳 Promise)
    renderedImage.value = await sharedApp.renderer.extract.base64(rt);

    // 用完馬上銷毀虛擬畫布
    rt.destroy(true);
  } catch (err) {
    console.error("截圖發生錯誤:", err);
  }

  // 7. 立即銷毀紙娃娃容器與裡面的 Sprite，完全不留負擔在記憶體中
  avatarContainer.destroy({ children: true });
}
</script>

<style scoped>
.doll-canvas-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.rendered-doll-img {
  display: block;
  /* 加上平滑漸變，讓圖片在置換時看起來更順暢 */
  transition: opacity 0.2s ease;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 1;
}
</style>
