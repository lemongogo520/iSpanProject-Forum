<template>
    <v-menu v-model="open" location="bottom end" origin="top end" offset="8" max-width="360">
        <template #activator="{ props }">
            <v-btn v-bind="props" icon variant="text">
                <v-badge v-if="chatStore.totalUnread > 0"
                    :content="chatStore.totalUnread > 99 ? '99+' : chatStore.totalUnread" color="error">
                    <v-icon>mdi-message-text</v-icon>
                </v-badge>
                <v-icon v-else>mdi-message-text</v-icon>
            </v-btn>
        </template>

        <v-card rounded="lg" elevation="8" class="msg-menu">
            <div class="msg-menu-head">
                <span class="font-weight-bold">訊息</span>
                <span v-if="chatStore.totalUnread > 0" class="text-caption text-medium-emphasis">
                    {{ chatStore.totalUnread }} 則未讀
                </span>
            </div>
            <v-divider />
            <div class="msg-menu-body">
                <v-list v-if="chatStore.conversationList.length" density="compact" nav>
                    <v-list-item v-for="c in chatStore.conversationList.filter((c) => c.type === 'DIRECT')" :key="c.id" lines="two" @click="select(c.id)">
                        <v-list-item-title :class="{ 'font-weight-bold': c.unreadCount > 0 }">
                            {{ c.title ?? "對話" }}
                        </v-list-item-title>
                        <v-list-item-subtitle>{{ c.lastMessage ?? "尚無訊息" }}</v-list-item-subtitle>
                        <template #append>
                            <div class="msg-menu-meta">
                                <span class="text-caption text-medium-emphasis">{{ timeAgo(c.updateTime) }}</span>
                                <v-badge v-if="c.unreadCount > 0" :content="c.unreadCount > 99 ? '99+' : c.unreadCount"
                                    color="error" inline />
                            </div>
                        </template>
                    </v-list-item>
                </v-list>
                <div v-else class="msg-menu-empty">
                    <v-icon size="32">mdi-message-outline</v-icon>
                    <span>還沒有任何對話</span>
                </div>
            </div>
        </v-card>
    </v-menu>
</template>
<script setup>
import { useChatStore } from '@/stores/conversation';
import { timeAgo } from "@/utils/time";
import { ref } from 'vue';

const chatStore = useChatStore();
const open = ref(false);

const select = (conversationId) => {
    chatStore.openChat(conversationId);
    open.value = false;
};


</script>
<style scoped>
.msg-menu {
    width: 200px;
}

.msg-menu-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 16px;
}

.msg-menu-body {
    max-height: 420px;
    overflow-y: auto;
}

.msg-menu-meta {
    display: flex;
    flex-direction: column;
    align-items: end;
    gap: 4px;
}

.msg-menu-empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 32px;
    color: rgba(var(--v-theme-on-surface), 0.55);
    font-size: 0.875rem;
}
</style>