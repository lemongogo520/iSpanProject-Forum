<template>
  <div
    class="friend-avatar"
    :style="{ width: `${size}px`, height: `${size}px` }"
  >
    <div v-if="!sortedLayers.length" class="friend-avatar-empty">
      <v-icon icon="mdi-account" color="grey" :size="size * 0.6" />
    </div>

    <div v-else class="doll-stage" :style="stageStyle">
      <div
        v-for="l in sortedLayers"
        :key="l.id"
        class="doll-layer"
        :style="layerStyle(l)"
      ></div>
    </div>
  </div>
</template>

<script setup>
import { computed, watch } from "vue";
import { userAvatar } from "@/services/dollSystemApi/dollAvaratApi";

const props = defineProps({
  userId: { type: String, default: "" },
  size: { type: Number, default: 68 },
  scale: { type: Number, default: 1.8 },
  baseUrl: { type: String, default: "http://localhost:8080/sprites/" },
});

const avatarStore = userAvatar();

// 直接讀 store 的 cache（同步），沒有載入中的中間狀態
const items = computed(() => avatarStore.cache[props.userId] ?? null);

const sortedLayers = computed(() =>
  Object.values(items.value ?? {})
    .filter((i) => i && i.category && i.filePath)
    .sort((a, b) => a.category.zIndex - b.category.zIndex)
);

watch(
  () => props.userId,
  (id) => {
    if (id && !avatarStore.cache[id]) {
      avatarStore.fetchAvatar(id).catch(() => {});   // 失敗就維持預設圖示
    }
  },
  { immediate: true }
);

const stageStyle = computed(() => ({
  transform: `scale(${props.scale}) translateY(9.6px)`,   // 64 * 0.15
}));

const layerStyle = (l) => ({
  backgroundImage: `url(${props.baseUrl}${l.filePath})`,
  zIndex: l.category.zIndex,
  filter: l.color != null ? `hue-rotate(${Number(l.color)}deg)` : "none",
});
</script>

<style scoped>
.friend-avatar {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border-radius: 50%;
  border: 1px solid var(--color-border, rgba(0, 0, 0, 0.12));
  background: var(--color-background-light, #f0f0f0);
  flex-shrink: 0;
}
.friend-avatar-empty {
  display: flex;
  align-items: center;
  justify-content: center;
}
.doll-stage {
  position: relative;
  width: 64px;
  height: 64px;
  flex-shrink: 0;
}
.doll-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 64px;
  height: 64px;
  background-position: 0 -128px;   /* 對應 Pixi 的 Rectangle(0, 128, 64, 64) */
  background-repeat: no-repeat;
  image-rendering: pixelated;      /* 對應 SCALE_MODES.NEAREST */
}
</style>