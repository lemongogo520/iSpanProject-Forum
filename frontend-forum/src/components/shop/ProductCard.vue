<template>
  <div class="product-card">
   <!-- 商品圖片 -->
<div class="product-image-box">
  <div   v-if="imageSrc"   class="shop-sprite-thumbnail" :style="{ backgroundImage: `url(${imageSrc})` }"
    role="img"  :aria-label="product.name"></div>
  <div v-else class="image-placeholder">🛍️</div>
</div>

    <!-- 商品資料 -->
    <div class="product-info">
      <span class="product-type">{{ typeLabel }}</span>

      <h3 class="product-name">{{ product.name }}</h3>

      <p class="product-description">
        {{ product.description || "暫無商品說明" }}
      </p>

      <div class="product-bottom">
        <div class="product-price">
          <span class="coin-icon">●</span>
          {{ product.price }} 點
        </div>

        <button
          type="button"
          class="buy-button"
          :disabled="loading || isOwned"
          @click="handlePurchase"
        >
          <span v-if="loading">購買中...</span>
          <span v-else-if="isOwned">已擁有</span>
          <span v-else>購買</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { resolveProductImage } from "@/services/shopApi";

const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["purchase"]);
const imageError = ref(false);

const imageSrc = computed(() => {
  return resolveProductImage(props.product.filePath);
});

watch(imageSrc, () => {
  imageError.value = false;
});

const typeLabel = computed(() => {
  return props.product.category?.typeName || "其他";
});

const isOwned = computed(() => {
  return props.product.isOwned === true;
});

function handlePurchase() {
  if (props.loading || isOwned.value) return;
  emit("purchase", props.product);
}
</script>

<style scoped>
.product-card {
  background: #ffffff;
  border: 1px solid #e8e8e8;
  border-radius: 16px;
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.product-image-box {
  height: 190px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: #f7f8fa;
}

/* 沿用衣櫃：第一欄、第三列，每格 64 × 64 */
.shop-sprite-thumbnail {
  width: 64px;
  height: 64px;
  flex-shrink: 0;
  background-position: 0px -128px;
  background-repeat: no-repeat;
  background-size: auto;
  image-rendering: pixelated;
  transform: scale(2);
}

.image-placeholder {
  font-size: 64px;
}

.product-info {
  padding: 18px;
}

.product-type {
  display: inline-block;
  padding: 4px 10px;
  background: #eef4ff;
  border-radius: 20px;
  font-size: 13px;
  color: #5271a5;
}

.product-name {
  margin: 10px 0 8px;
  font-size: 20px;
  color: #333333;
}

.product-description {
  min-height: 42px;
  margin-bottom: 18px;
  font-size: 14px;
  color: #777777;
  line-height: 1.5;
}

.product-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.product-price {
  font-size: 18px;
  font-weight: bold;
  color: #555555;
}

.coin-icon {
  color: #e6ad3c;
}

.buy-button {
  border: none;
  border-radius: 8px;
  padding: 9px 18px;
  background: #6f91c7;
  color: white;
  cursor: pointer;
  font-size: 14px;
}

.buy-button:hover:not(:disabled) {
  background: #5d80b7;
}

.buy-button:disabled {
  background: #bbbbbb;
  cursor: not-allowed;
}
</style>