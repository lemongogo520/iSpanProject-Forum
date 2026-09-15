import httpClient from "@/services/httpClient";

// 取得主留言
export const getComments = (postId, page, size) => {
  return httpClient.get(`/api/comments/posts/${postId}`, {
    params: {
      page,
      size,
    },
  });
};

// 取得子留言
export const getChildComments = (parentCommentId) => {
  return httpClient.get(
    `/api/comments/${parentCommentId}/children`
  );
};

// 新增主留言
export const createComment = (postId, content) => {
  return httpClient.post(`/api/comments/posts/${postId}`, {
    content,
  });
};

// 新增子留言
export const createReply = (
  postId,
  parentCommentId,
  content
) => {
  return httpClient.post(
    `/api/comments/posts/${postId}`,
    { content },
    {
      params: {
        parentCommentId,
      },
    }
  );
};

// 修改留言
export const updateComment = (commentId, content) => {
  return httpClient.put(`/api/comments/${commentId}`, {
    content,
  });
};

// 刪除留言
export const deleteComment = (commentId,reason = null,) => {
  return httpClient.delete(
    `/api/comments/${commentId}`,
    {
      data: {
        reason,
      },
    },
  );
};

// 取得留言編輯紀錄
export const getCommentHistory = (commentId) => {
  return httpClient.get(
    `/api/comments/${commentId}/history`
  )
}