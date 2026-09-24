<template>
  <section>
    <!-- 作者和文章分成兩張卡片 -->
    <v-row align="start">
      <!-- 作者卡片 -->
      <v-col cols="12" md="2" class="author-column">
        <v-card elevation="1" class="author-area rounded-lg">
          <div v-if="post.userId" class="author-doll-frame">
            <DollAvatar
              v-if="post.userId"
              :user-id="post.userId"
              :scale="2.2"
              is-full-body
              class="author-doll"
            />
          </div>
          <v-avatar v-else size="90" class="mb-4">
            <v-icon icon="mdi-account" size="55" />
          </v-avatar>

          <div class="text-subtitle-1 font-weight-bold">
            {{ post.userName || " " }}
          </div>
        </v-card>
      </v-col>

      <!-- 文章卡片 -->
      <v-col cols="12" md="10">
        <v-card elevation="1" class="post-card pa-6 rounded-lg">
          <div class="text-subtitle-2 text-medium-emphasis mb-4">
            {{ formatDate(post.createTime) }}
          </div>

          <v-card-title class="px-0 text-h5 font-weight-bold">
            <v-chip color="primary" variant="tonal" size="small" class="me-2">
              {{ post.postType || "討論" }}
            </v-chip>

            {{ post.title }}
          </v-card-title>

          <v-divider class="my-4" />

          <v-card-text class="px-0 post-content" v-html="post.content" />

          <!-- 暫時隱藏標籤 -->
          <!--
<div class="d-flex align-center flex-wrap ga-2 mt-6">
  <span class="font-weight-bold mr-2">探索主題</span>

  <v-chip size="small">電腦</v-chip>
  <v-chip size="small">遊戲</v-chip>
  <v-chip size="small">科技</v-chip>
</div>
-->

          <!-- 按讚與文章操作 -->
          <div class="reaction-area">
            <!-- 按讚按鈕 -->
            <v-btn
              class="like-button"
              :class="{
                'like-button-active': likedByCurrentUser,
              }"
              variant="tonal"
              rounded="pill"
              :color="likedByCurrentUser ? 'deep-orange' : 'grey-darken-1'"
              :prepend-icon="
                likedByCurrentUser ? 'mdi-thumb-up' : 'mdi-thumb-up-outline'
              "
              :loading="likeUpdating"
              :disabled="likeUpdating"
              @click="emit('toggle-like')"
            >
              <span class="font-weight-bold">
                {{ likeCount }}
              </span>
            </v-btn>
            <v-spacer />

            <!-- 文章作者和各版管理員顯示 -->
            <v-menu v-if="isPostOwner || canModerate">
              <template #activator="{ props: menuProps }">
                <v-btn
                  v-bind="menuProps"
                  icon="mdi-dots-vertical"
                  variant="text"
                />
              </template>

              <v-list>
                <v-list-item
                  v-if="isPostOwner"
                  title="編輯文章"
                  prepend-icon="mdi-pencil-outline"
                  @click="emit('edit-post')"
                />

                <v-list-item
                  title="刪除文章"
                  prepend-icon="mdi-delete-outline"
                  base-color="error"
                  :disabled="postDeleting"
                  @click="emit('delete-post')"
                />
              </v-list>
            </v-menu>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </section>
</template>

<script setup>
import DollAvatar from "@/components/doll/DollAvatar.vue";
defineProps({
  post: {
    type: Object,
    required: true,
  },

  likeCount: {
    type: Number,
    default: 0,
  },

  isPostOwner: {
    type: Boolean,
    default: false,
  },

  postDeleting: {
    type: Boolean,
    default: false,
  },
  likedByCurrentUser: {
    type: Boolean,
    default: false,
  },

  likeUpdating: {
    type: Boolean,
    default: false,
  },
  canModerate: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["toggle-like", "edit-post", "delete-post"]);

const formatDate = (date) => {
  if (!date) {
    return "";
  }

  return new Date(date).toLocaleString("zh-TW", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};
</script>

<style scoped>
.like-button {
  min-width: 72px;
  transition:
    color 0.2s ease,
    background-color 0.2s ease,
    transform 0.2s ease;
}

.like-button:hover {
  transform: translateY(-2px);
}

.like-button-active {
  color: #ff6d00 !important;
  background-color: #fff3e0 !important;
  box-shadow: 0 3px 10px rgba(255, 109, 0, 0.3);
  animation: like-pop 0.35s ease;
}

.like-button-active :deep(.v-icon) {
  transform: rotate(-8deg) scale(1.15);
}

@keyframes like-pop {
  0% {
    transform: scale(1);
  }

  45% {
    transform: scale(1.18) rotate(-3deg);
  }

  75% {
    transform: scale(0.96);
  }

  100% {
    transform: scale(1);
  }
}

@media (prefers-reduced-motion: reduce) {
  .like-button,
  .like-button-active {
    transition: none;
    animation: none;
  }
}
.post-card {
  overflow: hidden;
}

.author-area {
  display: flex;
  min-height: 260px;
  padding: 16px 10px;
  flex-direction: column;
  align-items: center;
  border: 1px solid var(--forum-border);
  border-radius: var(--forum-radius);
  background: var(--forum-soft-bg);
}

.post-content {
  min-height: 180px;
  line-height: 1.8;
  font-size: 16px;
  max-width: 100%;
  overflow: hidden;
}

.reaction-area {
  display: flex;
  align-items: center;
  margin-top: 24px;
  padding-top: 12px;
  border-top: 1px solid #e0e0e0;
}

:deep(.post-content iframe.ql-video) {
  display: block;
  width: 100%;
  max-width: 800px;
  aspect-ratio: 16 / 9;
  height: auto !important;
  margin: 24px auto;
  border: 0;
  border-radius: 12px;
  background-color: #000;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.16);
}
:deep(.post-content img) {
  display: inline-block;
  width: auto;
  max-width: 100%;
  height: auto;
  margin: 0;
  vertical-align: middle;
}

:deep(.post-content .ql-align-center img) {
  margin-left: auto;
  margin-right: auto;
}

:deep(.post-content .ql-align-right img) {
  margin-left: auto;
  margin-right: 0;
}
:deep(.post-content .ql-align-center) {
  text-align: center;
}

:deep(.post-content .ql-align-right) {
  text-align: right;
}

:deep(.post-content .ql-align-justify) {
  text-align: justify;
}
.author-doll-frame {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  min-height: 125px;
  margin-bottom: 8px;
  overflow: hidden;
  border: none;
  border-radius: 12px;
  background: radial-gradient(
    circle at 50% 55%,
    rgba(66, 103, 213, 0.1) 0%,
    rgba(219, 234, 255, 0.4) 38%,
    transparent 72%
  );
  box-shadow: none;
}
.author-doll-frame::after {
  position: absolute;
  bottom: 13px;
  left: 50%;
  width: 72px;
  height: 10px;
  border-radius: 50%;
  background: rgba(48, 83, 130, 0.1);
  transform: translateX(-50%);
  content: "";
}
.author-doll-frame :deep(.v-sheet) {
  position: relative;
  z-index: 1;
}
@media (max-width: 600px) {
  :deep(.post-content iframe.ql-video) {
    width: 100%;
    aspect-ratio: 16 / 9;
    height: auto !important;
    margin: 16px auto;
    border-radius: 8px;
  }
}
/* 電腦版：作者卡片跟隨畫面捲動 */
@media (min-width: 960px) {
  .author-column {
    position: sticky;
    top: 80px;
  }
}
</style>
