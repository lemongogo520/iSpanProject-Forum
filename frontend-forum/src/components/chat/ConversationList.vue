<template>
  <v-list density="comfortable" nav>
    <v-skeleton-loader v-if="chatStore.loading" type="list-item-avatar" />

    <v-list-item v-for="c in chatStore.conversationList" :key="c.id"
      :to="{ name: 'chat', params: { conversationId: c.id }, replace: true }"
      :active="c.id === chatStore.currentConversationId" lines="two">
      <template #prepend>
        <DollAvatar v-if="c.type === 'DIRECT'" :userId="c?.targetUserId" :scale="1.5" :size="48"></DollAvatar>
        <v-avatar v-else size="48" color="primary" variant="tonal">
          <v-icon>mdi-forum</v-icon>
        </v-avatar>
      </template>
      <div class="ml-4">
        <v-list-item-title>{{ c.title ?? "對話" }}</v-list-item-title>
        <v-list-item-subtitle>{{ c.lastMessage ?? "尚無訊息" }}</v-list-item-subtitle>
      </div>

      <template #append>
        <v-badge v-if="c.unreadCount > 0" :content="c.unreadCount > 99 ? '99+' : c.unreadCount" color="error" inline />
      </template>
    </v-list-item>
  </v-list>
</template>
<script setup>
import { useChatStore } from '@/stores/conversation';
import DollAvatar from '../doll/DollAvatar.vue';

const chatStore = useChatStore();
</script>
<style></style>
