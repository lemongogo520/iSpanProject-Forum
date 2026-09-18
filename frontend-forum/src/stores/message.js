import httpClient from "@/services/httpClient";
import { defineStore } from "pinia";
import { ref } from "vue";
export const useMessageStore = defineStore("message", () => {
  const messages = ref([]); // 目前聊天室的歷史訊息
  const loading = ref(false); // 正在載入
  const sending = ref(false); //正在送出

  // 取得訊息
  const getMessages = async (conversationId) => {
    loading.value = true;
    await httpClient
      .get(`/api/chat/${conversationId}/messages`)
      .then((response) => {
        messages.value = response.data;
      })
      .catch((error) => console.log("取得訊息失敗", error))
      .finally((loading.value = false));
  };

  // 傳送訊息
  const sendMessage = async (conversationId, content) => {
    if (!content.trim()) return;
    sending.value = true;
    await httpClient({
      method: "post",
      url: "/api/chat/message",
      data: {
        conversationId: conversationId,
        content: content,
      },
    })
      .then((response) => {
        messages.value.push(response.data);
      })
      .catch((error) => console.log("傳送訊息失敗", error))
      .finally((sending.value = false));
  };

  // 新增訊息
  const addMessage = (message) => {
    messages.value.push(message);
  };

  // 清除訊息
  const clearMessages = () => {
    messages.value = [];
  };

  return {
    messages,
    loading,
    sending,
    getMessages,
    sendMessage,
    addMessage,
    clearMessages,
  };
});
