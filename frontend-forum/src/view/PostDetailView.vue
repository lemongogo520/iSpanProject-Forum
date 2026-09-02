<template>
  <v-container class="py-8" max-width="1200">
    <!-- 返回看板 -->
    <v-btn
      variant="text"
      prepend-icon="mdi-arrow-left"
      class="mb-4"
      @click="goBack"
    >
      返回看板
    </v-btn>
    <!-- 載入中 -->
    <div v-if="loading" class="text-center py-10">
      <v-progress-circular indeterminate color="primary" size="50" />
    </div>
    <!-- 錯誤 -->
    <v-alert v-else-if="errorMessage" type="error" variant="tonal" class="mb-6">
      {{ errorMessage }}
    </v-alert>
    <!-- 文章 -->
    <template v-else-if="post">
      <v-card elevation="1" class="post-card">
        <v-row no-gutters>
          <!-- ====================== -->
          <!-- 作者區 -->
          <!-- ====================== -->
          <v-col cols="12" md="2" class="author-area">
            <v-avatar size="90" class="mb-4">
              <v-icon icon="mdi-account" size="55" />
            </v-avatar>
            <div class="text-subtitle-1 font-weight-bold">
              {{ getUserName(post) }}
            </div>
            <div class="text-caption text-medium-emphasis mt-1">使用者</div>
          </v-col>
          <!-- ====================== -->
          <!-- 文章內容 -->
          <!-- ====================== -->
          <v-col cols="12" md="10" class="pa-6">
            <!-- 發布時間 -->
            <div class="text-subtitle-2 text-medium-emphasis mb-4">
              {{ formatDate(post.createTime) }}
            </div>
            <!-- 標題 -->
            <v-card-title class="px-0 text-h5 font-weight-bold">
              {{ post.title }}
            </v-card-title>
            <v-divider class="my-4" />
            <!-- 文章內容 -->
            <v-card-text class="px-0 post-content" v-html="post.content" />
            <!-- 標籤 -->
            <div class="d-flex align-center flex-wrap ga-2 mt-6">
              <span class="font-weight-bold mr-2"> 探索主題 </span>
              <v-chip size="small"> 電腦 </v-chip>
              <v-chip size="small"> 遊戲 </v-chip>
              <v-chip size="small"> 科技 </v-chip>
            </div>
          </v-col>
        </v-row>
      </v-card>
      <!-- ====================== -->
      <!-- 按讚 / 倒讚 -->
      <!-- ====================== -->
      <div class="reaction-area">
        <v-btn variant="text" prepend-icon="mdi-thumb-up-outline">
          {{ likeCount }}
        </v-btn>
        <v-btn variant="text" prepend-icon="mdi-thumb-down-outline"> - </v-btn>
        <v-spacer />
        <!-- 更多 -->
        <v-menu>
          <template #activator="{ props }">
            <v-btn v-bind="props" icon="mdi-dots-vertical" variant="text" />
          </template>
          <v-list>
            <v-list-item title="編輯文章" />
            <v-list-item title="刪除文章" />
          </v-list>
        </v-menu>
      </div>
      <!-- ====================== -->
      <!-- 留言標題 -->
      <!-- ====================== -->
      <div class="comment-title">
        <v-icon icon="mdi-chevron-down" size="20" />
        <span> 留言 </span>
      </div>
      <!-- ====================== -->
      <!-- 沒有留言 -->
      <!-- ====================== -->
      <v-card
        v-if="comments.length === 0"
        variant="tonal"
        class="pa-6 text-center mb-4"
      >
        <v-icon icon="mdi-comment-outline" size="40" class="mb-2" />
        <div class="text-body-1">目前還沒有留言</div>
        <div class="text-caption text-medium-emphasis mt-1">
          成為第一個留言的人吧！
        </div>
      </v-card>
      <!-- ====================== -->
      <!-- 留言列表 -->
      <!-- ====================== -->
      <v-card
        v-for="comment in comments"
        :key="comment.id"
        class="comment-card mb-3 pa-4"
      >
        <!-- 父留言編輯模式 -->
        <template v-if="editingCommentId === comment.id">
          <v-textarea
            v-model="editingContent"
            label="編輯留言"
            variant="outlined"
            rows="3"
            auto-grow
          />

          <div class="d-flex justify-end ga-2">
            <v-btn
              size="small"
              variant="text"
              :disabled="commentUpdating"
              @click="cancelEditComment"
            >
              取消
            </v-btn>

            <v-btn
              size="small"
              color="primary"
              variant="flat"
              :loading="commentUpdating"
              @click="updateComment(comment.id)"
            >
              儲存
            </v-btn>
          </div>
        </template>

        <!-- 父留言一般顯示模式 -->
        <template v-else>
          <!-- 父留言內容 -->
          <div class="mb-3" :class="{ 'text-grey': comment.isDeleted }">
            {{ comment.content }}
          </div>

          <!-- 未刪除留言的操作按鈕 -->
          <div v-if="!comment.isDeleted" class="d-flex justify-end ga-2">
            <!-- 回覆 -->
            <v-btn
              size="small"
              color="primary"
              variant="text"
              prepend-icon="mdi-reply"
              @click.stop="startReply(comment.id)"
            >
              回覆
            </v-btn>

            <!-- 編輯 -->
            <v-btn
              size="small"
              color="primary"
              variant="outlined"
              prepend-icon="mdi-pencil-outline"
              @click.stop="startEditComment(comment)"
            >
              編輯
            </v-btn>

            <!-- 刪除 -->
            <v-btn
              size="small"
              color="error"
              variant="outlined"
              prepend-icon="mdi-delete-outline"
              @click.stop="deleteComment(comment.id)"
            >
              刪除
            </v-btn>

            <!-- 查看或收合回覆 -->
            <v-btn
              size="small"
              variant="text"
              prepend-icon="mdi-comment-outline"
              @click.stop="toggleReplies(comment.id)"
            >
              {{ expandedComments[comment.id] ? "收合回覆" : "查看回覆" }}
            </v-btn>
          </div>

          <!-- 回覆輸入框 -->
          <div
            v-if="!comment.isDeleted && replyingCommentId === comment.id"
            class="mt-4 ml-6"
          >
            <v-textarea
              v-model="replyContent"
              label="回覆這則留言"
              placeholder="輸入回覆內容..."
              variant="outlined"
              rows="2"
              auto-grow
            />

            <div class="d-flex justify-end ga-2">
              <v-btn
                size="small"
                variant="text"
                :disabled="replySubmitting"
                @click="cancelReply"
              >
                取消
              </v-btn>

              <v-btn
                size="small"
                color="primary"
                variant="flat"
                :loading="replySubmitting"
                @click="submitReply(comment.id)"
              >
                送出回覆
              </v-btn>
            </div>
          </div>

          <!-- 展開後顯示子留言 -->
          <div v-if="expandedComments[comment.id]" class="mt-4 ml-6">
            <v-divider class="mb-3" />

            <!-- 沒有子留言 -->
            <div
              v-if="
                !childComments[comment.id] ||
                childComments[comment.id].length === 0
              "
              class="text-caption text-grey"
            >
              目前沒有回覆
            </div>

            <!-- 子留言列表 -->
            <v-card
              v-for="child in childComments[comment.id] || []"
              :key="child.id"
              class="mb-2 pa-3"
              variant="tonal"
            >
              <div :class="{ 'text-grey': child.isDeleted }">
                {{ child.content }}
              </div>

              <div class="text-caption text-grey mt-2">
                {{ getUserName(child) }}
                ・
                {{ formatDate(child.createTime) }}
              </div>
            </v-card>
          </div>
        </template>
      </v-card>
      <!-- ====================== -->
      <!-- 留言分頁 -->
      <!-- ====================== -->
      <v-pagination
        v-if="totalCommentPages > 1"
        v-model="commentPage"
        :length="totalCommentPages"
        class="my-6"
        @update:model-value="fetchComments"
      />
      <!-- ====================== -->
      <!-- 發表留言 -->
      <!-- ====================== -->
      <v-card class="pa-5 mt-6" elevation="1">
        <div class="text-h6 mb-4">發表留言</div>
        <v-textarea
          v-model="newComment"
          label="留言內容"
          placeholder="輸入你的留言..."
          variant="outlined"
          rows="4"
          hide-details
        />
        <div class="d-flex justify-end mt-4">
          <v-btn
            color="primary"
            :loading="commentSubmitting"
            @click="submitComment"
          >
            發表留言
          </v-btn>
        </div>
      </v-card>
    </template>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import httpClient from "@/api/httpClient";
// ======================
// Router
// ======================
const route = useRoute();
const router = useRouter();
// ======================
// Post ID
// ======================
const postId = route.params.postId;
// ======================
// Post
// ======================
const post = ref(null);
// ======================
// 主留言Comments
// ======================
const comments = ref([]);
const commentPage = ref(1);
const commentPageSize = ref(5);
const totalCommentPages = ref(0);
// ======================
// 子留言childComment
// ======================
const childComments = ref({});
const replyingCommentId = ref(null);
const replyContent = ref("");
const replySubmitting = ref(false);
const expandedComments = ref({});
// ======================
// 更新留言
// ======================
const editingCommentId = ref(null);
const editingContent = ref("");
const commentUpdating = ref(false);
// ======================
// 新增留言
// ======================
const newComment = ref("");
const commentSubmitting = ref(false);
// ======================
// 按讚
// ======================
const likeCount = ref(0);
// ======================
// 狀態
// ======================
const loading = ref(false);
const errorMessage = ref("");
// ======================
// 取得 Post
// ======================
const fetchPost = () => {
  loading.value = true;
  errorMessage.value = "";
  httpClient({
    method: "get",
    url: `/api/posts/${postId}`,
  })
    .then((response) => {
      post.value = response.data;
      // 如果後端有 likes 就使用
      likeCount.value = response.data.likes || 0;
      loading.value = false;
    })
    .catch((error) => {
      console.log("===== Post API 失敗 =====");
      console.log(error);
      errorMessage.value = "文章取得失敗";
      loading.value = false;
    });
};
// ======================
// 取得 Comments
// ======================
const fetchComments = () => {
  httpClient({
    method: "get",
    url: `/api/comments/posts/${postId}`,
    params: {
      page: commentPage.value - 1,
      size: commentPageSize.value,
    },
  })
    .then((response) => {
      comments.value = response.data.content;
      totalCommentPages.value = response.data.totalPages;

      console.log("取得留言成功：", response.data);
    })
    .catch((error) => {
      console.error("取得留言失敗：", error);

      comments.value = [];
      totalCommentPages.value = 0;
    });
};
// ======================
// 取得子留言
// ======================
const fetchChildComments = (parentCommentId) => {
  httpClient({
    method: "get",
    url: `/api/comments/${parentCommentId}/children`,
  })
    .then((response) => {
      childComments.value[parentCommentId] = response.data;
      childComments.value = {
        ...childComments.value,
        [parentCommentId]: response.data,
      };
    })
    .catch((error) => {
      console.error("取得子留言失敗：", error);
      childComments.value ={...childComments.value,[parentCommentId] : []
      };
    });
};
const toggleReplies = (parentCommentId) => {
  const willExpand = !expandedComments.value[parentCommentId];

  // 更新展開狀態
  expandedComments.value = {
    ...expandedComments.value,
    [parentCommentId]: willExpand,
  };

  console.log("父留言 ID：", parentCommentId);
  console.log("是否展開：", willExpand);

  // 展開時查詢子留言
  if (willExpand) {
    fetchChildComments(parentCommentId);
  }
};
const startReply = (commentId) => {
  replyingCommentId.value = commentId;
  replyContent.value = "";
};
const cancelReply = () => {
  replyingCommentId.value = null;
  replyContent.value = "";
};
// ======================
// 新增子留言
// ======================
const submitReply = (parentCommentId) => {
  const content = replyContent.value.trim();

  if (!content) {
    alert("請輸入回覆內容");
    return;
  }

  replySubmitting.value = true;

  httpClient({
    method: "post",
    url: `/api/comments/posts/${postId}`,
    params: {
      parentCommentId: parentCommentId,
    },

    // 對應後端的 @RequestBody Comments
    data: {
      content: content,
    },
  })
    .then((response) => {
      console.log("回覆成功：", response.data);

      cancelReply();

      // 顯示回覆區
      expandedComments.value[parentCommentId] = true;

      // 重新取得這則父留言的所有回覆
      fetchChildComments(parentCommentId);
    })
    .catch((error) => {
      console.error("回覆失敗：", error);

      alert(error.response?.data ?? "回覆失敗");
    })
    .finally(() => {
      replySubmitting.value = false;
    });
};
// ======================
// 發表留言
// ======================
const submitComment = () => {
  const content = newComment.value.trim();
  if (!content) {
    alert("請輸入留言內容");
    return;
  }

  commentSubmitting.value = true;
  httpClient({
    method: "post",

    url: `/api/comments/posts/${postId}`,

    data: {
      content: content,
    },
  })
    .then((response) => {
      console.log("新增成功: ", response.data);
      newComment.value = "";
      commentPage.value = 1;
      fetchComments();
    })
    .catch((error) => {
      console.log("新增失敗", error.response?.data ?? error);
      alert("留言失敗，請稍後再試");
    })
    .finally(() => {
      commentSubmitting.value = false;
    });
};
// ======================
// 編輯留言
// ======================
const startEditComment = (comment) => {
  editingCommentId.value = comment.id;
  editingContent.value = comment.content;
};
const cancelEditComment = () => {
  editingCommentId.value = null;
  editingContent.value = "";
};
const updateComment = (commentId) => {
  const content = editingContent.value.trim();

  if (!content) {
    alert("留言內容不能為空白");
    return;
  }

  commentUpdating.value = true;

  httpClient({
    method: "put",
    url: `/api/comments/${commentId}`,
    data: {
      content: content,
    },
  })
    .then((response) => {
      console.log("修改留言成功：", response.data);

      cancelEditComment();
      fetchComments();
    })
    .catch((error) => {
      console.error("修改留言失敗：", error);
      alert(error.response?.data ?? "修改留言失敗");
    })
    .finally(() => {
      commentUpdating.value = false;
    });
};
// ======================
// 刪除留言
// ======================
const deleteComment = (commentId) => {
  if (!confirm("確定要刪除這則留言嗎？")) {
    return;
  }
  httpClient({
    method: "delete",
    url: `/api/comments/${commentId}`,
  })
    .then((response) => {
      console.log("刪除留言成功：", response.data);
      fetchComments();
    })
    .catch((error) => {
      console.error("刪除留言失敗：", error);
      alert(error.response?.data ?? "刪除留言失敗");
    });
};
// ======================
// 取得使用者名稱
// ======================
const getUserName = (data) => {
  if (!data) {
    return "使用者";
  }
  if (data.users) {
    return data.users.name || data.users.account || "使用者";
  }
  return "使用者";
};
// ======================
// 日期格式
// ======================
const formatDate = (date) => {
  if (!date) {
    return "";
  }
  return new Date(date).toLocaleString("zh-TW", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};
// ======================
// 返回
// ======================
const goBack = () => {
  router.back();
};
// ======================
// 頁面載入
// ======================
onMounted(() => {
  fetchPost();
  fetchComments();
});
</script>

<style scoped>
.post-card {
  overflow: hidden;
}
/* 作者 */
.author-area {
  background: #f7f7f7;
  border-right: 1px solid #e0e0e0;
  min-height: 320px;
  padding: 30px 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
/* 文章內容 */
.post-content {
  min-height: 180px;
  line-height: 1.8;
  font-size: 16px;
}
/* 按讚 */
.reaction-area {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #e0e0e0;
}
/* 留言標題 */
.comment-title {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 18px 0;
  font-weight: 600;
}
/* 留言 */
.comment-card {
  padding: 20px;
}
</style>
