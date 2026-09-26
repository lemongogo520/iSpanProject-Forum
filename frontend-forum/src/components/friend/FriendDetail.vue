<template>
  <div class="friend-detail">
    <template v-if="friendStore.selectedFriend" class="detail-inner">
      <div class="detail-section rounded-lg">
        <DollAvatar class="detail-avatar" :userId="friendStore.selectedFriend.userId" :scale="2.2" is-full-body />
        <div class="info-main">
          <div class="detail-name">{{ friendStore.selectedFriend.displayName }}</div>
          <div class="detail-account"> @{{ friendStore.selectedFriend.account }}</div>
          <div v-if="friendStore.selectedFriend.email || friendStore.selectedFriend.birthday" class="detail-info">
            <div v-if="friendStore.selectedFriend.email" class="info-row">
              <v-icon size="18" color="var(--color-icon)">mdi-email-outline</v-icon>
              <span>{{ friendStore.selectedFriend.email }}</span>
            </div>
            <div v-if="friendStore.selectedFriend.birthday" class="info-row">
              <v-icon size="18" color="var(--color-icon)">mdi-cake-variant-outline</v-icon>
              <span>{{ formatDate(friendStore.selectedFriend.birthday) }}</span>
            </div>
          </div>
        </div>
        <!-- 私訊 -->

        <v-btn variant="flat" class="auth-button font-weight-bold" prepend-icon="mdi-message-text"
          @click="startChat(friendStore.selectedFriend)">
          私訊
        </v-btn>

      </div>
      <!-- 近期發表文章 -->
      <section class="post-section">
        <h3 class="auth-title">最近發表</h3>

        <v-skeleton-loader v-if="loadingPosts" type="list-item-avatar-two-line" />

        <template v-else-if="recentPosts.length" class="recent-post">
          <!-- TODO: 改文章路徑 -->
          <v-card v-for="p in recentPosts" :key="p.id" :to="`/post/${p.id}`" variant="outlined" class="post-card ">
            <v-card-text class="post-row pa-3">
              <!-- 左側縮圖 -->
              <div class="post-thumbnail">
                <v-img :src="defaultPostImage" width="130" height="80" cover class="rounded" />
              </div>

              <!-- 右側文章資訊 -->
              <div class="post-main">
                <div class="post-heading">
                  <v-chip color="primary" variant="tonal" size="small">
                    {{ p.postType || "討論" }}
                  </v-chip>
                  <div class="auth-title">{{ p.title }}</div>
                </div>

                <div class="post-excerpt auth-subtitle">{{ p.excerpt }}</div>

                <div class="post-meta auth-subtitle">
                  {{ p.boardName }} · {{ timeAgo(p.createTime) }}
                </div>
              </div>
            </v-card-text>
          </v-card>
        </template>

        <div v-else class="post-empty">
          <v-icon size="28">mdi-text-box-outline</v-icon>
          <span>還沒有發表過文章</span>
        </div>
      </section>

    </template>
    <div v-else class="detail-empty">
      <v-icon size="64">mdi-account-search</v-icon>
      <p>選擇一位好友查看資訊</p>
    </div>
  </div>
</template>
<script setup>
import { ref, watch } from "vue";
import { useFriendStore } from "@/stores/friend";
import DollAvatar from "@/components/doll/DollAvatar.vue";
import { useChatStore } from "@/stores/conversation";
import httpClient from "@/services/httpClient";
import defaultPostImage from "@/assets/images/default-post.png";
import { timeAgo, formatDate } from "@/utils/time";

const friendStore = useFriendStore();
const chatStore = useChatStore();

const recentPosts = ref([]);
const loadingPosts = ref(false);
watch(
  () => friendStore.selectedFriend?.userId,
  async (userId) => {
    recentPosts.value = [];
    if (!userId) return;

    loadingPosts.value = true;
    try {
      const { data } = await httpClient.get(`/api/users/${userId}/posts`, {
        params: { size: 3 },
      });
      if (friendStore.selectedFriend?.userId === userId) recentPosts.value = data;
    } catch (e) {
      console.error("取得最近文章失敗", e);
    } finally {
      if (friendStore.selectedFriend?.userId === userId) loadingPosts.value = false;
    }
  },
  { immediate: true }
);

const startChat = async (friend) => {
  const c = await chatStore.directConversation(friend.userId);
  chatStore.openChat(c.id);
};
</script>
<style scoped>
.friend-detail {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 0 24px 24px;
  display: flex;
  flex-direction: column;
}

.detail-inner > *{
  max-width: 720px;
  width: 100%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  gap: 20px;
}

.detail-section {
  flex: none;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 24px 28px;
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: var(--forum-radius, 14px);
}

.detail-avatar {
  flex-shrink: 0;
}

.info-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.detail-name {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--color-title);
}

.detail-account {
  font-size: 0.8rem;
  color: var(--color-text);
}

/* 生日 email */
.detail-info {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.85rem;
  color: var(--color-text);
}


.detail-actions {
  flex-shrink: 0;
  align-self: center;
}

.detail-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: var(--color-text);
}

@media (max-width: 599px) {
  .detail-section {
    flex-direction: column;
    text-align: center;
  }

  .info-main {
    align-items: center;
  }
}

.post-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow-y: auto;
}


.post-card {
  display: block;
  flex-shrink: 0;
  padding: 14px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--forum-radius, 14px);
  background: var(--color-card);
  color: inherit;
  text-decoration: none;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.post-card:hover {
  border-color: var(--color-primary);
  box-shadow: var(--forum-shadow, 0 6px 18px rgba(48, 83, 130, 0.08));
}

.post-row {
  display: flex;
  align-items: center;
  gap: 14px;
  min-height: 76px;
}

.post-thumbnail {
  align-items: center;
  flex-shrink: 0;
  width: 108px;
  height: 68px;
  overflow: hidden;
  border-radius: 8px;
  background-color: var(--color-background-light);
}

.post-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.post-heading {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.post-title {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  font-size: 0.95rem;
  line-height: 1.4;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-excerpt {
  display: -webkit-box;
  overflow: hidden;
  font-size: 0.85rem;
  line-height: 1.5;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}
</style>