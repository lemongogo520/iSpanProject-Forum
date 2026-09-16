<template>
  <v-container fluid class="board-container py-8">
    <!-- 返回 -->
    <v-btn
      variant="text"
      prepend-icon="mdi-arrow-left"
      class="mb-4"
      @click="goBack"
    >
      返回討論版
    </v-btn>

    <!-- 載入中 -->
    <div v-if="loading" class="text-center py-10">
      <v-progress-circular indeterminate color="primary" size="50" />
    </div>

    <!-- 錯誤 -->
    <v-alert v-else-if="errorMessage" type="error" variant="tonal" class="mb-6">
      {{ errorMessage }}
    </v-alert>

    <!-- 看板 -->
    <template v-else>
      <!-- 看板標題 -->
      <v-card class="mb-6 rounded-xl" variant="tonal">
        <v-card-item>
          <v-card-title class="text-h4">
            {{ board.name }}
          </v-card-title>

          <v-card-subtitle v-if="board.description" class="mt-2">
            {{ board.description }}
          </v-card-subtitle>
        </v-card-item>
      </v-card>
      <!-- 左側文章、右側聊天室 -->
      <div class="board-layout">
        <main class="board-main">
          <!-- 看板內搜尋 -->
          <v-form class="mb-4" @submit.prevent="handleSearch">
            <div class="d-flex align-center ga-2">
              <v-text-field
                v-model="searchKeyword"
                label="搜尋此看板的文章標題"
                prepend-inner-icon="mdi-magnify"
                variant="outlined"
                hide-details
                clearable
                :disabled="loading"
                @click:clear="clearSearch"
              />

              <v-select
                v-model="sortBy"
                :items="sortOptions"
                item-title="title"
                item-value="value"
                label="文章排序"
                variant="outlined"
                hide-details
                :disabled="loading"
                style="max-width: 180px"
                @update:model-value="handleSortChange"
              />

              <v-btn type="submit" color="primary" :disabled="loading">
                搜尋
              </v-btn>
            </div>
          </v-form>
          <!-- 發表文章 -->
          <div class="d-flex justify-end mb-4">
            <v-btn
              color="primary"
              prepend-icon="mdi-pencil"
              @click="createPost"
            >
              發表文章
            </v-btn>
          </div>

          <!-- 文章列表 -->
          <v-card
            v-for="post in posts"
            :key="post.id"
            class="forum-post mb-2"
            variant="flat"
            @click="openPost(post.id)"
          >
            <v-card-text class="post-row pa-3">
              <!-- 左側縮圖 -->
              <div class="post-thumbnail">
                <v-img
                  :src="getFirstImage(post.content) || defaultPostImage"
                  width="130"
                  height="80"
                  cover
                />
              </div>
              <!-- 中間文章資訊 -->
              <div class="post-main">
                <div class="post-heading">
                  <v-chip color="primary" variant="tonal" size="small">
                    {{ post.postType || "討論" }}
                  </v-chip>

                  <div class="post-title">
                    {{ post.title }}
                  </div>
                </div>

                <div class="post-excerpt">
                  {{ getPostExcerpt(post.content) }}
                </div>
              </div>

              <!-- 右側統計資訊 -->
              <div class="post-stats">
                <div class="stat-row">
                  <span class="stat-item">
                    <v-icon icon="mdi-comment-outline" size="15" />

                    {{ post.comments?.length ?? 0 }}
                  </span>

                  <span class="stat-divider">/</span>

                  <span class="stat-item">
                    <v-icon icon="mdi-eye-outline" size="15" />

                    {{ post.views ?? 0 }}
                  </span>
                </div>

                <div class="post-date">
                  {{ formatPostDate(post.createTime) }}
                </div>
              </div>
            </v-card-text>
          </v-card>
          <!-- 沒有文章 -->
          <v-card
            v-if="posts.length === 0"
            class="pa-8 text-center"
            variant="tonal"
          >
            <v-icon icon="mdi-forum-outline" size="50" class="mb-3" />

            <div class="text-h6">
              {{ activeKeyword ? "找不到符合條件的文章" : "目前沒有文章" }}
            </div>

            <div class="text-body-2 text-medium-emphasis mt-2">
              {{
                activeKeyword
                  ? "請嘗試其他關鍵字"
                  : "成為第一個發表文章的人吧！"
              }}
            </div>
          </v-card>

          <!-- 分頁 -->
          <v-pagination
            v-if="totalPages > 1"
            v-model="page"
            :length="totalPages"
            class="mt-6"
            @update:model-value="fetchPosts"
          />
        </main>

        <!-- 右側聊天室 -->
        <aside class="board-chat-column">
          <BoardChatRoom :board-id="boardId" :board-name="board.name" />
        </aside>
      </div>
    </template>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

import httpClient from "@/services/httpClient";
import {
  getPostsByBoard,
  searchPosts as searchPostsApi,
} from "@/services/postService";

import { useNotification } from "@/composables/useNotification";

import BoardChatRoom from "@/components/forum/BoardChatRoom.vue";

import defaultPostImage from "@/assets/images/default-post.png";
const { showMessage } = useNotification();
// Router
const route = useRoute();
const router = useRouter();

// Snackbar 狀態
const errorMessage = ref("");

// Board ID
const boardId = route.params.boardId;

// Board
const board = ref({});

// Posts
const posts = ref([]);

// 搜尋框正在輸入的文字
const searchKeyword = ref("");
// 已按下搜尋、實際用來查詢的關鍵字
const activeKeyword = ref("");
// 文章排序
const sortBy = ref("latest");

const sortOptions = [
  {
    title: "最新發布",
    value: "latest",
  },
  {
    title: "最多瀏覽",
    value: "views",
  },
  {
    title: "最多點讚",
    value: "likes",
  },
];
// 分頁
const page = ref(1);


const totalPages = ref(0);

// 狀態
const loading = ref(false);

// ======================
// 取得 Board
// ======================

const fetchBoard = () => {
  httpClient({
    method: "get",
    url: `/api/boards/${boardId}`,
  })
    .then((response) => {
      board.value = response.data;
    })
    .catch((error) => {
      console.error("取得看板失敗：", error);
      errorMessage.value = "找不到這個討論版";
    });
};

// ======================
// 取得 Posts
// ======================
const fetchPosts = () => {
  loading.value = true;
  errorMessage.value = "";

  let request;

  if (activeKeyword.value) {
    // 搜尋目前看板的文章
    request = searchPostsApi(
      activeKeyword.value,
      page.value - 1,
      boardId,
      sortBy.value,
    );
  } else {
    // 沒有關鍵字，顯示原本的看板文章
    request = getPostsByBoard(
      boardId,
      page.value - 1,
      sortBy.value,
    );
  }

  return request
    .then((response) => {
      posts.value = response.data.content;
      totalPages.value = response.data.totalPages;
    })
    .catch((error) => {
      console.error("取得文章失敗：", error);
      errorMessage.value = "文章取得失敗";
    })
    .finally(() => {
      loading.value = false;
    });
};
// ======================
// 擷取文章第一張圖片
// ======================
const getFirstImage = (content) => {
  if (!content) return "";

  const document = new DOMParser().parseFromString(content, "text/html");

  return document.querySelector("img")?.src ?? "";
};
const getPostExcerpt = (content) => {
  if (!content) return "這篇文章目前沒有文字摘要";

  const document = new DOMParser().parseFromString(content, "text/html");

  const text = document.body.textContent?.replace(/\s+/g, " ").trim() ?? "";

  return text || "此文章包含圖片或影片內容";
};
// ======================
// 搜尋功能
// ======================
const handleSearch = () => {
  if (loading.value) return;

  activeKeyword.value = (searchKeyword.value ?? "").trim();

  // 換關鍵字時回到第一頁
  page.value = 1;
  fetchPosts();
};
// ======================
// 切換文章排序
// ======================
const handleSortChange = () => {
  if (loading.value) return;

  // 切換排序後回到第一頁
  page.value = 1;
  fetchPosts();
};

// 點搜尋框的清除按鈕
const clearSearch = () => {
  if (loading.value) return;

  searchKeyword.value = "";
  activeKeyword.value = "";
  page.value = 1;

  // 恢復原本的看板文章列表
  fetchPosts();
};

// ======================
// 點擊文章
// ======================

const openPost = (postId) => {
  router.push(`/post/${postId}`);
};

// ======================
// 發表文章
// ======================
const createPost = () => {
  // 1. 安全機制：優先從已經從後端拿到的 board 物件中取 id
  const currentBoardId = board.value?.id || boardId;

  if (!currentBoardId) {
    showMessage("找不到目前的看板 ID，請重新整理頁面再試一次", "error");
    return;
  }
  router
    .push({
      name: "PostCreate",
      query: {
        boardId: currentBoardId,
      },
    })
    .catch((error) => {
      console.error("跳轉發文頁失敗：", error);
      showMessage("跳轉失敗，請確認發文頁路由設定", "error");
    });
};
const formatPostDate = (date) => {
  if (!date) return "";

  return new Date(date).toLocaleDateString("zh-TW", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};
// ======================
// 返回首頁
// ======================

const goBack = () => {
  router.push("/categories");
};

// ======================
// 頁面載入
// ======================

onMounted(() => {
  fetchBoard();

  fetchPosts();
});
</script>
<style scoped>
.board-container {
  width: 100%;
  max-width: 1680px;
  margin: 0 auto;
  padding-right: 32px;
  padding-left: 32px;
}
.board-layout {
  display: grid;
  grid-template-columns: 200px minmax(0, 1fr) 320px;
  gap: 24px;
  align-items: start;
}

.board-main {
  grid-column: 2;
  min-width: 0;
}
.board-chat-column {
  grid-column: 3;
  position: sticky;
  top: 72px;
  min-width: 0;
}

/* 中型螢幕：取消左側 200px 保留空間 */
@media (max-width: 1399px) {
  .board-layout {
    grid-template-columns: minmax(0, 1fr) 300px;
  }

  .board-main {
    grid-column: 1;
  }

  .board-chat-column {
    grid-column: 2;
  }
}

/* 平板與手機：文章與聊天室改成上下排列 */
@media (max-width: 959px) {
  .board-layout {
    grid-template-columns: minmax(0, 1fr);
  }

  .board-main,
  .board-chat-column {
    grid-column: 1;
  }

  .board-chat-column {
    position: static;
  }
}
.post-card {
  cursor: pointer;
  background-color: #ffffff;
  border: 1px solid #e4e7ec;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease;
}

.post-card:hover {
  transform: translateY(-2px);
  border-color: #90caf9;
  box-shadow: 0 6px 18px rgb(0 0 0 / 10%);
}
.post-meta {
  color: #757575;
  font-size: 13px;
}

.post-open-icon {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  color: #1976d2;
  background-color: #e3f2fd;
  border-radius: 50%;
  transition:
    color 0.2s ease,
    background-color 0.2s ease,
    transform 0.2s ease;
}

.post-card:hover .post-open-icon {
  color: #ffffff;
  background-color: #1976d2;
  transform: translateX(3px);
}

@media (max-width: 600px) {
  .post-title {
    font-size: 15px;
  }

  .post-meta {
    flex-wrap: wrap;
    gap: 8px !important;
  }
}
.forum-post {
  cursor: pointer;
  background-color: #ffffff;
  border: 1px solid #e5e7eb;
  transition:
    background-color 0.2s ease,
    border-color 0.2s ease;
}

.forum-post:hover {
  background-color: #f8fbff;
  border-color: #90caf9;
}

.post-row {
  display: flex;
  align-items: center;
  gap: 18px;
  min-height: 104px;
}

.post-thumbnail {
  flex-shrink: 0;
  width: 130px;
  height: 80px;
  overflow: hidden;
  background-color: #f5f5f5;
  border-radius: 6px;
}
.post-main {
  flex: 1;
  min-width: 0;
}

.post-heading {
  display: flex;
  align-items: center;
  gap: 10px;
}

.post-title {
  overflow: hidden;
  color: #18181b;
  font-size: 17px;
  font-weight: 700;
  line-height: 1.4;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-excerpt {
  display: -webkit-box;
  margin-top: 9px;
  overflow: hidden;
  color: #6b7280;
  font-size: 14px;
  line-height: 1.5;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.post-stats {
  flex-shrink: 0;
  min-width: 125px;
  color: #4b5563;
  font-size: 13px;
  text-align: right;
}

.stat-row {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 6px;
}

.stat-item {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}

.stat-divider {
  color: #bdbdbd;
}

.post-date {
  margin-top: 7px;
  color: #757575;
}
@media (max-width: 700px) {
  .post-row {
    align-items: flex-start;
    flex-wrap: wrap;
    gap: 12px;
  }

  .post-thumbnail {
    width: 90px;
    height: 65px;
  }

  .post-main {
    width: calc(100% - 102px);
  }

  .post-stats {
    display: flex;
    justify-content: space-between;
    width: 100%;
    padding-left: 102px;
    text-align: left;
  }
  .post-date {
    margin-top: 0;
  }
}
</style>
