<template>
    <v-menu v-model="open" location="bottom end" origin="top end" offset="8" :close-on-content-click="false" max-width="380">
        <!-- 選單按鈕 -->
        <template #activator="{ props }">
            <v-btn v-bind="props" icon variant="text">
                <v-badge v-if="totalBadge" :content="totalBadge > 99 ? '99+' : totalBadge" color="error">
                    <v-icon>mdi-bell</v-icon>
                </v-badge>
                <v-icon v-else>mdi-bell</v-icon>
            </v-btn>
        </template>
        <!-- 選單內容 -->
        <v-card rounded="lg" elevation="8" width="380">
            <!-- 分頁 -->
            <v-tabs v-model="tab" density="compact" grow>
                <v-tab value="notice">
                    通知
                    <v-badge v-if="unreadCount" :content="unreadCount" color="error" inline />
                </v-tab>
                <v-tab value="message">
                    訊息
                    <v-badge v-if="chatStore.totalUnread" :content="chatStore.totalUnread" color="error" inline />
                </v-tab>
            </v-tabs>

            <v-divider />

            <v-window v-model="tab">
                <!-- 事件通知 -->
                <v-window-item value="notice">
                    <div class="nc-body">
                        <v-list v-if="items.length" density="compact" nav>
                            <v-list-item v-for="n in items" :key="n.id" :class="{ 'nc-unread': !n.read }" lines="two"
                                @click="onNotice(n)">
                                <template #prepend>
                                    <v-icon :icon="n.icon" :color="n.color ?? 'grey'" />
                                </template>
                                <v-list-item-title class="text-wrap">{{ n.text }}</v-list-item-title>
                                <v-list-item-subtitle>{{ timeAgo(n.time) }}</v-list-item-subtitle>
                            </v-list-item>
                        </v-list>
                        <div v-else class="nc-empty">
                            <v-icon size="32">mdi-bell-sleep-outline</v-icon>
                            <span>目前沒有通知</span>
                        </div>
                    </div>
                    <template v-if="items.length">
                        <v-divider />
                        <div class="nc-foot">
                            <v-btn v-if="unreadCount" size="x-small" variant="text" @click="markAllRead">全部已讀</v-btn>
                            <v-btn size="x-small" variant="text" @click="clear">清空</v-btn>
                        </div>
                    </template>
                </v-window-item>

                <!-- 對話列表 -->
                <v-window-item value="message">
                    <div class="nc-body">
                        <v-list v-if="directList.length" density="compact" nav>
                            <v-list-item v-for="c in directList" :key="c.id" lines="two" @click="onConversation(c)">
                                <v-list-item-title :class="{ 'font-weight-bold': c.unreadCount > 0 }">
                                    {{ c.title ?? "對話" }}
                                </v-list-item-title>
                                <v-list-item-subtitle>{{ c.lastMessage ?? "尚無訊息" }}</v-list-item-subtitle>

                                <template #append>
                                    <div class="nc-meta">
                                        <span class="text-caption">{{ timeAgo(c.updateTime) }}</span>
                                        <v-badge v-if="c.unreadCount"
                                            :content="c.unreadCount > 99 ? '99+' : c.unreadCount" color="error"
                                            inline />
                                    </div>
                                </template>
                            </v-list-item>
                        </v-list>

                        <div v-else class="nc-empty">
                            <v-icon size="32">mdi-message-outline</v-icon>
                            <span>還沒有任何對話</span>
                        </div>
                    </div>

                    <v-divider />
                    <div class="nc-foot">
                        <v-btn size="x-small" variant="text" @click="open = false; router.push('/chat')">
                            查看全部
                        </v-btn>
                    </div>
                </v-window-item>
            </v-window>

        </v-card>
    </v-menu>
</template>
<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { useChatStore } from "@/stores/conversation";
import { useNotificationCenter } from "@/composables/useNotificationCenter";
import { timeAgo } from "@/utils/time";

const { items, unreadCount, markAllRead, markRead, clear } = useNotificationCenter();
const chatStore = useChatStore();
const router = useRouter();
const open = ref(false);
const tab = ref("notice");

// 總數 = 未讀好友通知 + 未讀訊息
const totalBadge = computed(() => unreadCount.value + chatStore.totalUnread);

const directList = computed(() =>
    chatStore.conversationList.filter((c) => c.type === "DIRECT")
);

const onNotice = (n) => {
    markRead(n.id);
    open.value = false;
    if (n.conversationId) chatStore.openChat(n.conversationId);
    else if (n.to) router.push(n.to);
};

const onConversation = (c) => {
    open.value = false;
    chatStore.openChat(c.id);
};
</script>
<style scoped>
.nc-body {
  max-height: 400px;
  overflow-y: auto;
}
.nc-foot {
  display: flex;
  justify-content: flex-end;
  gap: 4px;
  padding: 6px 8px;
}
.nc-unread {
  background: var(--color-primary-soft);
}
.nc-meta {
  display: flex;
  flex-direction: column;
  align-items: end;
  gap: 4px;
}
.nc-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 32px;
  color: var(--color-text);
  font-size: 0.875rem;
}
</style>