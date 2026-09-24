import httpClient from "@/services/httpClient";
import { defineStore } from "pinia";
import { computed, ref } from "vue";
export const useChatStore = defineStore("chat", () => {
  // 私訊、已訂閱看板
  const conversationList = ref([]);
  const currentConversationId = ref(null);
  // 未訂閱看板
  const boardConversation = ref(null);

  const isMinimized = ref(true);
  const loading = ref(false);
  const error = ref(null);

  // 單一資料來源：從 list 取出
  const currentConversation = computed(() => {
    // 先從私訊列表及有訂閱看板找
    const inList = conversationList.value.find(
      (c) => c.id === currentConversationId.value ?? null,
    );
    if (inList) return inList;
    //未訂閱的看板
    return boardConversation.value?.id === currentConversationId.value
      ? boardConversation.value
      : null;
  });

  // 取得此使用者所有conversation(私訊、有訂閱的看板)
  const getConversations = async () => {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await httpClient.get("/api/chat/conversations");
      conversationList.value = data;
    } catch (e) {
      error.value = "取得對話列表失敗";
      console.log("取得conversation list失敗", e);
    } finally {
      loading.value = false;
    }
  };

  // 儲存現在看的聊天室、狀態
  const persistWindow = () => {
    if (currentConversationId.value == null) {
      sessionStorage.removeItem("chatWindow");
    } else {
      sessionStorage.setItem(
        "chatWindow",
        JSON.stringify({
          conversationId: currentConversationId.value,
          minimized: isMinimized.value,
        }),
      );
    }
  };

  // 重整後恢復聊天視窗及狀態
  const restoreWindow = async () => {
    const saved = sessionStorage.getItem("chatWindow");
    if (!saved) return;
    try {
      const { conversationId, minimized } = JSON.parse(saved);
      currentConversationId.value = conversationId;
      isMinimized.value = minimized;
      // 未訂閱看板
      if (!conversationList.value.find((c) => c.id === conversationId)) {
        const { data } = await httpClient.get(
          `/api/chat/conversations/${conversationId}`,
        );
        boardConversation.value = data;
      }
    } catch {
      sessionStorage.removeItem("chatWindow");
      currentConversationId.value = null;
      isMinimized.value = true;
    }
  };

  // 進入cId聊天 看板聊天可帶dto
  const openChat = (conversationId, dto = null) => {
    const id = Number(conversationId);
    if (!Number.isFinite(id)) {
      console.error("openChat 需要對話 id，收到：", conversationId);
      return;
    }
    boardConversation.value = dto;
    currentConversationId.value = id;
    isMinimized.value = false;
    persistWindow();
  };

  // 關閉聊天
  const closeChat = () => {
    boardConversation.value = null;
    currentConversationId.value = null;
    isMinimized.value = false;
    persistWindow();
  };

  // 切換最小化
  const toggleMinimize = () => {
    isMinimized.value = !isMinimized.value;
    persistWindow();
  };

  // 私訊的未讀數量
  const totalUnread = computed(() =>
    conversationList.value
      .filter((c) => c.type === "DIRECT")
      .reduce((sum, c) => sum + (c.unreadCount ?? 0), 0),
  );

  // 建立或取得私訊對話
  const directConversation = async (targetUserId) => {
    try {
      const { data } = await httpClient.post("/api/chat/conversations/direct", {
        targetUserId,
      });

      const idx = conversationList.value.findIndex((c) => c.id === data.id);
      if (idx >= 0) {
        conversationList.value[idx] = data;
      } else {
        conversationList.value.unshift(data);
      }

      return data; // 呼叫端要拿 id 導頁
    } catch (e) {
      error.value = "建立私訊失敗";
      console.error("建立私訊失敗", e);
      throw error;
    }
  };

  // 取得看板聊天室
  const getboardConversation = async (boardId) => {
    try {
      const { data } = await httpClient.get(`/api/chat/boards/${boardId}`);
      openChat(data.id, data.subscribed ? null : data);
      if (data.subscribed) applyConversation(data);
      return data;
    } catch (e) {
      error.value = "取得對話失敗";
      console.error("取得對話失敗", e);
      throw error;
    }
  };

  const subscribeBoard = async (boardId) => {
    try {
      const { data } = await httpClient.post(
        `/api/chat/boards/${boardId}}/subscribe`,
      );
      applyConversation(data);
      return data;
    } catch (error) {
      error.value = "訂閱失敗";
      console.error("訂閱失敗", e);
      throw error;
    }
  };

  const unsubscribeBoard = async (boardId) => {
    try {
      const { data } = await httpClient.delete(
        `/api/chat/boards/${boardId}}/unsubscribe`,
      );
      applyConversation(data);
      return data;
    } catch (error) {
      error.value = "取消訂閱失敗";
      console.error("取消訂閱失敗", e);
      throw error;
    }
  };

  // 把後端回傳的 DTO 寫回列表或暫存
  // 更新訂閱狀態 或是 有新訊息
  const applyConversation = (dto) => {
    const idx = conversationList.value.findIndex((c) => c.id === dto.id);

    if (dto.subscribed || dto.type === "DIRECT") {
      if (idx >= 0)
        conversationList.value[idx] = dto; //原本就在列表: 更新狀態
      else conversationList.value.unshift(dto); //原本不在列表: 放到列表頂端
      if (boardConversation.value?.id === dto.id)
        boardConversation.value = null; // 未訂閱看板清空
    } else {
      if (idx >= 0) conversationList.value.splice(idx, 1); // 取消訂閱 → 移出列表
      if (currentConversationId.value === dto.id) boardConversation.value = dto;
    }
  };

  // 清除某對話未讀
  const clearUnreadCount = (conversationId) => {
    const c = conversationList.value.find((c) => c.id === conversationId);

    if (c) {
      c.unreadCount = 0;
    }
  };

  // 收到新訊息時更新側欄：預覽文字、未讀數、排序
  const applyIncoming = (message, { isCurrent = false } = {}) => {
    const idx = conversationList.value.findIndex(
      (c) => c.id === message.conversationId,
    );
    if (idx < 0) {
      // 不在原本的list
      // 如果是未訂閱看板 就不重新抓列表
      if (boardConversation.value?.id !== message.conversationId)
        getConversations(); // 新會話，重新拉列表
      return;
    }

    const c = conversationList.value[idx];
    c.lastMessage = message.content;
    c.updateTime = message.createTime;
    if (!isCurrent && !message.mine && c.type === "DIRECT") {
      c.unreadCount = (c.unreadCount ?? 0) + 1;
    }

    // 移到對話列表頂端
    conversationList.value.splice(idx, 1); //把原本cid紀錄刪掉
    conversationList.value.unshift(c); // 插入列表頂端
  };

  const reset = () => {
    conversationList.value = [];
    currentConversationId.value = null;
    boardConversation.value = null;
    error.value = null;
    closeChat();
  };

  return {
    conversationList,
    currentConversationId,
    currentConversation,
    boardConversation,
    isMinimized,
    loading,
    error,
    totalUnread,

    getConversations, // 取得此使用者所有conversation(私訊、有訂閱的看板)
    directConversation, // 建立或取得私訊對話
    getboardConversation, // 取得看板聊天室
    subscribeBoard, //訂閱看板
    unsubscribeBoard, //取消訂閱看板
    clearUnreadCount, // 清除某對話未讀
    applyIncoming, // 收到新訊息時更新側欄：預覽文字、未讀數、排序
    persistWindow, // 儲存現在看的聊天室、狀態
    restoreWindow, // 重整後恢復聊天視窗及狀態
    openChat, // 進入cId聊天 看板聊天可帶dto
    closeChat, // 關閉聊天
    toggleMinimize, // 切換視窗狀態
    reset, //對話列表、目前儲存對話id清空並關閉對話
  };
});
