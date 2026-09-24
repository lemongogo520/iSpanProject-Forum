import { Client } from "@stomp/stompjs";

let client = null;
let messageHandler = null;


export function connectSocket({ onMessage, onReconnect, onGiveUp } = {}) {
    messageHandler = onMessage;
    if (client?.active) return;

    let firstConnect = true;
    let failures = 0;

    client = new Client({
        brokerURL: import.meta.env.VITE_WS_URL ?? "ws://localhost:8080/ws",
        reconnectDelay: 5000,
        heartbeatIncoming: 10000,
        heartbeatOutgoing: 10000,
        debug: (msg) => { },
        // debug: (msg) => console.log("[STOMP]", msg),
        onConnect: () => {
            failures = 0;
            client.subscribe("/user/queue/messages", (frame) => {
                onMessage?.(JSON.parse(frame.body));
            });

            // 重建看板訂閱
            const ids = [...topicSubs.keys()];
            topicSubs.clear();
            ids.forEach((id) => subscribeConversation(id, boardHandler));
            if (!firstConnect) onReconnect?.();
            firstConnect = false;
        },
        onWebSocketClose: () => {
            failures += 1;
            if (failures >= 5) {
                console.warn("WebSocket 連線失敗多次，停止重連");
                client?.deactivate();
                onGiveUp?.();
            }
        },
        // stomp 協定層錯誤(訂閱一個不存在的目的地)
        onStompError: (frame) => console.error("STOMP 錯誤", frame.headers.message),
        // Websocket 連不上
        onWebSocketError: (error) => console.error("WebSocket 錯誤", error),
    });
    client.activate(); // 開始連線
    if (import.meta.env.DEV) window.__stomp = client;
}

export function disconnectSocket() {
    client?.deactivate();
    client = null;
    topicSubs.clear();
}

const topicSubs = new Map();   // conversationId → subscription

export function subscribeConversation(conversationId) {
  if (!client?.connected || topicSubs.has(conversationId)) return;
  const sub = client.subscribe(`/topic/conversation.${conversationId}`, (frame) => {
    messageHandler?.(JSON.parse(frame.body));
  });
  topicSubs.set(conversationId, sub);
}

export function unsubscribeConversation(conversationId) {
  topicSubs.get(conversationId)?.unsubscribe();
  topicSubs.delete(conversationId);
}