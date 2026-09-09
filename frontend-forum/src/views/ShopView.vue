<template>
  <div class="shop-page">
    <!-- ====================== -->
    <!-- 商城標題 -->
    <!-- ====================== -->

    <section class="shop-header">
      <div>
        <h1>紙娃娃商城</h1>

        <p>使用點數購買喜歡的造型商品</p>
      </div>
    </section>

    <!-- ====================== -->
    <!-- 測試會員 -->
    <!-- ====================== -->

    <section class="test-user-box">
      <label> 測試會員 UUID </label>

      <input
        v-model="userId"
        type="text"
        placeholder="請輸入測試會員 UUID"
        @change="saveUserId"
      />

      <small> 目前後端測試階段需要 userId， 正式登入整合後會移除此欄。 </small>
    </section>

    <!-- ====================== -->
    <!-- 商品分類 -->
    <!-- ====================== -->

    <section class="category-section">
      <button
        v-for="category in categories"
        :key="category.value || 'ALL'"
        class="category-button"
        :class="{
          active: selectedType === category.value,
        }"
        @click="selectCategory(category.value)"
      >
        {{ category.label }}
      </button>
    </section>

    <!-- ====================== -->
    <!-- 成功訊息 -->
    <!-- ====================== -->

    <div v-if="successMessage" class="message success-message">
      {{ successMessage }}
    </div>

    <!-- ====================== -->
    <!-- 錯誤訊息 -->
    <!-- ====================== -->

    <div v-if="errorMessage" class="message error-message">
      {{ errorMessage }}
    </div>

    <!-- ====================== -->
    <!-- Loading -->
    <!-- ====================== -->

    <div v-if="loading" class="status-box">商品載入中...</div>

    <!-- ====================== -->
    <!-- 商品不存在 -->
    <!-- ====================== -->

    <div v-else-if="products.length === 0" class="status-box">目前沒有商品</div>

    <!-- ====================== -->
    <!-- 商品列表 -->
    <!-- ====================== -->

    <section v-else class="product-grid">
      <ProductCard
        v-for="product in products"
        :key="product.id"
        :product="product"
        :loading="purchasingProductId === product.id"
        @purchase="handlePurchase"
      />
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";

import ProductCard from "../components/shop/ProductCard.vue";

import { getProducts, purchaseProduct } from "../services/shopApi";

// ==============================
// 商品
// ==============================

const products = ref([]);

// ==============================
// Loading
// ==============================

const loading = ref(false);

const purchasingProductId = ref(null);

// ==============================
// 訊息
// ==============================

const successMessage = ref("");

const errorMessage = ref("");

// ==============================
// 商品分類
// ==============================

const selectedType = ref(null);

const categories = [
  {
    label: "全部",
    value: null,
  },

  {
    label: "服裝",
    value: "CLOTHES",
  },

  {
    label: "髮型",
    value: "HAIR",
  },

  {
    label: "配件",
    value: "ACCESSORY",
  },

  {
    label: "背景",
    value: "BACKGROUND",
  },

  {
    label: "其他",
    value: "OTHER",
  },
];

// ==============================
// 測試用會員 UUID
// ==============================

const userId = ref(localStorage.getItem("shopTestUserId") || "");

function saveUserId() {
  localStorage.setItem("shopTestUserId", userId.value);
}

// ==============================
// 查詢商品
// ==============================

async function loadProducts() {
  loading.value = true;

  errorMessage.value = "";

  try {
    products.value = await getProducts(selectedType.value);
  } catch (error) {
    console.error(error);

    errorMessage.value = error.message || "商品載入失敗";
  } finally {
    loading.value = false;
  }
}

// ==============================
// 商品分類
// ==============================

async function selectCategory(type) {
  selectedType.value = type;

  await loadProducts();
}

// ==============================
// 購買商品
// ==============================

async function handlePurchase(product) {
  successMessage.value = "";
  errorMessage.value = "";

  if (!userId.value) {
    errorMessage.value = "請先輸入測試會員 UUID";

    return;
  }

  const confirmed = window.confirm(
    `確定要花 ${product.price} 點購買「${product.name}」嗎？`,
  );

  if (!confirmed) {
    return;
  }

  purchasingProductId.value = product.id;

  try {
    const result = await purchaseProduct(product.id, userId.value);

    successMessage.value = `購買成功：${result.productName}，消耗 ${result.price} 點`;

    console.log("購買結果：", result);

    // 商品狀態可能改變，
    // 購買成功後重新讀取
    await loadProducts();
  } catch (error) {
    console.error(error);

    errorMessage.value = error.message || "購買失敗";
  } finally {
    purchasingProductId.value = null;
  }
}

// ==============================
// 頁面第一次進入
// ==============================

onMounted(() => {
  loadProducts();
});
</script>

<style scoped>
.shop-page {
  max-width: 1200px;

  margin: 0 auto;

  padding: 32px 24px;
}

.shop-header {
  display: flex;

  align-items: center;
  justify-content: space-between;

  margin-bottom: 24px;
}

.shop-header h1 {
  margin: 0 0 6px;

  font-size: 30px;

  color: #333333;
}

.shop-header p {
  margin: 0;

  color: #777777;
}

/* 測試會員 */

.test-user-box {
  margin-bottom: 24px;

  padding: 16px;

  background: #f8f9fb;

  border-radius: 12px;
}

.test-user-box label {
  display: block;

  margin-bottom: 8px;

  font-weight: bold;
}

.test-user-box input {
  width: 100%;

  box-sizing: border-box;

  padding: 10px 12px;

  border: 1px solid #cccccc;

  border-radius: 8px;
}

.test-user-box small {
  display: block;

  margin-top: 7px;

  color: #888888;
}

/* 分類 */

.category-section {
  display: flex;

  gap: 10px;

  flex-wrap: wrap;

  margin-bottom: 25px;
}

.category-button {
  padding: 8px 16px;

  border: 1px solid #d6dce6;

  border-radius: 20px;

  background: white;

  color: #555555;

  cursor: pointer;
}

.category-button.active {
  background: #6f91c7;

  border-color: #6f91c7;

  color: white;
}

/* 商品 */

.product-grid {
  display: grid;

  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));

  gap: 22px;
}

/* 訊息 */

.message {
  padding: 12px 16px;

  border-radius: 8px;

  margin-bottom: 20px;
}

.success-message {
  background: #edf8f0;

  color: #37734a;
}

.error-message {
  background: #fff0f0;

  color: #a84b4b;
}

.status-box {
  padding: 50px;

  text-align: center;

  color: #888888;
}
</style>
