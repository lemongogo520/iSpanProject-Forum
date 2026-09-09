import httpClient from "@/services/httpClient";

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

// 增加觀看數
export const increasePostViews = (postId) => {
  return httpClient.patch(`/api/posts/${postId}/views`);
};

// 搜尋功能 沒有帶 boardId 時搜尋全站
export const searchPosts = (title, page, size, boardId = null) => {
  const params = {
    title,
    page,
    size,
  };

  if (boardId !== null) {
    params.boardId = boardId;
  }

  return httpClient.get("/api/posts/search", { params });
};