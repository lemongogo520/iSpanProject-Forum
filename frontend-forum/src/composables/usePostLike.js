import { ref } from "vue";

import {
  getPostLikeStatus as getPostLikeStatusApi,
  togglePostLike as togglePostLikeApi,
} from "@/services/postService";

import { useNotification } from "@/composables/useNotification";

export function usePostLike(postId) {
  const { showMessage } = useNotification();

  // ======================
  // 按讚狀態
  // ======================
  const likeCount = ref(0);
  const likedByCurrentUser = ref(false);
  const likeUpdating = ref(false);
  // ======================
  // 取得按讚狀態
  // ======================
  const fetchLikeStatus = async () => {
    try {
      const response =
        await getPostLikeStatusApi(postId);

      likeCount.value =
        response.data.likeCount ?? 0;

      likedByCurrentUser.value =
        response.data.likedByCurrentUser ?? false;
    } catch (error) {
      console.error(
        "取得按讚狀態失敗：",
        error,
      );

      likeCount.value = 0;
      likedByCurrentUser.value = false;
    }
  };

  // ======================
  // 新增或取消按讚
  // ======================

  const handleToggleLike = async () => {
    // 防止使用者連續點擊
    if (likeUpdating.value) {
      return;
    }

    likeUpdating.value = true;

    try {
      const response =
        await togglePostLikeApi(postId);

      likeCount.value =
        response.data.likeCount ?? 0;

      likedByCurrentUser.value =
        response.data.likedByCurrentUser ?? false;
    } catch (error) {
      console.error(
        "按讚操作失敗：",
        error,
      );

      showMessage(
        error.response?.data ??
          "按讚操作失敗，請稍後再試",
        "error",
      );
    } finally {
      likeUpdating.value = false;
    }
  };

  return {
    likeCount,
    likedByCurrentUser,
    likeUpdating,
    fetchLikeStatus,
    handleToggleLike,
  };
}