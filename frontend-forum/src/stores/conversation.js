import httpClient from "@/services/httpClient";
import { defineStore } from "pinia";
import { computed, ref } from "vue";
export const useChatStore = defineStore("chat", () => {
  const conversationList = ref([]);
  const currentConversationId = ref(null);
  const loading = ref(false);
  const error = ref(null);

  // 單一資料來源：從 list 取出
  const currentConversation = computed(() => {
    conversationList.value.find(c =>
      c.id === currentConversationId.value ?? null
    )
  });

  // 取得此使用者所有conversation
  const getConversations = async () => {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await httpClient.get("/api/chat/conversations");
      conversationList.value = data;
    } catch (error) {
      error.value = "取得對話列表失敗"
      console.log("取得conversation list失敗", error)
    } finally {
      loading.value = false
    }
  };

  // 選擇對話
  const selectConversation = (conversationId) => {
    currentConversationId.value = conversationId;
  };

  // 建立或取得私訊對話
  const directConversation = async (targetUserId) => {
    try {
      const { data } = await httpClient.post("/api/chat/conversations/direct",
        {
          targetUserId,
        })
      const idx = conversationList.value.findIndex((c) => { c.id === data.id });
      if (idx >= 0) {
        conversationList.value[idx] = data;
      } else {
        conversationList.value.unshift(data);
      }

      currentConversationId.value = data.id;
      return data; // 呼叫端要拿 id 導頁
    } catch (error) {
      error.value = "建立私訊失敗";
      console.error("建立私訊失敗", error);
      throw error;
    }
  };

  const clearUnreadCount = (conversationId) => {
    const c = conversationList.value.find(c => c.id === conversationId);

    if (c) {
      c.unreadCount = 0;
    }
  };

  // 收到新訊息時更新側欄：預覽文字、未讀數、排序
  const applyIncoming = ((message, { isCurrent = false } = {}) => {
    const idx = conversationList.value.findIndex((c) => c.id === message.conversationId);
    if (idx < 0) {
      getConversations(); // 新會話，重新拉列表
      return;
    }

    const c = conversationList.value[idx];
    c.lastMessage = message.content;
    c.updateTime = message.createTime;
    if (!isCurrent && !message.mine) {
      c.unreadCount = (c.unreadCount ?? 0) + 1;
    }

    // 移到頂端
    conversationList.value.splice(idx, 1);
    conversationList.value.unshift(c);
  });

  return {
    conversationList,
    currentConversationId,
    currentConversation,
    loading,
    error,

    getConversations,
    selectConversation,
    directConversation,
    clearUnreadCount,
    applyIncoming,
  };
});
