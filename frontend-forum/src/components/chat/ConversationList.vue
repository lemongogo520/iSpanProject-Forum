<template>
  <v-list density="comfortable" nav>
    <v-skeleton-loader v-if="chatStore.loading" type="list-item-avatar" />

    <v-list-item v-for="c in chatStore.conversationList" :key="c.id" @click="chatStore.openChat(c.id)"
      :active="c.id === chatStore.currentConversationId" lines="two">
      <template #prepend>
        <v-avatar color="grey-lighten-2">
          {{ (c.title ?? "?").charAt(0) }}
        </v-avatar>
      </template>

      <v-list-item-title>{{ c.title ?? "對話" }}</v-list-item-title>
      <v-list-item-subtitle>{{ c.lastMessage ?? "尚無訊息" }}</v-list-item-subtitle>

      <template #append>
        <v-badge v-if="c.unreadCount > 0" :content="c.unreadCount > 99 ? '99+' : c.unreadCount" color="error" inline />
      </template>
    </v-list-item>
  </v-list>
</template>
<script setup>
import { useChatStore } from '@/stores/conversation';

const chatStore = useChatStore();
</script>
<style></style>
