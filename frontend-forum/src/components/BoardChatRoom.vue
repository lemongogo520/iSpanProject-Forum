<template>
  <v-card class="chat-room rounded-xl" border>
    <!-- 聊天室標題 -->
    <v-card-item class="chat-header">
      <template #prepend>
        <v-avatar color="primary" variant="tonal">
          <v-icon icon="mdi-chat-processing-outline" />
        </v-avatar>
      </template>

      <v-card-title>看板聊天室</v-card-title>

      <v-card-subtitle>
        {{ boardName || `看板 ${boardId}` }}
      </v-card-subtitle>
    </v-card-item>

    <v-divider />

    <!-- 展示模式提示 -->
    <v-alert type="info" variant="tonal" density="compact" class="ma-4 mb-0">
      目前為前端展示模式，重新整理後訊息會消失。
    </v-alert>

    <!-- 訊息列表 -->
    <div ref="messageListRef" class="message-list">
      <!-- 沒有訊息 -->
      <div v-if="messages.length === 0" class="empty-message">
        <v-icon icon="mdi-message-text-outline" size="48" class="mb-2" />

        <div>目前還沒有訊息</div>
        <div class="text-body-2">來打聲招呼吧！</div>
      </div>

      <!-- 訊息 -->
      <div
        v-for="message in messages"
        :key="message.id"
        class="message-row"
        :class="{
          'message-row-own': isOwnMessage(message),
        }"
      >
        <div class="message-wrapper">
          <div class="message-user">
            {{ message.userName }}
          </div>

          <div
            class="message-bubble"
            :class="{
              'message-bubble-own': isOwnMessage(message),
              'message-bubble-other': !isOwnMessage(message),
            }"
          >
            {{ message.content }}
          </div>

          <div class="message-time">
            {{ formatTime(message.createdAt) }}
          </div>
        </div>
      </div>
    </div>

    <v-divider />

    <!-- 訊息輸入區 -->
    <div class="message-input-area">
      <v-text-field
        v-model="messageInput"
        label="輸入訊息"
        placeholder="按 Enter 發送"
        variant="outlined"
        density="compact"
        hide-details
        :maxlength="maxMessageLength"
        :disabled="sending"
        @keydown.enter="handleEnter"
      />

      <div class="input-footer">
        <span
          class="text-caption"
          :class="{
            'text-error': messageInput.length >= maxMessageLength,
          }"
        >
          {{ messageInput.length }} / {{ maxMessageLength }}
        </span>

        <v-btn
          color="primary"
          prepend-icon="mdi-send"
          :loading="sending"
          :disabled="!canSend"
          @click="sendMessage"
        >
          發送
        </v-btn>
      </div>
    </div>
  </v-card>
</template>

<script setup>
import { computed, nextTick, ref } from "vue";

const props = defineProps({
  boardId: {
    type: [Number, String],
    required: true,
  },

  boardName: {
    type: String,
    default: "",
  },
});

// TODO：登入 API 完成後，改成真正登入的使用者
const currentUser = {
  id: "current-demo-user",
  name: "展示使用者",
};

// TODO：後端聊天室 API 完成後，改成從 API 載入
const messages = ref([
  {
    id: 1,
    userId: "demo-user-1",
    userName: "小明",
    content: "大家好，歡迎來到這個看板的聊天室！",
    createdAt: new Date(Date.now() - 5 * 60 * 1000),
  },
  {
    id: 2,
    userId: "current-demo-user",
    userName: "展示使用者",
    content: "你好！目前聊天室還是前端展示模式。",
    createdAt: new Date(Date.now() - 2 * 60 * 1000),
  },
]);

const messageInput = ref("");
const sending = ref(false);
const messageListRef = ref(null);

const maxMessageLength = 200;

// 判斷訊息能否發送
const canSend = computed(() => {
  const cleanContent = messageInput.value.trim();

  return (
    cleanContent.length > 0 &&
    cleanContent.length <= maxMessageLength &&
    !sending.value
  );
});

// 判斷是不是自己的訊息
const isOwnMessage = (message) => {
  return message.userId === currentUser.id;
};

// 顯示訊息時間
const formatTime = (date) => {
  return new Intl.DateTimeFormat("zh-TW", {
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  }).format(new Date(date));
};

// Enter 發送，Shift + Enter 換行
const handleEnter = (event) => {
  if (event.shiftKey) {
    return;
  }

  event.preventDefault();
  sendMessage();
};

// 發送訊息
const sendMessage = async () => {
  if (!canSend.value) {
    return;
  }

  const cleanContent = messageInput.value.trim();

  sending.value = true;

  try {
    // TODO：後端完成後，改成呼叫發送訊息 API
    messages.value.push({
      id: Date.now(),
      userId: currentUser.id,
      userName: currentUser.name,
      content: cleanContent,
      createdAt: new Date(),
    });

    messageInput.value = "";

    await nextTick();

    // 發送後捲動到最新訊息
    messageListRef.value?.scrollTo({
      top: messageListRef.value.scrollHeight,
      behavior: "smooth",
    });
  } finally {
    sending.value = false;
  }
};
</script>

<style scoped>
.chat-room {
  overflow: hidden;
}

.chat-header {
  background-color: #fafafa;
}

.message-list {
  height: 250px;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
}

.empty-message {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: rgba(0, 0, 0, 0.6);
  text-align: center;
}

.message-row {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 10px;
}

.message-row-own {
  justify-content: flex-end;
}

.message-wrapper {
  display: flex;
  flex-direction: column;
  max-width: 72%;
}

.message-row-own .message-wrapper {
  align-items: flex-end;
}

.message-user {
  margin-bottom: 4px;
  padding: 0 4px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.6);
}

.message-bubble {
  padding: 8px 10px;
  border-radius: 16px;
  line-height: 1.5;
  overflow-wrap: anywhere;
  white-space: pre-wrap;
}

.message-bubble-other {
  color: #212121;
  background-color: #ffffff;
  border-bottom-left-radius: 4px;
}

.message-bubble-own {
  color: #ffffff;
  background-color: rgb(var(--v-theme-primary));
  border-bottom-right-radius: 4px;
}

.message-time {
  margin-top: 4px;
  padding: 0 4px;
  font-size: 11px;
  color: rgba(0, 0, 0, 0.5);
}

.message-input-area {
  padding: 16px;
  background-color: #ffffff;
}

.input-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
}

@media (max-width: 600px) {
  .message-list {
    height: 320px;
    padding: 14px;
  }

  .message-wrapper {
    max-width: 88%;
  }

  .message-input-area {
    padding: 12px;
  }
}
</style>
