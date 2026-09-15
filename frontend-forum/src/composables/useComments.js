import { ref } from "vue";

// API匯入
import {
  getComments as getCommentsApi,
  getChildComments as getChildCommentsApi,
  createComment as createCommentApi,
  createReply as createReplyApi,
  updateComment as updateCommentApi,
  deleteComment as deleteCommentApi,
  getCommentHistory as getCommentHistoryApi,
} from "@/services/commentService";

import { useNotification } from "@/composables/useNotification";

export function useComments(postId) {
  const { showMessage } = useNotification();
  // ======================
  // 主留言
  // ======================
  const comments = ref([]);
  const totalCommentElements = ref(0);
  const commentPage = ref(1);
  const commentPageSize = ref(5);
  const totalCommentPages = ref(0);
  // ======================
  // 子留言
  // ======================
  const childComments = ref({});
  const expandedComments = ref({});
  // ======================
  // 新增主留言
  // ======================
  const newComment = ref("");
  const commentSubmitting = ref(false);
  // ======================
  // 回覆留言
  // ======================
  const replyingCommentId = ref(null);
  const replyContent = ref("");
  const replySubmitting = ref(false);
  // ======================
  // 編輯留言
  // ======================
  const editingCommentId = ref(null);
  const editingContent = ref("");
  const commentUpdating = ref(false);
  // ======================
  // 刪除留言
  // ======================
  const deletingCommentId = ref(null);
  // ======================
  // 留言編輯紀錄
  // ======================
  const commentHistoryDialog = ref(false);
  const commentHistory = ref([]);
  const commentHistoryLoading = ref(false);
  const commentHistoryError = ref("");
  // ======================
  // 取得主留言
  // ======================
  const fetchComments = async () => {
    try {
      const response = await getCommentsApi(
        postId,
        commentPage.value - 1,
        commentPageSize.value,
      );

      comments.value = response.data.content;
      totalCommentPages.value = response.data.totalPages;
      totalCommentElements.value = response.data.totalElements;
    } catch (error) {
      console.error("取得留言失敗：", error);

      comments.value = [];
      totalCommentPages.value = 0;
      totalCommentElements.value = 0;
    }
  };
  // ======================
  // 取得子留言
  // ======================
  const fetchChildComments = async (parentCommentId) => {
    try {
      const response = await getChildCommentsApi(parentCommentId);

      childComments.value = {
        ...childComments.value,
        [parentCommentId]: response.data,
      };
    } catch (error) {
      console.error("取得子留言失敗：", error);

      childComments.value = {
        ...childComments.value,
        [parentCommentId]: [],
      };
    }
  };
  // ======================
  // 展開或收合回覆
  // ======================
  const toggleReplies = (parentCommentId) => {
    const willExpand = !expandedComments.value[parentCommentId];

    expandedComments.value = {
      ...expandedComments.value,
      [parentCommentId]: willExpand,
    };

    if (willExpand) {
      fetchChildComments(parentCommentId);
    }
  };
  // ======================
  // 開始／取消回覆
  // ======================
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

  const submitReply = async (parentCommentId) => {
    const content = replyContent.value.trim();

    if (!content) {
      showMessage("請輸入回覆內容", "warning");
      return;
    }

    replySubmitting.value = true;

    try {
      await createReplyApi(postId, parentCommentId, content);

      cancelReply();
      showMessage("回覆成功");

      expandedComments.value = {
        ...expandedComments.value,
        [parentCommentId]: true,
      };

      await fetchChildComments(parentCommentId);
    } catch (error) {
      showMessage(error.response?.data ?? "回覆失敗", "error");
    } finally {
      replySubmitting.value = false;
    }
  };
  // ======================
  // 新增主留言
  // ======================
  const submitComment = async () => {
    const content = newComment.value.trim();

    if (!content) {
      showMessage("請輸入留言內容", "warning");
      return;
    }

    commentSubmitting.value = true;

    try {
      await createCommentApi(postId, content);

      newComment.value = "";
      commentPage.value = 1;

      showMessage("留言發表成功");

      await fetchComments();
    } catch (error) {
      showMessage(error.response?.data ?? "留言失敗，請稍後再試", "error");
    } finally {
      commentSubmitting.value = false;
    }
  };
  // ======================
  // 開始／取消編輯
  // ======================
  const startEditComment = (comment) => {
    editingCommentId.value = comment.id;
    editingContent.value = comment.content;
  };

  const cancelEditComment = () => {
    editingCommentId.value = null;
    editingContent.value = "";
  };

  // ======================
  // 更新留言
  // ======================

  const updateComment = async (commentId, parentCommentId = null) => {
    const content = editingContent.value.trim();

    if (!content) {
      showMessage("留言內容不能為空白", "warning");

      return;
    }

    commentUpdating.value = true;

    try {
      await updateCommentApi(commentId, content);

      cancelEditComment();
      showMessage("留言修改成功");

      if (parentCommentId !== null) {
        await fetchChildComments(parentCommentId);
      } else {
        await fetchComments();
      }
    } catch (error) {
      showMessage(error.response?.data ?? "修改留言失敗", "error");
    } finally {
      commentUpdating.value = false;
    }
  };
  // ======================
  // 刪除留言
  // ======================
  const deleteComment = async (commentId, parentCommentId = null,reason = null,) => {
    deletingCommentId.value = commentId;

    try {
      await deleteCommentApi(commentId,reason);

      showMessage("留言刪除成功");

      if (parentCommentId !== null) {
        await fetchChildComments(parentCommentId);
      } else {
        await fetchComments();
      }

      return true;
    } catch (error) {
      showMessage(error.response?.data ?? "刪除留言失敗", "error");

      return false;
    } finally {
      deletingCommentId.value = null;
    }
  };

  // ======================
  // 留言編輯紀錄
  // ======================

  const openCommentHistory = async (commentId) => {
    commentHistoryDialog.value = true;
    commentHistoryLoading.value = true;
    commentHistoryError.value = "";
    commentHistory.value = [];

    try {
      const response = await getCommentHistoryApi(commentId);

      commentHistory.value = response.data;
    } catch (error) {
      console.error("取得留言編輯紀錄失敗：", error);

      commentHistoryError.value =
        error.response?.data ?? "取得留言編輯紀錄失敗";
    } finally {
      commentHistoryLoading.value = false;
    }
  };

  return {
    // 主留言
    comments,
    totalCommentElements,
    commentPage,
    commentPageSize,
    totalCommentPages,

    // 子留言
    childComments,
    expandedComments,

    // 新增留言
    newComment,
    commentSubmitting,

    // 回覆
    replyingCommentId,
    replyContent,
    replySubmitting,

    // 編輯
    editingCommentId,
    editingContent,
    commentUpdating,

    // 刪除
    deletingCommentId,

    // 編輯紀錄
    commentHistoryDialog,
    commentHistory,
    commentHistoryLoading,
    commentHistoryError,

    // 方法
    fetchComments,
    fetchChildComments,
    toggleReplies,
    startReply,
    cancelReply,
    submitReply,
    submitComment,
    startEditComment,
    cancelEditComment,
    updateComment,
    deleteComment,
    openCommentHistory,
  };
}
