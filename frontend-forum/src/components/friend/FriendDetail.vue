<template>
  <div class="friend-detail">
    <template v-if="friendStore.selectedFriend">
      <div class="detail-hero rounded-lg">
        <DollAvatar :userId="friendStore.selectedFriend.userId" :scale="1.5" :size="120" />

        <div class="text-h5 font-weight-bold mt-4">{{ friendStore.selectedFriend.displayName }}</div>
        <div class="text-body-1 text-medium-emphasis"> @{{ friendStore.selectedFriend.account }}</div>
      </div>
      <!-- 近期發表文章 -->
      <section class="post-section">
        <h3 class="post-section-title">最近發表</h3>

        <v-skeleton-loader v-if="loadingPosts" type="list-item-avatar-two-line" />

        <template v-else-if="recentPosts.length">
          <!-- TODO: 改文章路徑 -->
          <v-card v-for="p in recentPosts" :key="p.id" :to="`/post/${p.id}`" variant="outlined" class="post-card">
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
                  <div class="post-title">{{ p.title }}</div>
                </div>

                <div class="post-excerpt">{{ p.excerpt }}</div>

                <div class="post-meta">
                  {{ p.boardName }} · {{ formatPostDate(p.createTime) }}
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

      <!-- 私訊 -->
      <div class="detail-actions">
        <v-btn variant="flat" class="auth-button font-weight-bold" prepend-icon="mdi-message-text"
          @click="startChat(friendStore.selectedFriend)">
          私訊
        </v-btn>
      </div>
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
        params: { size: 1 },
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
const formatPostDate = (date) => {
  if (!date) return "";

  return new Date(date).toLocaleDateString("zh-TW", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

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
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 0 16px 16px;
}

.detail-hero {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  background: var(--color-background-light);
  border: 1px solid var(--color-border);
}

.detail-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.detail-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: rgba(var(--v-theme-on-surface), 0.6);
}



.post-row {
  display: flex;
  align-items: center;
  gap: 18px;
  min-height: 104px;
}

.post-thumbnail {
  flex-shrink: 0;
  width: 130px;
  height: 80px;
  overflow: hidden;
  background-color: #f5f5f5;
  border-radius: 6px;
}
.post-main {
  flex: 1;
  min-width: 0;
}

.post-heading {
  display: flex;
  align-items: center;
  gap: 10px;
}

.post-title {
  overflow: hidden;
  color: #18181b;
  font-size: 17px;
  font-weight: 700;
  line-height: 1.4;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-excerpt {
  display: -webkit-box;
  margin-top: 9px;
  overflow: hidden;
  color: #6b7280;
  font-size: 14px;
  line-height: 1.5;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}


</style>