import httpClient from "@/services/httpClient";
import { defineStore } from "pinia";
import { ref } from "vue";
export const useChatStore = defineStore("chat", () => {
  const conversationList = ref([]);
  const currentConversation = ref(null);
  const loading = ref(false);

  // 取得此使用者所有conversation
  const getConversations = async () => {
    loading.value = true;
    await httpClient
      .get("/api/chat/conversation")
      .then((response) => {
        conversationList.value = response.data;
      })
      .catch((error) => console.log("取得conversationlist失敗", error))
      .finally((loading.value = false));
  };

  // 選擇對話
  const selectConversation = (conversation) => {
    currentConversation.value = conversation;
  };

  // 建立或取得私訊對話
  const directConversation = async (targetUserId) => {
    await httpClient({
      method: "post",
      url: "/api/chat/direct",
      data: {
        targetUserId,
      },
    })
      .then((response) => {
        const conversation = response.data;
        currentConversation.value = conversation;

        const exists = conversationList.value.some(
          (c) => c.id === conversation.id,
        );
        if (!exists) {
          conversationList.value.push(conversation);
        }
      })
      .catch((error) => console.error("建立私訊失敗", error));
  };

  return {
    conversationList,
    currentConversation,
    loading,

    getConversations,
    selectConversation,
    directConversation,
  };
});
