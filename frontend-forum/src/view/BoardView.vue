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

// Router
const route = useRoute();

const router = useRouter();

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

const errorMessage = ref("");

// ======================
// 取得 Board
// ======================

const fetchBoard = () => {

    httpClient({
        method: 'get',
        url: `/api/boards/${boardId}`
    })
    .then(response => {

        console.log('===== Board API =====')
        console.log('URL:', `/api/boards/${boardId}`)
        console.log('DATA:', response.data)

        board.value = response.data

    })
    .catch(error => {

        console.log(error)

        errorMessage.value = '找不到這個討論版'

    })

}

// ======================
// 取得 Posts
// ======================

const fetchPosts = () => {
  loading.value = true;
  errorMessage.value = "";

  httpClient({
    method: 'get',
    url: `/api/posts/boards/${boardId}`,
    // 💡 註：GET 請求建議使用 params。若您的 httpClient 封裝特殊必須用 data，再自行改回 data。
    params: {
      page: page.value - 1,
      size: pageSize.value
    }
  })
    .then(response => {
      console.log('===== Posts API =====');
      console.log('URL:', `/api/posts/boards/${boardId}`);
      console.log('DATA:', response.data);

      // 因後端 Controller 回傳 List<Posts> (純陣列)，故直接賦值
      posts.value = response.data.content;
      totalPages.value = response.data.totalPages;
      
      loading.value = false;
    })
    .catch(error => {
      console.log(error);
      errorMessage.value = '文章取得失敗';
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
    alert("系統提示：找不到目前的看板 ID，請重新整理頁面再試一次。");
    return;
  }

  // 2. 執行跳轉與錯誤處理（改為統一的 then / catch 風格）
  router.push(`/post/create?boardId=${currentBoardId}`)
    .then(() => {
      console.log('===== 路由跳轉成功 =====');
      console.log('URL:', `/post/create?boardId=${currentBoardId}`);
    })
    .catch(error => {
      console.log('===== 路由跳轉失敗 =====');
      console.log(error);
      alert("跳轉失敗！請確認你的 router/index.js 裡是否有設定 /post/create 這個路徑。");
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
