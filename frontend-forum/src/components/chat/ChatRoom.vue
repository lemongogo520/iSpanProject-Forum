<template>
  <div class="chat-window" :class="{
    minimized: chatStore.isMinimized,
    fullscreen: smAndDown && !chatStore.isMinimized,
  }">
    <header class="cw-header" @click="chatStore.toggleMinimize()">
      <v-btn v-if="!isBoard" icon="mdi-arrow-expand" size="x-small" variant="text" title="展開為全頁" @click.stop="expand" />
      <span class="cw-title">{{ conversation?.title ?? "對話" }}</span>

      <v-badge v-if="chatStore.isMinimized && conversation?.unreadCount" :content="conversation.unreadCount"
        color="#e8806f" inline />

      <BoardSubscribeButton v-if="isBoard" :board-id="conversation.boardId" :icon-only="true" />

      <v-btn :icon="chatStore.isMinimized ? 'mdi-chevron-up' : 'mdi-minus'" size="x-small" variant="text"
        @click.stop="chatStore.toggleMinimize()" />
      <v-btn icon="mdi-close" size="x-small" variant="text" @click.stop="chatStore.closeChat()" />
    </header>

    <template v-if="!chatStore.isMinimized">
      <MessageList :conversationId="conversationId" :showSender="isBoard" />
      <MessageInput :conversationId="conversationId" />
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, watch, ref, onUnmounted } from "vue";
import { useDisplay } from "vuetify";
import { useChatStore } from "@/stores/conversation";
import { useMessageStore } from "@/stores/message";
import MessageList from "@/components/chat/MessageList.vue";
import MessageInput from "@/components/chat/MessageInput.vue";
import { useRoute, useRouter } from "vue-router";
import { subscribeConversation, unsubscribeConversation } from "@/services/socket";
import BoardSubscribeButton from "@/components/chat/BoardSubscribeButton.vue";
const router = useRouter();
const route = useRoute();
const props = defineProps({
  conversationId: { type: Number, required: true },
});
const chatStore = useChatStore();
const messageStore = useMessageStore();
const { smAndDown } = useDisplay();

const conversation = computed(() => chatStore.findConversation(props.conversationId));
const isBoard = computed(() => conversation.value?.type === "BOARD");
const subBusy = ref(false);


// 展開成整頁聊天視窗
const expand = () => {
  sessionStorage.setItem("chatReturnTo", route.fullPath);
  router.push(`/chat/${props.conversationId}`);
};

onMounted(async () => {
  if (isBoard.value) subscribeConversation(props.conversationId);   // STOMP訂閱
  await messageStore.getMessages(props.conversationId);
  if (!chatStore.isMinimized) {
    await messageStore.markAsRead(props.conversationId);
  }
});

onUnmounted(() => {
  if (isBoard.value) unsubscribeConversation(props.conversationId); // 離開對話就不會再收到此對話通知

  // 最小化狀態下使用者其實沒看到訊息，不標已讀
  if (!chatStore.isMinimized) {
    messageStore.markAsRead(props.conversationId);
  }
});


// 不是最小化才標已讀
watch(
  () => chatStore.isMinimized,
  (min) => {
    if (!min) messageStore.markAsRead(props.conversationId);
  },
);
</script>
<style scoped>
.chat-window {
  width: 340px;
  height: 500px;
  max-height: calc(100dvh - 64px - 16px);
  min-height: 0;
  display: flex;
  flex-direction: column;
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: 12px 12px 0 0;
  box-shadow: 0 12px 32px var(--color-shadow);
  overflow: hidden;
}

.chat-window.minimized {
  height: 44px;
}

.chat-window.fullscreen {
  position: fixed;
  top: var(--v-layout-top, 64px);
  bottom: 0;
  left: 0;
  right: 70px;
  width: auto;
  height: auto;
  border-radius: 0;
}

.cw-header {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 44px;
  flex-shrink: 0;
  padding: 0 4px 0 12px;
  cursor: pointer;
  background: var(--color-primary-deep);
  color: #ffffff;
}

.cw-title {
  flex: 1;
  min-width: 0;
  font-size: 0.9rem;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
