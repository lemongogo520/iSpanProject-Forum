<template>
  <div class="doll-canvas-container" :style="{ minHeight: `${64 * scale}px` }">
    <!-- 防呆：如果沒有任何裝備，顯示佔位符 -->
    <div v-if="sortedLayers.length === 0" class="empty-state">
      <v-icon icon="mdi-account" size="80" color="grey-darken-2"></v-icon>
      <div class="text-grey-darken-1 mt-2">尚未穿備任何物品</div>
    </div>

    <!-- 原生 PixiJS 畫布掛載點 -->
    <div
      v-show="sortedLayers.length > 0"
      ref="pixiContainer"
      class="pixi-wrapper"
    ></div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount } from "vue";
import * as PIXI from "pixi.js"; // 直接使用原生 PIXI

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

// DOM 節點參考
const pixiContainer = ref(null);
let app = null;
let mainContainer = null;

// 記錄已經載入過的 Texture 快取 (避免重複發送網路請求)
const textureCache = {};

onMounted(() => {
  // 1. 初始化原生 PixiJS 應用
  app = new PIXI.Application({
    width: 64 * props.scale,
    height: 64 * props.scale,
    backgroundAlpha: 0,
  });

  // 2. 將 Canvas 塞入 HTML 畫面中
  pixiContainer.value.appendChild(app.view);

  // 3. 建立主要群組 (處理縮放與位移)
  mainContainer = new PIXI.Container();
  mainContainer.scale.set(props.scale);
  if (props.avatarMode) {
    mainContainer.y = 64 * props.scale * 0.15;
  }
  // 開啟自動排序，確保 zIndex 永遠正確
  mainContainer.sortableChildren = true;
  app.stage.addChild(mainContainer);

  // 初次繪製
  renderSprites();
});

// 當玩家離開這個頁面時，務必銷毀 WebGL 釋放記憶體
onBeforeUnmount(() => {
  if (app) {
    app.destroy(true, { children: true, texture: false, baseTexture: false });
  }
});

// 監聽裝備或顏色變化，觸發重新繪製
// 使用 deep: true 確保當 equippedItems 裡面的某個裝備的 color 改變時，也能被偵測到
watch(
  () => props.equippedItems,
  () => {
    if (app && mainContainer) {
      renderSprites();
    }
  },
  { deep: true },
);

// 記錄目前畫布上的 Sprites (Key: layer.id, Value: PIXI.Sprite)
const spriteCache = {};

// 核心繪製邏輯
async function renderSprites() {
  // 1. 找出目前應該要顯示的所有裝備 ID
  const currentIds = new Set(sortedLayers.value.map((l) => l.id));

  // 2. 【解決殘影】移除已經被脫下的裝備
  for (const id in spriteCache) {
    if (!currentIds.has(Number(id))) {
      mainContainer.removeChild(spriteCache[id]);
      spriteCache[id].destroy(); // 釋放顯示卡記憶體
      delete spriteCache[id];
    }
  }

  // 3. 更新或新增現有裝備
  for (const layer of sortedLayers.value) {
    if (!layer.filePath) continue;

    // 如果快取裡沒有這張圖，就去下載並裁切 (非同步)
    if (!textureCache[layer.id]) {
      try {
        const baseTex = await PIXI.Texture.fromURL(
          `${props.baseUrl}${layer.filePath}`,
        );
        baseTex.baseTexture.scaleMode = PIXI.SCALE_MODES.NEAREST;
        const frame = new PIXI.Rectangle(0, 128, 64, 64);
        textureCache[layer.id] = new PIXI.Texture(baseTex.baseTexture, frame);
      } catch (error) {
        console.error("圖片載入失敗", layer.filePath, error);
        continue;
      }
    }

    // 確定圖片有載入成功後
    if (textureCache[layer.id]) {
      let sprite = spriteCache[layer.id];

      // 如果是第一次穿上，建立新 Sprite 並加進畫布
      if (!sprite) {
        sprite = new PIXI.Sprite(textureCache[layer.id]);
        spriteCache[layer.id] = sprite;
        mainContainer.addChild(sprite);
      }

      // 每次重繪都強制更新圖層高度，確保衣服不會互相覆蓋
      sprite.zIndex = layer.category.zIndex;

      // 【真・Tint 調色系統】：將圖層去色後，覆蓋指定顏色
      if (layer.color !== undefined && layer.color !== null) {
        if (!sprite.filters || sprite.filters.length === 0) {
          sprite.filters = [new PIXI.ColorMatrixFilter()];
        }

        const filter = sprite.filters[0];
        // 重設濾鏡矩陣，避免重複疊加造成不可預期的結果
        filter.reset();

        // 1. 強制去色 (將橘色頭髮等彩色圖檔變成完美的灰階陰影底圖)
        filter.desaturate();

        // ✨ 1.5 偷偷打光 (解決底圖太暗導致染色不夠鮮豔的問題，可視情況調整 1.2 ~ 1.5)
        filter.brightness(1.2, true);

        // 2. 將玩家指定的色碼覆蓋上去 (true 代表與前一個矩陣相乘)
        filter.tint(Number(layer.color), true);
      } else {
        // 如果沒有選擇任何顏色，恢復素材最原始的狀態
        sprite.filters = [];
      }
    }
  }
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

.pixi-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 1;
}
</style>
