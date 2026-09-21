import httpClient from "@/services/httpClient";
import { defineStore } from "pinia";
import { computed, ref } from "vue";
import { useChatStore } from "./conversation";
export const useMessageStore = defineStore("message", () => {
  const messagesMap = ref({}); // { [conversationId]: Message[] }  舊→新
  const cursorMap = ref({}); // { [conversationId]: { hasMore, nextCursor } }
  const loading = ref(false);
  const loadingOlder = ref(false);
  const sending = ref(false);
  const error = ref(null);

  const chatStore = useChatStore();
  const currentMessages = computed(() => {
    const id = useChatStore().currentConversationId;
    return id ? (messagesMap.value[id] ?? []) : [];
  });

  const currentHasMore = computed(
    () => cursorMap.value[chatStore.currentConversationId]?.hasMore ?? false,
  );

  const ensureList = (id) => {
    if (!messagesMap.value[id]) messagesMap.value[id] = [];
    return messagesMap.value[id];
  };

  // 取得最新一頁的訊息
  const getMessages = async (conversationId) => {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await httpClient.get(
        `/api/chat/conversations/${conversationId}/messages`,
      );
      messagesMap.value[conversationId] = data.items;
      cursorMap.value[conversationId] = {
        hasMore: data.hasMore,
        nextCursor: data.nextCursor,
      };
    } catch (e) {
      error.value = "取得訊息失敗";
      console.log("取得訊息失敗", e);
    } finally {
      loading.value = false;
    }
  };

  // 往上捲動 獲得舊訊息
  const loadOlderMessages = async (conversationId) => {
    const cursor = cursorMap.value[conversationId];
    if (!cursor?.hasMore || loadingOlder.value) return;
    loadingOlder.value = true;
    try {
      const { data } = await httpClient.get(
        `/api/chat/conversations/${conversationId}/messages`,
        {
          params: { before: cursor.nextCursor },
        },
      );
      ensureList(conversationId).unshift(...data.items);
      cursorMap.value[conversationId] = {
        hasMore: data.hasMore,
        nextCursor: data.nextCursor,
      };
    } catch (e) {
      console.error("載入舊訊息失敗", e);
    } finally {
      loadingOlder.value = false;
    }
  };

  // 標記已讀
  const markAsRead = async (conversationId) => {
    try {
      await httpClient.patch(`/api/chat/conversations/${conversationId}/read`);
      chatStore.clearUnreadCount(conversationId);
    } catch (e) {
      console.error("標記已讀失敗", e);
    }
  };

  // 傳送訊息
  const sendMessage = async (conversationId, content) => {
    console.log("sendMessage", content);
    const text = content;
    if (!text) {
      console.log("訊息為空", content);
      return;
    } // 訊息為空
    sending.value = true;
    try {
      console.log("try post");
      const { data } = await httpClient.post(
        `/api/chat/conversations/${conversationId}/messages`,
        {
          content: text,
          clientMsgId: crypto.randomUUID(),
        },
      );

      addMessage(data);
      chatStore.applyIncoming(data, { isCurrent: true });
      return data;
    } catch (e) {
      error.value = "傳送訊息失敗";
      console.error("傳送訊息失敗", e);
      throw e;
    } finally {
      sending.value = false;
    }
  };

  // 新增訊息 //比對clientMsgId 避免重複傳送
  const addMessage = (message) => {
    const list = ensureList(message.conversationId);
    const idx = list.findIndex(
      (m) =>
        (message.id && m.id === message.id) ||
        (message.clientMsgId && m.clientMsgId === message.clientMsgId),
    );
    if (idx >= 0) {
      list[idx] = message;
    } else {
      list.push(message);
    }
  };

  // 清除訊息
  const clearMessages = () => {
    if (conversationId) {
      delete messagesMap.value[conversationId];
      delete cursorMap.value[conversationId];
    } else {
      messagesMap.value = {};
      cursorMap.value = {};
    }
  };

  const reset = () => {
    messagesMap.value = {};
    cursorMap.value = {};
    error.value = null;
  };

  return {
    messagesMap,
    currentMessages,
    currentHasMore,
    cursorMap,
    loading,
    loadingOlder,
    sending,
    error,

    getMessages,
    loadOlderMessages,
    markAsRead,
    sendMessage,
    addMessage,
    clearMessages,
    reset,
  };
});
