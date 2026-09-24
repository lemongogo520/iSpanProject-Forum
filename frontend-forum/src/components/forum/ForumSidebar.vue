<template>
  <aside class="category-sidebar">
    <nav class="category-nav" aria-label="論壇分類">
      <!-- 搜尋文章 -->
      <div class="sidebar-search">
        <h2 class="nav-title">搜尋文章</h2>

        <v-form @submit.prevent="emit('search')">
          <v-text-field
            :model-value="keyword"
            label="全站文章標題"
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            hide-details
            clearable
            :disabled="searchLoading"
            @update:model-value="emit('update:keyword', $event ?? '')"
            @click:clear="emit('clear')"
          />

          <v-btn
            type="submit"
            color="primary"
            block
            class="mt-3"
            :loading="searchLoading"
            :disabled="searchLoading"
          >
            搜尋
          </v-btn>
        </v-form>
      </div>

      <!-- 論壇概況 -->
      <section class="forum-summary">
        <h2 class="nav-title">論壇概況</h2>

        <div class="summary-grid">
          <div class="summary-item">
            <strong>{{ categories.length }}</strong>
            <span>分類</span>
          </div>

          <div class="summary-item">
            <strong>{{ boardCount }}</strong>
            <span>看板</span>
          </div>
        </div>
      </section>

      <v-divider class="my-5" />

      <!-- 分類導覽 -->
      <h2 class="nav-title">分類導覽</h2>

      <p v-if="loading" class="nav-hint">載入中…</p>
      <p v-else-if="!categories.length" class="nav-hint">尚無分類</p>

      <button
        v-for="category in categories"
        :key="category.id"
        type="button"
        class="category-nav-item"
        @click="emit('select-category', category.id)"
      >
        <span aria-hidden="true">
          {{ getCategoryIcon(category.name) }}
        </span>

        <span class="nav-name">{{ category.name }}</span>

        <span class="nav-count">
          {{ category.boards?.length ?? 0 }}
        </span>
      </button>
      <v-divider class="my-5" />

      <section class="latest-activity">
        <h2 class="nav-title">最新動態</h2>

        <p v-if="latestPosts.length === 0" class="nav-hint">目前還沒有文章</p>

        <RouterLink
          v-for="post in latestPosts"
          :key="post.id"
          :to="`/post/${post.id}`"
          class="activity-item"
        >
          <span class="activity-dot"></span>

          <span class="activity-content">
            <strong>{{ post.title }}</strong>
            <small>{{ post.boardName }}</small>
          </span>

          <v-icon icon="mdi-chevron-right" size="17" color="grey-lighten-1" />
        </RouterLink>
      </section>
    </nav>
  </aside>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  keyword: {
    type: String,
    default: "",
  },
  categories: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
  searchLoading: {
    type: Boolean,
    default: false,
  },
  getCategoryIcon: {
    type: Function,
    required: true,
  },
});

const emit = defineEmits([
  "update:keyword",
  "search",
  "clear",
  "select-category",
]);

const boardCount = computed(() =>
  props.categories.reduce(
    (total, category) => total + (category.boards?.length ?? 0),
    0,
  ),
);
const latestPosts = computed(() =>
  props.categories
    .flatMap((category) =>
      (category.boards ?? [])
        .filter((board) => board.latestPost)
        .map((board) => ({
          ...board.latestPost,
          boardName: board.name,
        })),
    )
    .sort(
      (a, b) =>
        new Date(b.createTime).getTime() -
        new Date(a.createTime).getTime(),
    )
    .slice(0, 5),
);
</script>

<style scoped>
.category-sidebar {
  position: sticky;
  top: 88px;
}

.category-nav {
  max-height: calc(100vh - 112px);
  overflow-y: auto;
  padding: 16px;
  border: 1px solid var(--forum-border);
  border-radius: 16px;
  background: var(--forum-card-bg);
}

.sidebar-search {
  width: 100%;
  min-width: 0;
}

.nav-title {
  margin-bottom: 14px;
  padding: 0 8px;
  color: #253349;
  font-size: 16px;
  font-weight: 700;
}

.nav-hint {
  padding: 8px;
  color: #6b7789;
  font-size: 14px;
}

.category-nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 12px 10px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: #34445c;
  font: inherit;
  text-align: left;
  cursor: pointer;
}

.category-nav-item + .category-nav-item {
  margin-top: 4px;
}

.category-nav-item:hover {
  background: var(--forum-hover-bg);
  color: #1867c0;
}

.category-nav-item:focus-visible {
  outline: 2px solid #1867c0;
  outline-offset: -2px;
}

.nav-name {
  flex: 1;
  min-width: 0;
  font-size: 14px;
  overflow-wrap: anywhere;
}

.nav-count {
  flex-shrink: 0;
  padding: 2px 7px;
  border-radius: 6px;
  background: #f1f5f9;
  color: #6b7789;
  font-size: 12px;
}

.forum-summary {
  margin-top: 20px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  border-radius: 12px;
  background: var(--forum-soft-bg);
}

.summary-item strong {
  color: #3478d4;
  font-size: 20px;
}

.summary-item span {
  margin-top: 2px;
  color: #7895ad;
  font-size: 12px;
}
.latest-activity {
  display: flex;
  flex-direction: column;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 9px;
  min-width: 0;
  padding: 10px 8px;
  border-radius: 10px;
  color: #34445c;
  text-decoration: none;
}

.activity-item:hover {
  color: #1867c0;
  background: var(--forum-hover-bg);
}

.activity-item:focus-visible {
  outline: 2px solid #1867c0;
  outline-offset: -2px;
}

.activity-dot {
  flex: 0 0 auto;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #8bbce0;
}

.activity-content {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-width: 0;
}

.activity-content strong {
  overflow: hidden;
  font-size: 13px;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-content small {
  margin-top: 3px;
  overflow: hidden;
  color: #8996a8;
  font-size: 11px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 959px) {
  .category-sidebar {
    position: static;
  }

.category-nav {
  max-height: calc(100vh - 112px);
  overflow-y: auto;
  padding: 16px;
  border: 1px solid var(--forum-border);
  border-radius: var(--forum-radius);
  background: var(--forum-card-bg);
  box-shadow: var(--forum-shadow);
}

  .sidebar-search {
    flex: 0 0 100%;
  }

  .nav-title {
    flex-shrink: 0;
    margin: 0;
    font-size: 14px;
  }

  .sidebar-search .nav-title {
    margin-bottom: 14px;
  }

  .category-nav-item {
    flex-shrink: 0;
    width: auto;
  }

  .category-nav-item + .category-nav-item {
    margin-top: 0;
  }

  .nav-name {
    white-space: nowrap;
  }
}
</style>
