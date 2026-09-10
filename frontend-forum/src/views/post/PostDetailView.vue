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
        :liked-by-current-user="likedByCurrentUser"
        :like-updating="likeUpdating"
        :is-post-owner="isPostOwner"
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

    <!-- 文章編輯紀錄 -->
    <PostHistoryDialog
      v-model="historyDialog"
      :histories="postHistory"
      :loading="historyLoading"
      :error-message="historyError"
    />
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
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

// API匯入
import {
  getPost as getPostApi,
  getPostHistory as getPostHistoryApi,
  deletePost as deletePostApi,
  increasePostViews as increasePostViewsApi,
  getPostLikeStatus as getPostLikeStatusApi,
  togglePostLike as togglePostLikeApi,
} from "@/services/postService";
import {
  getComments as getCommentsApi,
  getChildComments as getChildCommentsApi,
  createComment as createCommentApi,
  createReply as createReplyApi,
  updateComment as updateCommentApi,
  deleteComment as deleteCommentApi,
} from "@/services/commentService";

// components 元件匯入
import CommentItem from "@/components/CommentItem.vue";
import PostContent from "@/components/PostContent.vue";
import PostHistoryDialog from "@/components/PostHistoryDialog.vue";
// 通知功能匯入
import { useNotification } from "@/composables/useNotification";

const { showMessage } = useNotification();

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
const totalCommentElements = ref(0);
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
const likedByCurrentUser = ref(false);
const likeUpdating = ref(false);
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
const deletingCommentId = ref(null);

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
const requestDelete = (type, commentId = null, parentCommentId = null) => {
  deleteTarget.value = { type, commentId, parentCommentId };
  deleteDialog.value = true;
};
// 關閉與確認刪除
const closeDeleteDialog = () => {
  if (isDeleting.value) return;
  deleteDialog.value = false;
  deleteTarget.value = null;
};
// 確認刪除
const confirmDelete = () => {
  const target = deleteTarget.value;
  if (!target) return;

  if (target.type === "post") {
    deletePost();
  } else {
    deleteComment(target.commentId, target.parentCommentId);
  }
};
// 轉接 CommentItem 的刪除事件
const handleCommentDeleteRequest = (commentId, parentCommentId) => {
  requestDelete("comment", commentId, parentCommentId);
};
// ======================
// 使用者判斷 暫時寫死
// ======================
const currentUserId = ref('')
// ======================
// 取得 Post
// ======================
const fetchPost = () => {
  loading.value = true;
  errorMessage.value = "";
  getPostApi(postId)
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
// 取得主留言Comments
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
// 取得子留言ChildComments
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

// ======================
// 取得按讚狀態
// ======================
const fetchLikeStatus = () => {
  return getPostLikeStatusApi(postId)
    .then((response) => {
      likeCount.value = response.data.likeCount ?? 0;

      likedByCurrentUser.value = response.data.likedByCurrentUser ?? false;
    })
    .catch((error) => {
      console.error("取得按讚狀態失敗：", error);

      likeCount.value = 0;
      likedByCurrentUser.value = false;
    });
};
// ======================
// 新增或取消按讚
// ======================
const handleToggleLike = () => {
  if (likeUpdating.value) {
    return;
  }

  likeUpdating.value = true;

  togglePostLikeApi(postId)
    .then((response) => {
      likeCount.value = response.data.likeCount ?? 0;
      likedByCurrentUser.value = response.data.likedByCurrentUser ?? false;
    })
    .catch((error) => {
      console.error("按讚操作失敗：", error);

      showMessage(error.response?.data ?? "按讚操作失敗，請稍後再試", "error");
    })
    .finally(() => {
      likeUpdating.value = false;
    });
};
// 展開與收合回覆
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
// 開始與取消回覆
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
// 開始編輯留言與取消編輯
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
  fetchPost();
  fetchPostHistory();
  fetchLikeStatus();
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
