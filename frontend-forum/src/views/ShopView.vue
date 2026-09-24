<template>
  <div class="shop-page">
    <!-- 商城標題 -->
    <section class="shop-header">
      <div>
        <h1>紙娃娃商城</h1>
        <p>使用點數購買喜歡的造型商品</p>
      </div>
    </section>

    <!-- 商品分類 -->
    <section class="category-section">
      <button
        v-for="category in categories"
        :key="category.value"
        type="button"
        class="category-button"
        :class="{ active: selectedType === category.value }"
        :disabled="loading || purchasingProductId !== null"
        @click="selectCategory(category.value)"
      >
        {{ category.label }}
      </button>
    </section>

    <!-- 成功訊息 -->
    <div
      v-if="successMessage"
      class="message success-message"
      role="status"
    >
      {{ successMessage }}
    </div>

    <!-- 錯誤訊息 -->
    <div
      v-if="errorMessage"
      class="message error-message"
      role="alert"
    >
      {{ errorMessage }}
    </div>

    <!-- 商品載入狀態 -->
    <div v-if="loading" class="status-box">
      商品載入中...
    </div>

    <!-- 查詢失敗 -->
    <div v-else-if="errorMessage && products.length === 0" class="status-box">
      無法載入商品，請確認連線或登入狀態後重新整理。
    </div>

    <!-- 沒有商品 -->
    <div v-else-if="products.length === 0" class="status-box">
      目前沒有商品
    </div>

    <!-- 商品列表 -->
    <section v-else class="product-grid">
      <ProductCard
        v-for="product in products"
        :key="product.id"
        :product="product"
        :loading="purchasingProductId === product.id"
        @purchase="handlePurchase"
      />
    </section>

    <!-- 商品分頁 -->
    <div v-if="totalPages > 1" class="pagination">
      <button
        type="button"
        :disabled="
          loading || purchasingProductId !== null || pageNumber <= 1
        "
        @click="changePage(pageNumber - 1)"
      >
        上一頁
      </button>

      <span>第 {{ pageNumber }} / {{ totalPages }} 頁</span>

      <button
        type="button"
        :disabled="
          loading || purchasingProductId !== null || pageNumber >= totalPages
        "
        @click="changePage(pageNumber + 1)"
      >
        下一頁
      </button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import ProductCard from "@/components/shop/ProductCard.vue";
import {
  getCategories,
  getProducts,
  purchaseProduct,
} from "@/services/shopApi";

const products = ref([]);
const categories = ref([]);
const selectedType = ref(null);

const pageNumber = ref(1);
const totalPages = ref(0);

const loading = ref(false);
const purchasingProductId = ref(null);

const successMessage = ref("");
const errorMessage = ref("");

function getErrorMessage(error, fallback) {
  const data = error.response?.data;

  if (typeof data === "string" && !data.trim().startsWith("<")) {
    return data;
  }

  return data?.message || error.message || fallback;
}

async function loadProducts() {
  if (selectedType.value === null) return;

  loading.value = true;
  errorMessage.value = "";

  try {
    const data = await getProducts(selectedType.value, pageNumber.value);
    products.value = data.content;
    totalPages.value = data.totalPages;
  } catch (error) {
    products.value = [];
    totalPages.value = 0;
    errorMessage.value = getErrorMessage(error, "商品載入失敗");
  } finally {
    loading.value = false;
  }
}

async function selectCategory(categoryId) {
  if (loading.value || purchasingProductId.value !== null) return;
  if (selectedType.value === categoryId) return;

  selectedType.value = categoryId;
  pageNumber.value = 1;
  successMessage.value = "";

  await loadProducts();
}

async function changePage(nextPage) {
  if (loading.value || purchasingProductId.value !== null) return;
  if (nextPage < 1 || nextPage > totalPages.value) return;

  pageNumber.value = nextPage;
  await loadProducts();
}

async function handlePurchase(product) {
  if (loading.value || purchasingProductId.value !== null) return;
  if (product.isOwned === true) return;

  successMessage.value = "";
  errorMessage.value = "";

  const confirmed = window.confirm(
    `確定要花 ${product.price} 點購買「${product.name}」嗎？`,
  );

  if (!confirmed) return;

  purchasingProductId.value = product.id;

  try {
    const result = await purchaseProduct(product.id);

    product.isOwned = true;
    successMessage.value =
      `購買成功：${result.itemName}，消耗 ${result.price} 點，` +
      `剩餘 ${result.balanceAfter} 點`;

    await loadProducts();
  } catch (error) {
    errorMessage.value = getErrorMessage(error, "購買失敗");
  } finally {
    purchasingProductId.value = null;
  }
}

onMounted(async () => {
  loading.value = true;

  try {
    const data = await getCategories();

    categories.value = data.map((category) => ({
      label: category.typeName,
      value: category.id,
    }));

    selectedType.value = categories.value[0]?.value ?? null;
  } catch (error) {
    errorMessage.value = getErrorMessage(error, "分類載入失敗");
  } finally {
    loading.value = false;
  }

  if (selectedType.value !== null) {
    await loadProducts();
  }
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
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 28px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #d6dce6;
  border-radius: 8px;
  background: white;
  color: #555555;
  cursor: pointer;
}

.pagination button:disabled,
.category-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
