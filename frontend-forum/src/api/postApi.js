import httpClient from "./httpClient";

export const getPost = (postId) => {
  return httpClient.get(`/api/posts/${postId}`);
};

export const updatePost = (postId, data) => {
  return httpClient.put(`/api/posts/${postId}`, data);
};

export const deletePost = (postId) => {
  return httpClient.delete(`/api/posts/${postId}`);
};