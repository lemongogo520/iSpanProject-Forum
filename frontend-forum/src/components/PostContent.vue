<template>
  <section>
    <!-- 文章內容 -->
    <v-card elevation="1" class="post-card">
      <v-row no-gutters>
        <!-- 作者區 -->
        <v-col cols="12" md="2" class="author-area">
          <v-avatar size="90" class="mb-4">
            <v-icon icon="mdi-account" size="55" />
          </v-avatar>

          <div class="text-subtitle-1 font-weight-bold">
            {{ post.userName || "使用者" }}
          </div>

          <div class="text-caption text-medium-emphasis mt-1">
            使用者
          </div>
        </v-col>

        <!-- 文章內容區 -->
        <v-col cols="12" md="10" class="pa-6">
          <div class="text-subtitle-2 text-medium-emphasis mb-4">
            {{ formatDate(post.createTime) }}
          </div>

          <v-card-title class="px-0 text-h5 font-weight-bold">
            {{ post.title }}
          </v-card-title>

          <v-divider class="my-4" />

          <v-card-text
            class="px-0 post-content"
            v-html="post.content"
          />

          <!-- 暫時保留的標籤 -->
          <div class="d-flex align-center flex-wrap ga-2 mt-6">
            <span class="font-weight-bold mr-2">
              探索主題
            </span>

            <v-chip size="small">電腦</v-chip>
            <v-chip size="small">遊戲</v-chip>
            <v-chip size="small">科技</v-chip>
          </div>
        </v-col>
      </v-row>
    </v-card>

    <!-- 按讚與文章操作 -->
    <div class="reaction-area">
      <v-btn
        variant="text"
        prepend-icon="mdi-thumb-up-outline"
      >
        {{ likeCount }}
      </v-btn>

      <v-btn
        variant="text"
        prepend-icon="mdi-thumb-down-outline"
      >
        -
      </v-btn>

      <v-spacer />

      <!-- 只有文章作者顯示 -->
      <v-menu v-if="isPostOwner">
        <template #activator="{ props: menuProps }">
          <v-btn
            v-bind="menuProps"
            icon="mdi-dots-vertical"
            variant="text"
          />
        </template>

        <v-list>
          <v-list-item
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
  </section>
</template>

<script setup>
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
});

const emit = defineEmits([
  "edit-post",
  "delete-post",
]);

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
.post-card {
  overflow: hidden;
}

.author-area {
  min-height: 320px;
  padding: 30px 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #f7f7f7;
  border-right: 1px solid #e0e0e0;
}

.post-content {
  min-height: 180px;
  line-height: 1.8;
  font-size: 16px;
}

.reaction-area {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #e0e0e0;
}

@media (max-width: 960px) {
  .author-area {
    min-height: auto;
    border-right: 0;
    border-bottom: 1px solid #e0e0e0;
  }
}
</style>