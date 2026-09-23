import httpClient from "@/services/httpClient";
import { defineStore } from "pinia";
import { computed, ref } from "vue";
import { useChatStore } from "./conversation";
export const useMessageStore = defineStore("message", () => {
  const messagesMap = ref({}); // { [conversationId]: Message[] }  舊→新
  const cursorMap = ref({}); // { [conversationId]: { hasMore, nextCursor } }
  const loading = ref(false);
  const loadingOlder = ref(false);
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


      const fetched = data.items;
      const known = new Set(fetched.map((m) => m.clientMsgId));
      const lastId = fetched.at(-1)?.id ?? 0;

      // API 回傳裡沒有的 —— id == null 是本地還沒送成功的,id > lastId 是 WS 剛推進來的。
      const keep = (messagesMap.value[conversationId] ?? []).filter(
        (m) => !known.has(m.clientMsgId) && (m.id == null || m.id > lastId)
      );

      messagesMap.value[conversationId] = [...fetched, ...keep];


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
  const genId = () =>
    crypto.randomUUID?.() ??
    "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (c) => {
      const r = (Math.random() * 16) | 0;
      return (c === "x" ? r : (r & 0x3) | 0x8).toString(16);
    });

  // 實際送出（首次送出與重送共用）
  const postMessage = async (conversationId, content, clientMsgId) => {
    try {
      const { data } = await httpClient.post(`/api/chat/conversations/${conversationId}/messages`, {
        content,
        clientMsgId,
      });
      addMessage({ ...data, status: "sent" });
      chatStore.applyIncoming(data, { isCurrent: true });
      return data;
    } catch (e) {
      patchByClientMsgId(conversationId, clientMsgId, { status: "failed" });
      console.error("傳送訊息失敗", e);
      throw e;
    }
  };

  const sendMessage = async (conversationId, content) => {
    const text = content;
    if (!text) return;
    const clientMsgId = genId();
    ensureList(conversationId).push({
      id: null,
      clientMsgId,
      conversationId,
      content: text,
      createTime: new Date().toISOString(),
      mine: true,
      status: "sending",
    });

    return postMessage(conversationId, text, clientMsgId);
  };

  // 重送
  const retryMessage = async (conversationId, clientMsgId) => {
    const msg = findByClientMsgId(conversationId, clientMsgId);
    if (!msg || msg.status !== "failed") return;
    msg.status = "sending";
    return postMessage(conversationId, msg.content, clientMsgId);
  };

  // 放棄一則失敗訊息
  const discardMessage = (conversationId, clientMsgId) => {
    const list = messagesMap.value[conversationId];
    if (!list) return;
    const idx = list.findIndex((m) => m.clientMsgId === clientMsgId);
    if (idx >= 0) list.splice(idx, 1);
  };

  const findByClientMsgId = (conversationId, clientMsgId) =>
    messagesMap.value[conversationId]?.find((m) => m.clientMsgId === clientMsgId);

  const patchByClientMsgId = (conversationId, clientMsgId, patch) => {
    const msg = findByClientMsgId(conversationId, clientMsgId);
    if (msg) Object.assign(msg, patch);
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
    error,

    getMessages,
    loadOlderMessages,
    markAsRead,
    sendMessage,
    addMessage,
    retryMessage, discardMessage,
    clearMessages,
    reset,
  };
});
