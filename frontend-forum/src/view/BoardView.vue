<template>
  <v-container class="py-8">
    <!-- 返回 -->
    <v-btn
      variant="text"
      prepend-icon="mdi-arrow-left"
      class="mb-4"
      @click="goBack"
    >
      返回首頁
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

      <!-- 發表文章 -->
      <div class="d-flex justify-end mb-4">
        <v-btn color="primary" prepend-icon="mdi-pencil" @click="createPost">
          發表文章
        </v-btn>
      </div>

      <!-- 文章列表 -->
      <v-card
        v-for="post in posts"
        :key="post.id"
        class="mb-3 rounded-lg"
        hover
        @click="openPost(post.id)"
      >
        <v-card-item>
          <v-card-title>
            {{ post.title }}
          </v-card-title>

          <v-card-subtitle class="mt-2">
            瀏覽：{{ post.views }}
          </v-card-subtitle>
        </v-card-item>
      </v-card>

      <!-- 沒有文章 -->
      <v-card
        v-if="posts.length === 0"
        class="pa-8 text-center"
        variant="tonal"
      >
        <v-icon icon="mdi-forum-outline" size="50" class="mb-3" />

        <div class="text-h6">目前沒有文章</div>

        <div class="text-body-2 text-medium-emphasis mt-2">
          成為第一個發表文章的人吧！
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
    </template>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from "vue";

import { useRoute, useRouter } from "vue-router";

import httpClient from "@/api/httpClient";
import { getPostsByBoard } from "@/api/postApi";

import { useNotification } from "@/composables/useNotification";
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

// 分頁
const page = ref(1);

const pageSize = ref(10);

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

  getPostsByBoard(boardId, page.value - 1, pageSize.value)
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
  const currentBoardId = board.value.id || boardId;

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
// ======================
// 返回首頁
// ======================

const goBack = () => {
  router.push("/");
};

// ======================
// 頁面載入
// ======================

onMounted(() => {
  fetchBoard();

  fetchPosts();
});
</script>
