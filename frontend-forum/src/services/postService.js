import httpClient from "@/services/httpClient";

// 取得單篇文章
export const getPost = (postId) => {
  return httpClient.get(`/api/posts/${postId}`);
};

// 取得文章編輯紀錄
export const getPostHistory = (postId) => {
  return httpClient.get(`/api/posts/${postId}/history`);
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
export const getPostsByBoard = (boardId, page, sortBy = "latest") => {
  return httpClient.get(`/api/posts/boards/${boardId}`, {
    params: {
      page,
      sortBy,
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
export const searchPosts = (
  title,
  page,
  boardId = null,
  sortBy = "latest",
) => {
  const params = {
    title,
    page,
    sortBy,
  };

  if (boardId !== null) {
    params.boardId = boardId;
  }

  return httpClient.get("/api/posts/search", { params });
};

// 上傳文章圖片
export const uploadPostImage = (file) => {
  const formData = new FormData();

  formData.append("file", file);

  return httpClient.post("/api/posts/images", formData);
};

// 取得文章按讚數量與目前使用者的按讚狀態
export const getPostLikeStatus = (postId) => {
  return httpClient.get(`/api/posts/${postId}/like`);
};

// 新增或取消按讚
export const togglePostLike = (postId) => {
  return httpClient.put(`/api/posts/${postId}/like`);
};
