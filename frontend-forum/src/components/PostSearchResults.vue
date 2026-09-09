<template>
  <section v-if="keyword" class="mb-8">
    <div class="d-flex align-center justify-space-between ga-3 mb-4">
      <h2 class="text-h6 search-heading">
        「{{ keyword }}」的搜尋結果
      </h2>

      <v-btn
        variant="text"
        size="small"
        :disabled="loading"
        @click="emit('clear')"
      >
        清除搜尋
      </v-btn>
    </div>

    <div v-if="loading" class="text-center py-6">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <v-alert v-else-if="error" type="error" variant="tonal">
      {{ error }}
    </v-alert>

    <template v-else>
      <p class="text-body-2 text-medium-emphasis mb-3">
        共找到 {{ totalElements }} 篇文章
      </p>

      <v-card
        v-if="results.length === 0"
        class="pa-6 text-center rounded-lg"
        variant="tonal"
      >
        找不到符合條件的文章，請嘗試其他關鍵字。
      </v-card>

      <v-card
        v-for="post in results"
        :key="post.id"
        :to="{ name: 'PostDetail', params: { postId: post.id } }"
        class="mb-3 pa-4 rounded-lg"
        variant="outlined"
        hover
      >
        <div class="search-result-title">
          <v-chip
            color="primary"
            variant="tonal"
            size="small"
            class="me-2"
          >
            {{ post.postType || "討論" }}
          </v-chip>

          {{ post.title }}
        </div>

        <div class="text-body-2 text-medium-emphasis mt-2">
          {{ getBoardName(post.boardId) }}
          · {{ post.userName || "使用者" }}
          · 瀏覽：{{ post.views ?? 0 }}
        </div>
      </v-card>

      <v-pagination
        v-if="totalPages > 1"
        :model-value="page"
        :length="totalPages"
        :total-visible="5"
        class="mt-4"
        @update:model-value="emit('change-page', $event)"
      />
    </template>
  </section>
</template>

<script setup>
defineProps({
  keyword: {
    type: String,
    default: "",
  },
  results: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
  error: {
    type: String,
    default: "",
  },
  page: {
    type: Number,
    default: 1,
  },
  totalPages: {
    type: Number,
    default: 0,
  },
  totalElements: {
    type: Number,
    default: 0,
  },
  getBoardName: {
    type: Function,
    required: true,
  },
});

const emit = defineEmits(["clear", "change-page"]);
</script>

<style scoped>
.search-heading {
  min-width: 0;
  overflow-wrap: anywhere;
}

.search-result-title {
  color: #253349;
  font-size: 17px;
  font-weight: 600;
  line-height: 1.8;
  overflow-wrap: anywhere;
}
</style>