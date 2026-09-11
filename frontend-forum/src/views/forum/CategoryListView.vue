<template>
  <div class="category-page">
    <v-container class="py-8">
      <!-- 頁面標題 -->
      <header class="page-heading">
        <h1 class="text-h4 font-weight-bold mb-3">探索論壇</h1>
        <p class="text-body-1 text-medium-emphasis">
          找到感興趣的看板，開始參與討論。
        </p>
      </header>
      <div class="forum-layout">
        <!-- 左側：搜尋與分類導覽 -->
        <ForumSidebar
          v-model:keyword="searchKeyword"
          :categories="categories"
          :loading="loading"
          :search-loading="searchLoading"
          :get-category-icon="getCategoryIcon"
          @search="handleSearch"
          @clear="clearSearch"
          @select-category="scrollToCategory"
        />

        <!-- 右側：原本的分類與看板 -->
        <div class="forum-content">
          <!-- 搜尋結果 -->
          <PostSearchResults
            :keyword="activeKeyword"
            :results="searchResults"
            :loading="searchLoading"
            :error="searchError"
            :page="searchPage"
            :total-pages="searchTotalPages"
            :total-elements="searchTotalElements"
            :get-board-name="getBoardName"
            @clear="clearSearch"
            @change-page="handleSearchPageChange"
          />
          <!-- 載入中 -->
          <div v-if="loading">
            <v-skeleton-loader
              v-for="n in 3"
              :key="n"
              type="list-item-avatar-two-line"
              class="mb-4 rounded-lg"
            />
          </div>

          <!-- 分類列表 -->
          <template v-else-if="categories.length > 0">
            <ForumCategorySection
              v-for="category in categories"
              :key="category.id"
              :category="category"
              :get-category-icon="getCategoryIcon"
              :get-category-style="getCategoryStyle"
            />
          </template>

          <!-- 沒有分類 -->
          <div v-else class="text-center py-12">
            <v-icon size="56" color="grey-lighten-1" class="mb-4">
              mdi-folder-open-outline
            </v-icon>

            <p class="text-medium-emphasis">目前還沒有任何分類，請稍後再試</p>
          </div>
        </div>
        <!-- 結束 forum-content -->
      </div>
      <!-- 結束 forum-layout -->
    </v-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import httpClient from "@/services/httpClient";
// API匯入
import { getPostsByBoard } from "@/services/postService";
// 搜尋規則匯入
import { usePostSearch } from "@/composables/usePostSearch";
// compnents匯入
import PostSearchResults from "@/components/post/PostSearchResults.vue";
import ForumSidebar from "@/components/forum/ForumSidebar.vue";
import ForumCategorySection from "@/components/forum/ForumCategorySection.vue";

const categories = ref([]); // 存放大分類與其子看板的完整陣列
const loading = ref(true);

// 使用你指定的 httpClient 格式獲取資料
const fetchData = async () => {
  loading.value = true;
  try {
    // 1. 先獲取所有大分類
    const resCategories = await httpClient({
      method: "get",
      url: "/api/categories",
      data: {},
    });

    // 將大分類陣列進行權重排序
    const rawCategories = [...resCategories.data].sort(
      (a, b) => (a.rank || 0) - (b.rank || 0),
    );

    // 2. 遍歷每個大分類，對接你的 @GetMapping("/api/boards/category/{categoryId}")
    for (let cat of rawCategories) {
      const resBoards = await httpClient({
        method: "get",
        url: `/api/boards/category/${cat.id}`, // 動態帶入大分類 ID
        data: {},
      });

      cat.boards = await Promise.all(
        resBoards.data.map(async (board) => {
          try {
            // 查詢這個看板的第 0 頁，每頁 1 篇，依最新發布排序
            const response = await getPostsByBoard(board.id, 0, 1, "latest");

            return {
              ...board,
              latestPost: response.data.content[0] ?? null,
              latestPostError: false,
            };
          } catch (error) {
            console.error(`看板 ${board.id} 最新文章載入失敗：`, error);

            // 最新文章查詢失敗，仍保留看板
            return {
              ...board,
              latestPost: null,
              latestPostError: true,
            };
          }
        }),
      );
    }

    // 3. 把組裝好 boards 的完整資料丟給響應式變數渲染畫面
    categories.value = rawCategories;
  } catch (error) {
    console.error("資料載入失敗，請檢查 API 路由或後端伺服器:", error);
  } finally {
    loading.value = false;
  }
};

// 根據大分類名稱自動配對精美 Emoji 表情
const getCategoryIcon = (name) => {
  if (name.includes("遊戲")) return "🎮";
  if (name.includes("動漫")) return "🎬";
  if (name.includes("科技")) return "💻";
  if (name.includes("美食")) return "🍕";
  if (name.includes("休閒")) return "🏖️";
  return "📌";
};

const getCategoryStyle = (name = "") => {
  let accent = "#2563eb";
  let soft = "#eff6ff";

  if (name.includes("美食")) {
    accent = "#b45309";
    soft = "#fff7ed";
  } else if (name.includes("動漫")) {
    accent = "#7c3aed";
    soft = "#f5f3ff";
  } else if (name.includes("科技")) {
    accent = "#0e7490";
    soft = "#ecfeff";
  } else if (name.includes("休閒")) {
    accent = "#047857";
    soft = "#ecfdf5";
  }

  return {
    "--category-accent": accent,
    "--category-soft": soft,
  };
};

const scrollToCategory = (categoryId) => {
  const target = document.getElementById(`category-${categoryId}`);

  if (!target) return;

  const reduceMotion = window.matchMedia(
    "(prefers-reduced-motion: reduce)",
  ).matches;

  target.scrollIntoView({
    behavior: reduceMotion ? "auto" : "smooth",
    block: "start",
  });
};

const {
  searchKeyword,
  activeKeyword,
  searchResults,
  searchLoading,
  searchError,
  searchPage,
  searchTotalPages,
  searchTotalElements,
  fetchSearchResults,
  clearSearch,
  handleSearch,
} = usePostSearch();

// 取得搜尋文章所屬的看板名稱
const getBoardName = (boardId) => {
  const allBoards = categories.value.flatMap(
    (category) => category.boards ?? [],
  );

  const board = allBoards.find((item) => String(item.id) === String(boardId));

  return board ? board.name : `看板 ${boardId}`;
};

const handleSearchPageChange = (page) => {
  if (searchLoading.value) return;

  searchPage.value = page;
  fetchSearchResults();
};
onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.category-page {
  min-height: 100%;
  background: #f5f7fa;
}

.page-heading {
  margin-bottom: 36px;
}

/* 左側 200px，右側使用剩餘空間 */
.forum-layout {
  display: grid;
  grid-template-columns: 200px minmax(0, 1fr);
  align-items: start;
  gap: 24px;
}

.forum-content {
  min-width: 0;
}
/* 平板、手機：分類導覽移到上方 */
@media (max-width: 959px) {
  .forum-layout {
    grid-template-columns: minmax(0, 1fr);
    gap: 20px;
  }
}
</style>
