<template>
  <div class="forum-page">
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
        :liked-by-current-user="likedByCurrentUser"
        :like-updating="likeUpdating"
        :is-post-owner="isPostOwner"
        :can-moderate="canModerate"
        :post-deleting="postDeleting"
        @toggle-like="handleToggleLike"
        @edit-post="editPost"
        @delete-post="requestDelete('post')"
      />
      <div v-if="postHistory.length > 0" class="d-flex justify-end mt-3 mb-4">
        <v-btn
          variant="text"
          prepend-icon="mdi-history"
          @click="openHistoryDialog"
        >
          查看編輯紀錄
        </v-btn>
      </div>
      <section class="comments-section">
      <!-- ====================== -->
      <!-- 留言標題 -->
      <!-- ====================== -->
      <div class="comment-title">
        <v-icon icon="mdi-comment-outline" size="21" />
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
        :can-moderate="canModerate"
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
        @show-comment-history="openCommentHistory"
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
      <!-- 發表留言 -->
      <CommentForm
        v-model="newComment"
        :submitting="commentSubmitting"
        @submit="submitComment"
      />
      </section>
    </template>
    <!-- 文章編輯紀錄 -->
    <PostHistoryDialog
      v-model="historyDialog"
      :histories="postHistory"
      :loading="historyLoading"
      :error-message="historyError"
    />
    <!-- 留言編輯紀錄 Dialog -->
    <CommentHistoryDialog
      v-model="commentHistoryDialog"
      :histories="commentHistory"
      :loading="commentHistoryLoading"
      :error-message="commentHistoryError"
    />
    <!-- 共用刪除確認視窗 -->
    <DeleteConfirmDialog
      :model-value="deleteDialog"
      v-model:reason="deleteReason"
      :message="deleteDialogMessage"
      :loading="isDeleting"
      :reason-required="deleteReasonRequired"
      @cancel="closeDeleteDialog"
      @confirm="confirmDelete"
    />
  </v-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

import httpClient from "@/services/httpClient";

// API匯入
import {
  getPost as getPostApi,
  getPostHistory as getPostHistoryApi,
  deletePost as deletePostApi,
  increasePostViews as increasePostViewsApi,
} from "@/services/postService";

// components 元件匯入
import CommentItem from "@/components/post/CommentItem.vue";
import PostContent from "@/components/post/PostContent.vue";
import PostHistoryDialog from "@/components/post/PostHistoryDialog.vue";
import CommentHistoryDialog from "@/components/post/CommentHistoryDialog.vue";
import CommentForm from "@/components/post/CommentForm.vue";
import DeleteConfirmDialog from "@/components/common/DeleteConfirmDialog.vue";
import DollAvatar from "@/components/doll/DollAvatar.vue";

// JS匯入
import { useNotification } from "@/composables/useNotification";
import { useComments } from "@/composables/useComments";
import { usePostLike } from "@/composables/usePostLike";

import { useUserStore } from "@/stores/user";
const { showMessage } = useNotification();

const userStore = useUserStore();
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
const {
  // ======================
  // 主留言Comments
  // ======================
  comments,
  totalCommentElements,
  commentPage,
  totalCommentPages,
  // ======================
  // 子留言childComment
  // ======================
  childComments,
  expandedComments,
  // ======================
  // 新增留言
  // ======================
  newComment,
  commentSubmitting,
  // ======================
  // 新增子留言
  // ======================
  replyingCommentId,
  replyContent,
  replySubmitting,
  // ======================
  // 更新留言
  // ======================
  editingCommentId,
  editingContent,
  commentUpdating,
  deletingCommentId,
  // ======================
  // 留言編輯紀錄
  // ======================
  commentHistoryDialog,
  commentHistory,
  commentHistoryLoading,
  commentHistoryError,
  // ======================
  // 取得主留言Comments
  // ======================
  fetchComments,
  // ======================
  // 取得子留言ChildComments
  // ======================
  fetchChildComments,
  // ======================
  // 展開與收合回覆
  // ======================
  toggleReplies,
  // ======================
  // 開始與取消回覆
  // ======================
  startReply,
  cancelReply,
  // ======================
  // 新增子留言
  // ======================
  submitReply,
  // ======================
  // 發表留言
  // ======================
  submitComment,
  // ======================
  // 開始編輯留言與取消編輯
  // ======================
  startEditComment,
  cancelEditComment,
  // ======================
  // 更新留言
  // ======================
  updateComment,
  // ======================
  // 刪除留言
  // ======================
  deleteComment,
  // ======================
  // 取得留言編輯紀錄
  // ======================
  openCommentHistory,
} = useComments(postId);

// ======================
// 刪除按鈕顯示
// ======================
const canModerate = ref(false);
const {
  likeCount,
  likedByCurrentUser,
  likeUpdating,
  fetchLikeStatus,
  handleToggleLike,
} = usePostLike(postId);
// ======================
// 狀態
// ======================
const loading = ref(false); // 文章是否載入中
const errorMessage = ref(""); // 文章載入錯誤訊息
const deleteDialog = ref(false);
const historyDialog = ref(false);
const historyLoading = ref(false);
const historyError = ref("");
const postHistory = ref([]);
const deleteTarget = ref(null);
const postDeleting = ref(false);
const deleteReason = ref("");

const deleteDialogMessage = computed(() =>
  deleteTarget.value?.type === "post"
    ? "確定要刪除這篇文章嗎？刪除後將無法繼續編輯。"
    : "確定要刪除這則留言嗎？",
);
// 刪除功能
const isDeleting = computed(
  () => postDeleting.value || deletingCommentId.value !== null,
);
// 建立刪除目標
const requestDelete = (type, commentId = null, parentCommentId = null,ownerId = null,) => {
  deleteReason.value = "";
  deleteTarget.value = { type, commentId, parentCommentId, ownerId, };
  deleteDialog.value = true;
};
// 關閉與確認刪除
const closeDeleteDialog = () => {
  if (isDeleting.value) return;
  deleteDialog.value = false;
  deleteTarget.value = null;
  deleteReason.value = "";
};
// 確認刪除
const confirmDelete = async () => {
  const target = deleteTarget.value;

  if (!target) return;

  if (target.type === "post") {
    deletePost();
    return;
  }

  const success = await deleteComment(target.commentId, target.parentCommentId,deleteReasonRequired.value? deleteReason.value: null,);
  if (success) {
    deleteDialog.value = false;
    deleteTarget.value = null;
    deleteReason.value = "";
  }
};
// 轉接 CommentItem 的刪除事件
const handleCommentDeleteRequest = (commentId, parentCommentId,ownerId,) => {
  requestDelete("comment", commentId, parentCommentId,ownerId,);
};
// ======================
// 使用者判斷
// ======================
const currentUserId = computed(() => userStore.uid ?? "");
// ======================
// 管理員刪除
// ======================
const deleteReasonRequired = computed(() => {
  const target = deleteTarget.value;

  if (!target || target.type !== "comment") {
    return false;
  }
  const ownerId = target.ownerId;
  if (!ownerId || !currentUserId.value) {
    return false;
  }
  const isOwner =String(ownerId).toLowerCase() === String(currentUserId.value).toLowerCase();
  return canModerate.value && !isOwner;
});
// ======================
// 取得 Post
// ======================
const fetchPost = () => {
  loading.value = true;
  errorMessage.value = "";
  return getPostApi(postId)
    .then((response) => {
      post.value = response.data;
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
// 查詢文章編輯紀錄
// ======================
const fetchPostHistory = () => {
  historyLoading.value = true;
  historyError.value = "";

  return getPostHistoryApi(postId)
    .then((response) => {
      postHistory.value = response.data;
    })
    .catch((error) => {
      console.error("取得文章編輯紀錄失敗：", error);
      postHistory.value = [];
      historyError.value = "取得文章編輯紀錄失敗";
    })
    .finally(() => {
      historyLoading.value = false;
    });
};
// 開啟文章編輯紀錄對話框
const openHistoryDialog = () => {
  historyDialog.value = true;
};
// ======================
// 文章功能-權限判斷
// ======================
const isPostOwner = computed(() => {
  // PostDTO 直接回傳 userId，不是 users.id
  const postUserId = post.value?.userId;

  if (!postUserId || !currentUserId.value) {
    return false;
  }

  return postUserId.toLowerCase() === currentUserId.value.toLowerCase();
});
// ======================
// 跳轉到編輯文章頁面
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
// 管理員刪除留言功能
// ======================
const fetchModerationPermission = () => {
  const boardId = post.value?.boardId;

  if (!boardId) {
    canModerate.value = false;
    return Promise.resolve();
  }

  return httpClient({
    method: "get",
    url: `/api/boards/${boardId}/moderators/me`,
  })
    .then((response) => {
      canModerate.value = response.data?.canModerate === true;
    })
    .catch((error) => {
      console.error("取得版主權限失敗：", error);
      canModerate.value = false;
    });
};
// ======================
// 返回
// ======================
const goBack = () => {
  const boardId = post.value?.boardId;

  if (boardId) {
    router.push({
      name: "board",
      params: { boardId },
    });
    return;
  }

  router.push({ name: "userCategories" });
};
// ======================
// 頁面載入
// ======================
onMounted(async () => {
  loading.value = true;
  // 留言可以先載入
  fetchComments();

  const storageKey = `post-view-time:${postId}`;
  const cooldown = 5 * 60 * 1000; // 5 分鐘內重複點取文章或重新整理不會增加觀看數
  let shouldCount = true;
  try {
    const lastViewed = Number(sessionStorage.getItem(storageKey));

    shouldCount = !lastViewed || Date.now() - lastViewed >= cooldown;
  } catch (error) {
    console.warn("無法讀取瀏覽紀錄：", error);
  }

  if (shouldCount) {
    try {
      await increasePostViewsApi(postId);

      // API 成功後才記錄時間
      try {
        sessionStorage.setItem(storageKey, String(Date.now()));
      } catch (error) {
        console.warn("無法儲存瀏覽紀錄：", error);
      }
    } catch (error) {
      console.error("增加瀏覽數失敗：", error);
    }
  }
  // 不論是否計次，都正常取得文章
  await fetchPost();
  await fetchModerationPermission();
  fetchPostHistory();
  fetchLikeStatus();
});
</script>

<style scoped>
.forum-page {
  min-height: 100vh;
  background: var(--forum-page-bg);
} 
/* 留言標題 */
.comment-title {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 18px 0;
  font-weight: 600;
}
.comments-section {
  margin-top: var(--forum-space-lg);
  padding: var(--forum-space-lg);
  border: 1px solid var(--forum-border);
  border-radius: var(--forum-radius);
  background: var(--forum-card-bg);
  box-shadow: var(--forum-shadow);
}

.comment-title {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 8px;
  padding: 0 0 16px;
  color: var(--forum-title);
  font-size: 18px;
  font-weight: 700;
}
</style>
