import httpClient from "./httpClient";

// 取得單篇文章
export const getPost = (postId) => {
  return httpClient.get(`/api/posts/${postId}`);
};

// 修改文章
export const updatePost = (postId, data) => {
  return httpClient.put(`/api/posts/${postId}`, data);
};

// 刪除文章
export const deletePost = (postId) => {
  return httpClient.delete(`/api/posts/${postId}`);
};

// 取得看板文章
export const getPostsByBoard = (boardId, page, size) => {
  return httpClient.get(`/api/posts/boards/${boardId}`, {
    params: {
      page,
      size,
    },
  });
};

// 新增文章
export const createPost = (data) => {
  return httpClient.post("/api/posts", data);
};