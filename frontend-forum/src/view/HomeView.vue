<template>
  <v-container class="py-8">
    <!-- 首頁標題 -->
    <div class="mb-8">
      <h1 class="text-h4 font-weight-bold">論壇首頁</h1>

      <p class="text-body-1 text-medium-emphasis mt-2">選擇討論區</p>
    </div>

    <!-- 全站搜尋 -->
    <v-form class="mb-6" @submit.prevent="handleSearch">
      <div class="d-flex align-center ga-2">
        <v-text-field
          v-model="searchKeyword"
          label="搜尋全站文章標題"
          prepend-inner-icon="mdi-magnify"
          variant="outlined"
          hide-details
          clearable
          :disabled="searchLoading"
          @click:clear="clearSearch"
        />

        <v-btn
          type="submit"
          color="primary"
          :loading="searchLoading"
          :disabled="searchLoading"
        >
          搜尋
        </v-btn>
      </div>
    </v-form>

    <!-- 搜尋結果 -->
    <section v-if="activeKeyword" class="mb-8">
      <h2 class="text-h6 mb-4">「{{ activeKeyword }}」的搜尋結果</h2>

      <div v-if="searchLoading" class="text-center py-6">
        <v-progress-circular indeterminate color="primary" />
      </div>

      <v-alert v-else-if="searchError" type="error" variant="tonal">
        {{ searchError }}
      </v-alert>

      <template v-else>
        <div class="text-body-2 text-medium-emphasis mb-3">
          共找到 {{ searchTotalElements }} 篇文章
        </div>

        <v-card
          v-if="searchResults.length === 0"
          class="pa-6 text-center"
          variant="tonal"
        >
          找不到符合條件的文章，請嘗試其他關鍵字。
        </v-card>

        <v-card
          v-for="post in searchResults"
          :key="post.id"
          :to="{ name: 'PostDetail', params: { postId: post.id } }"
          class="mb-3 rounded-lg"
          hover
        >
          <v-card-item>
            <v-card-title>{{ post.title }}</v-card-title>

            <v-card-subtitle class="mt-2">
              {{ getBoardName(post.boardId) }}
              · {{ post.userName || "使用者" }} · 瀏覽：{{ post.views }}
            </v-card-subtitle>
          </v-card-item>
        </v-card>

        <v-pagination
          v-if="searchTotalPages > 1"
          v-model="searchPage"
          :length="searchTotalPages"
          class="mt-4"
          @update:model-value="fetchSearchResults"
        />
      </template>
    </section>
    <!-- 載入中 -->
    <div v-if="loading" class="text-center py-10">
      <v-progress-circular indeterminate color="primary" size="50" />
    </div>

    <!-- 錯誤 -->
    <v-alert v-else-if="errorMessage" type="error" variant="tonal">
      {{ errorMessage }}
    </v-alert>

    <!-- Category -->
    <div
      v-else
      v-for="category in sortedCategories"
      :key="category.id"
      class="mb-10"
    >
      <!-- Category 標題 -->
      <div class="d-flex align-center mb-4">
        <v-icon icon="mdi-folder" size="32" class="me-3" />

        <h2 class="text-h5 font-weight-bold">
          {{ category.name }}
        </h2>
      </div>

      <!-- Board -->
      <v-row>
        <v-col
          v-for="board in boards[category.id]"
          :key="board.id"
          cols="12"
          sm="6"
          md="4"
          lg="3"
        >
          <v-card :to="`/boards/${board.id}`" hover class="h-100 rounded-xl">
            <v-card-item>
              <v-card-title>
                {{ board.name }}
              </v-card-title>

              <v-card-subtitle v-if="board.description">
                {{ board.description }}
              </v-card-subtitle>
            </v-card-item>

            <v-card-actions>
              <v-btn
                color="primary"
                variant="text"
                append-icon="mdi-arrow-right"
              >
                進入討論
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </div>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";

import httpClient from "@/services/httpClient"; // 引入你在 main.js 中設定的 axios 實例

import { searchPosts as searchPostsApi } from "@/services/postService";
// =========================
// Category
// =========================

const categories = ref([]);

// =========================
// Board
// =========================
//
// 會長這樣：
//
// boards = {
//   1: [ Board1, Board2 ],
//   2: [ Board3, Board4 ],
//   3: [ Board5, Board6 ]
// }

const boards = ref({});

const loading = ref(false);

const errorMessage = ref("");

// =========================
// Category 排序
// =========================

const sortedCategories = computed(() => {
  return [...categories.value].sort((a, b) => a.rank - b.rank);
});

// =========================
// 取得 Category
// =========================

const fetchCategories = async () => {
  const response = await httpClient.get("/api/categories");

  categories.value = response.data;
};

// =========================
// 取得指定 Category 的 Board
// =========================

const fetchBoards = async (categoryId) => {
  const response = await httpClient.get(`/api/boards/category/${categoryId}`);

  boards.value[categoryId] = response.data;
};

// =========================
// 載入首頁
// =========================

const loadHomePage = async () => {
  loading.value = true;

  errorMessage.value = "";

  try {
    // ① 先取得 Category
    await fetchCategories();

    // ② 取得每個 Category 底下的 Board
    await Promise.all(
      categories.value.map((category) => fetchBoards(category.id)),
    );
  } catch (error) {
    console.error("首頁資料取得失敗：", error);

    errorMessage.value = "首頁資料取得失敗，請確認後端是否正常啟動。";
  } finally {
    loading.value = false;
  }
};
// =========================
// 全站搜尋
// =========================
const searchKeyword = ref("");
const activeKeyword = ref("");
const searchResults = ref([]);

const searchPage = ref(1);
const searchPageSize = 10;
const searchTotalPages = ref(0);
const searchTotalElements = ref(0);

const searchLoading = ref(false);
const searchError = ref("");

// 取得搜尋結果
const fetchSearchResults = () => {
  if (!activeKeyword.value || searchLoading.value) return;

  searchLoading.value = true;
  searchError.value = "";

  // 不傳 boardId，代表全站搜尋
  return searchPostsApi(
    activeKeyword.value,
    searchPage.value - 1,
    searchPageSize,
  )
    .then((response) => {
      searchResults.value = response.data.content;
      searchTotalPages.value = response.data.totalPages;
      searchTotalElements.value = response.data.totalElements;
    })
    .catch((error) => {
      console.error("搜尋文章失敗：", error);
      searchError.value = "搜尋失敗，請稍後再試";
      searchResults.value = [];
      searchTotalPages.value = 0;
      searchTotalElements.value = 0;
    })
    .finally(() => {
      searchLoading.value = false;
    });
};

// 按搜尋或 Enter
const handleSearch = () => {
  if (searchLoading.value) return;

  const keyword = (searchKeyword.value ?? "").trim();

  if (!keyword) {
    clearSearch();
    return;
  }

  activeKeyword.value = keyword;
  searchPage.value = 1;
  fetchSearchResults();
};

// 清除搜尋
const clearSearch = () => {
  if (searchLoading.value) return;

  searchKeyword.value = "";
  activeKeyword.value = "";
  searchResults.value = [];
  searchPage.value = 1;
  searchTotalPages.value = 0;
  searchTotalElements.value = 0;
  searchError.value = "";
};

// 使用首頁已載入的看板資料，顯示看板名稱
const getBoardName = (boardId) => {
  const allBoards = Object.values(boards.value).flat();
  const board = allBoards.find((item) => String(item.id) === String(boardId));

  return board ? board.name : `看板 ${boardId}`;
};
// =========================
// 頁面載入時執行
// =========================
onMounted(() => {
  loadHomePage();
});
</script>
