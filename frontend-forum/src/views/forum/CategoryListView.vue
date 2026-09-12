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
        <!-- 左側搜尋與分類導覽 -->
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

        <!-- 右側分類、看板與搜尋結果 -->
        <div class="forum-content">
          <!-- 文章搜尋結果 -->
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

          <!-- 資料載入中 -->
          <div v-if="loading">
            <v-skeleton-loader
              v-for="n in 3"
              :key="n"
              type="list-item-avatar-two-line"
              class="mb-4 rounded-lg"
            />
          </div>

          <!-- 分類與看板列表 -->
          <template v-else-if="categories.length > 0">
            <ForumCategorySection
              v-for="category in categories"
              :key="category.id"
              :category="category"
              :get-category-icon="getCategoryIcon"
              :get-category-style="getCategoryStyle"
            />
          </template>

          <!-- 沒有分類資料 -->
          <div v-else class="text-center py-12">
            <v-icon size="56" color="grey-lighten-1" class="mb-4">
              mdi-folder-open-outline
            </v-icon>

            <p class="text-medium-emphasis">
              目前還沒有任何分類，請稍後再試
            </p>
          </div>
        </div>
      </div>
    </v-container>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import httpClient from "@/services/httpClient";
import { getPostsByBoard } from "@/services/postService";
import { usePostSearch } from "@/composables/usePostSearch";
import ForumCategorySection from "@/components/forum/ForumCategorySection.vue";
import ForumSidebar from "@/components/forum/ForumSidebar.vue";
import PostSearchResults from "@/components/post/PostSearchResults.vue";

// ======================
// 頁面資料狀態
// ======================

// 儲存所有類別及其看板和最新文章
const categories = ref([]);

// 控制首頁資料的載入狀態
const loading = ref(true);

// ======================
// 文章搜尋功能
// ======================

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

// ======================
// 取得看板最新文章
// ======================

const fetchBoardLatestPost = (board) => {
  return getPostsByBoard(board.id, 0, 1, "latest")
    .then((response) => {
      return {
        ...board,
        latestPost: response.data.content[0] ?? null,
        latestPostError: false,
      };
    })
    .catch((error) => {
      console.error(`看板 ${board.id} 最新文章載入失敗：`, error);

      // 單一看板載入失敗時保留看板資料
      return {
        ...board,
        latestPost: null,
        latestPostError: true,
      };
    });
};

// ======================
// 取得類別下的看板
// ======================

const fetchCategoryBoards = (category) => {
  return httpClient({
    method: "get",
    url: `/api/boards/category/${category.id}`,
  })
    .then((response) => {
      // 同時取得每個看板的最新文章
      return Promise.all(response.data.map(fetchBoardLatestPost));
    })
    .then((boards) => {
      return {
        ...category,
        boards,
      };
    });
};

// ======================
// 取得論壇首頁資料
// ======================

const fetchData = () => {
  loading.value = true;

  return httpClient({
    method: "get",
    url: "/api/categories",
  })
    .then((response) => {
      // 依照 rank 由小到大排列類別
      const sortedCategories = [...response.data].sort(
        (a, b) => (a.rank ?? 0) - (b.rank ?? 0),
      );

      // 同時取得每個類別下的看板
      return Promise.all(sortedCategories.map(fetchCategoryBoards));
    })
    .then((categoriesWithBoards) => {
      categories.value = categoriesWithBoards;
    })
    .catch((error) => {
      console.error(
        "資料載入失敗，請檢查 API 路由或後端伺服器：",
        error,
      );
      categories.value = [];
    })
    .finally(() => {
      loading.value = false;
    });
};

// ======================
// 取得類別圖示
// ======================

const getCategoryIcon = (name = "") => {
  if (name.includes("遊戲")) return "🎮";
  if (name.includes("動漫")) return "🎬";
  if (name.includes("科技")) return "💻";
  if (name.includes("美食")) return "🍕";
  if (name.includes("休閒")) return "🏖️";

  return "📌";
};

// ======================
// 取得類別樣式
// ======================

const getCategoryStyle = (name = "") => {
  // 預設使用藍色系
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

// ======================
// 移動到指定類別
// ======================

const scrollToCategory = (categoryId) => {
  const target = document.getElementById(`category-${categoryId}`);

  // 找不到類別區塊時停止執行
  if (!target) {
    return;
  }

  // 使用者偏好減少動畫時停用平滑捲動
  const reduceMotion = window.matchMedia(
    "(prefers-reduced-motion: reduce)",
  ).matches;

  target.scrollIntoView({
    behavior: reduceMotion ? "auto" : "smooth",
    block: "start",
  });
};

// ======================
// 取得文章所屬看板名稱
// ======================

const getBoardName = (boardId) => {
  // 將每個類別的看板合併成單一陣列
  const allBoards = categories.value.flatMap(
    (category) => category.boards ?? [],
  );

  const board = allBoards.find(
    (item) => String(item.id) === String(boardId),
  );

  return board ? board.name : `看板 ${boardId}`;
};

// ======================
// 切換搜尋結果頁碼
// ======================

const handleSearchPageChange = (page) => {
  // 搜尋進行中時不允許重複送出請求
  if (searchLoading.value) {
    return;
  }

  searchPage.value = page;
  fetchSearchResults();
};

// ======================
// 元件生命週期
// ======================

// 元件載入完成後取得論壇首頁資料
onMounted(fetchData);
</script>

<style scoped>
.category-page {
  min-height: 100%;
  background: #f5f7fa;
}

.page-heading {
  margin-bottom: 36px;
}

/* 左側固定 200px，右側使用剩餘空間 */
.forum-layout {
  display: grid;
  grid-template-columns: 200px minmax(0, 1fr);
  align-items: start;
  gap: 24px;
}

.forum-content {
  min-width: 0;
}

/* 平板與手機將分類導覽移到上方 */
@media (max-width: 959px) {
  .forum-layout {
    grid-template-columns: minmax(0, 1fr);
    gap: 20px;
  }
}
</style>