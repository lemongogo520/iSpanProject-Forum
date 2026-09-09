import httpClient from "@/services/httpClient";

export const getComments = (postId, page, size) => {
  return httpClient.get(`/api/comments/posts/${postId}`, {
    params: {
      page,
      size,
    },
  });
};

export const getChildComments = (parentCommentId) => {
  return httpClient.get(
    `/api/comments/${parentCommentId}/children`
  );
};

export const createComment = (postId, content) => {
  return httpClient.post(`/api/comments/posts/${postId}`, {
    content,
  });
};

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

export const updateComment = (commentId, content) => {
  return httpClient.put(`/api/comments/${commentId}`, {
    content,
  });
};

export const deleteComment = (commentId) => {
  return httpClient.delete(`/api/comments/${commentId}`);
};