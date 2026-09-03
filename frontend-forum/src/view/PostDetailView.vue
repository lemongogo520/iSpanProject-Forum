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
      <PostContent
        :post="post"
        :like-count="likeCount"
        :is-post-owner="isPostOwner"
        :post-deleting="postDeleting"
        @edit-post="editPost"
        @delete-post="requestDelete('post')"/>
      <!-- ====================== -->
      <!-- 留言標題 -->
      <!-- ====================== -->
      <div class="comment-title">
        <v-icon icon="mdi-chevron-down" size="20" />
        <span> 留言（{{ totalCommentElements }}） </span>
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
      <CommentItem
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
        :child-comments="childComments[comment.id] || []"
        :expanded="Boolean(expandedComments[comment.id])"
        :editing-comment-id="editingCommentId"
        :editing-content="editingContent"
        :replying-comment-id="replyingCommentId"
        :reply-content="replyContent"
        :current-user-id="currentUserId"
        :comment-updating="commentUpdating"
        :reply-submitting="replySubmitting"
        :deleting-comment-id="deletingCommentId"
        @start-edit="startEditComment"
        @cancel-edit="cancelEditComment"
        @update-comment="updateComment"
        @delete-comment="handleCommentDeleteRequest"
        @start-reply="startReply"
        @cancel-reply="cancelReply"
        @submit-reply="submitReply"
        @toggle-replies="toggleReplies"
        @update:editing-content="editingContent = $event"
        @update:reply-content="replyContent = $event"
      />
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

    <!-- 共用刪除確認視窗 -->
    <v-dialog v-model="deleteDialog" max-width="420" persistent>
      <v-card>
        <v-card-title class="text-h6">確認刪除</v-card-title>
        <v-card-text>{{ deleteDialogMessage }}</v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            variant="text"
            :disabled="isDeleting"
            @click="closeDeleteDialog"
          >
            取消
          </v-btn>
          <v-btn
            color="error"
            variant="flat"
            :loading="isDeleting"
            @click="confirmDelete"
          >
            確定刪除
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 共用成功／錯誤提示 -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
      location="top"
    >
      {{ snackbar.message }}
      <template #actions>
        <v-btn variant="text" @click="snackbar.show = false">關閉</v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  getPost as getPostApi,
  deletePost as deletePostApi,
} from "@/api/postApi";

import {
  getComments as getCommentsApi,
  getChildComments as getChildCommentsApi,
  createComment as createCommentApi,
  createReply as createReplyApi,
  updateComment as updateCommentApi,
  deleteComment as deleteCommentApi,
} from "@/api/commentApi";

import CommentItem from "@/components/CommentItem.vue";
import PostContent from "@/components/PostContent.vue";

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
// 全部留言筆數
// ======================
const totalCommentElements = ref(0);
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
// 共用提示與刪除確認
// ======================
const snackbar = ref({
  show: false,
  message: "",
  color: "success",
});
const deleteDialog = ref(false);
const deleteTarget = ref(null);
const postDeleting = ref(false);
const deletingCommentId = ref(null);

const showMessage = (message, color = "success") => {
  snackbar.value = {
    show: true,
    message,
    color,
  };
};

const deleteDialogMessage = computed(() =>
  deleteTarget.value?.type === "post"
    ? "確定要刪除這篇文章嗎？刪除後將無法繼續編輯。"
    : "確定要刪除這則留言嗎？",
);

const isDeleting = computed(
  () => postDeleting.value || deletingCommentId.value !== null,
);

const requestDelete = (type, commentId = null, parentCommentId = null) => {
  deleteTarget.value = { type, commentId, parentCommentId };
  deleteDialog.value = true;
};

const closeDeleteDialog = () => {
  if (isDeleting.value) return;
  deleteDialog.value = false;
  deleteTarget.value = null;
};

const confirmDelete = () => {
  const target = deleteTarget.value;
  if (!target) return;

  if (target.type === "post") {
    deletePost();
  } else {
    deleteComment(target.commentId, target.parentCommentId);
  }
};

const handleCommentDeleteRequest = (commentId, parentCommentId) => {
  requestDelete("comment", commentId, parentCommentId);
};
// ======================
// 使用者判斷 暫時寫死
// ======================
const currentUserId = "10334e31-124a-4a74-b898-4a2c62753cc9";

// ======================
// 取得 Post
// ======================
const fetchPost = () => {
  loading.value = true;
  errorMessage.value = "";
  getPostApi(postId)
    .then((response) => {
      post.value = response.data;
      likeCount.value = response.data.likes || 0;
    })
    .catch((error) => {
      console.log("文章取得失敗:", error);
      errorMessage.value = "文章取得失敗";
    })
    .finally(() => {
      loading.value = false;
    });
};
// ======================
// 取得 Comments
// ======================
const fetchComments = () => {
  getCommentsApi(postId, commentPage.value - 1, commentPageSize.value)
    .then((response) => {
      comments.value = response.data.content;
      totalCommentPages.value = response.data.totalPages;
      totalCommentElements.value = response.data.totalElements;
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
  getChildCommentsApi(parentCommentId)
    .then((response) => {
      childComments.value = {
        ...childComments.value,
        [parentCommentId]: response.data,
      };
    })
    .catch((error) => {
      console.error("取得子留言失敗：", error);
      childComments.value = { ...childComments.value, [parentCommentId]: [] };
    });
};
const toggleReplies = (parentCommentId) => {
  const willExpand = !expandedComments.value[parentCommentId];

  // 更新展開狀態
  expandedComments.value = {
    ...expandedComments.value,
    [parentCommentId]: willExpand,
  };
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
    showMessage("請輸入回覆內容", "warning");
    return;
  }

  replySubmitting.value = true;

  createReplyApi(postId, parentCommentId, content)
    .then(() => {
      cancelReply();
      showMessage("回覆成功");

      expandedComments.value = {
        ...expandedComments.value,
        [parentCommentId]: true,
      };

      fetchChildComments(parentCommentId);
    })
    .catch((error) => {
      showMessage(error.response?.data ?? "回覆失敗", "error");
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
    showMessage("請輸入留言內容", "warning");
    return;
  }

  commentSubmitting.value = true;
  createCommentApi(postId, content)
    .then(() => {
      newComment.value = "";
      commentPage.value = 1;
      showMessage("留言發表成功");
      fetchComments();
    })
    .catch((error) => {
      showMessage(error.response?.data ?? "留言失敗，請稍後再試", "error");
    })
    .finally(() => {
      commentSubmitting.value = false;
    });
};
// ======================
// 文章功能-權限判斷
// ======================
const isPostOwner = computed(() => {
  // PostDTO 直接回傳 userId，不是 users.id
  const postUserId = post.value?.userId;

  if (!postUserId || !currentUserId) {
    return false;
  }

  return postUserId.toLowerCase() === currentUserId.toLowerCase();
});
// ======================
// 編輯文章
// ======================
const editPost = () => {
  router.push({
    name: "post-edit",
    params: {
      postId,
    },
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
const updateComment = (commentId, parentCommentId = null) => {
  const content = editingContent.value.trim();

  if (!content) {
    showMessage("留言內容不能為空白", "warning");
    return;
  }

  commentUpdating.value = true;

  updateCommentApi(commentId, content)
    .then(() => {
      cancelEditComment();
      showMessage("留言修改成功");

      if (parentCommentId !== null) {
        fetchChildComments(parentCommentId);
      } else {
        fetchComments();
      }
    })
    .catch((error) => {
      showMessage(error.response?.data ?? "修改留言失敗", "error");
    })
    .finally(() => {
      commentUpdating.value = false;
    });
};
// ======================
// 刪除文章
// ======================
const deletePost = () => {
  postDeleting.value = true;

  deletePostApi(postId)
    .then(() => {
      deleteDialog.value = false;
      deleteTarget.value = null;
      showMessage("文章刪除成功");
      router.back();
    })
    .catch((error) => {
      showMessage(error.response?.data ?? "文章刪除失敗", "error");
    })
    .finally(() => {
      postDeleting.value = false;
    });
};
// ======================
// 刪除留言
// ======================
const deleteComment = (commentId, parentCommentId = null) => {
  deletingCommentId.value = commentId;
  deleteCommentApi(commentId)
    .then(() => {
      deleteDialog.value = false;
      deleteTarget.value = null;
      showMessage("留言刪除成功");
      if (parentCommentId !== null) {
        fetchChildComments(parentCommentId);
      } else {
        fetchComments();
      }
    })
    .catch((error) => {
      showMessage(error.response?.data ?? "刪除留言失敗", "error");
    })
    .finally(() => {
      deletingCommentId.value = null;
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
/* 留言標題 */
.comment-title {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 18px 0;
  font-weight: 600;
}
</style>
