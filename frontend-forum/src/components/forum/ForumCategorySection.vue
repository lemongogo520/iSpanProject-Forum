<template>
  <section
    :id="`category-${category.id}`"
    class="category-section"
    :style="getCategoryStyle(category.name)"
  >
    <!-- 分類標題 -->
    <div class="category-heading">
      <h2 class="category-title">
        <span aria-hidden="true">
          {{ getCategoryIcon(category.name) }}
        </span>
        {{ category.name }}
      </h2>

      <span class="category-count">
        {{ category.boards?.length ?? 0 }} 個看板
      </span>
    </div>

    <!-- 同分類的看板放在一張卡片裡 -->
    <v-card variant="flat" class="board-list">
      <RouterLink
        v-for="board in category.boards"
        :key="board.id"
        :to="`/boards/${board.id}`"
        class="board-row"
      >
        <span class="board-icon" aria-hidden="true">
          {{ getCategoryIcon(category.name) }}
        </span>

        <span class="board-info">
          <span class="board-name">
            {{ board.name }}
          </span>

          <span v-if="board.description" class="board-description">
            {{ board.description }}
          </span>
        </span>
        <span class="latest-post">
          <span class="latest-post-label">最新文章：</span>

          <span v-if="board.latestPostError" class="latest-post-empty">
            暫時無法載入
          </span>

          <span v-else-if="board.latestPost" class="latest-post-content">
            <v-chip
              size="small"
              color="primary"
              variant="tonal"
              class="latest-post-type"
            >
              {{ board.latestPost.postType || "討論" }}
            </v-chip>

            <span class="latest-post-title" :title="board.latestPost.title">
              {{ board.latestPost.title }}
            </span>
          </span>

          <span v-else class="latest-post-empty"> 尚無文章 </span>
        </span>

        <span
          v-if="(board.postCount ?? board.post_count) != null"
          class="board-count"
        >
          {{ board.postCount ?? board.post_count }} 篇文章
        </span>

        <v-icon class="board-arrow" size="20" aria-hidden="true">
          mdi-chevron-right
        </v-icon>
      </RouterLink>

      <!-- 分類內沒有看板 -->
      <div v-if="!category.boards?.length" class="empty-category">
        此分類尚未建立看板
      </div>
    </v-card>
  </section>
</template>

<script setup>
defineProps({
  category: {
    type: Object,
    required: true,
  },
  getCategoryIcon: {
    type: Function,
    required: true,
  },
  getCategoryStyle: {
    type: Function,
    required: true,
  },
});
</script>

<style scoped>

.category-section {
  margin-bottom: 36px;
  overflow: hidden;
  border: 1px solid var(--forum-border);
  border-radius: 16px;
  background: var(--forum-card-bg);
  box-shadow: 0 4px 18px rgba(30, 50, 80, 0.03);

  /* 捲動到分類時避開導覽列 */
  scroll-margin-top: 88px;
}

.category-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 0;
  padding: 16px 20px;
  background-color: #c1ffe4;
  border-left: 4px solid var(--category-accent);
  border-bottom: 1px solid var(--forum-border);
}
.category-title {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  color: #253349;
  font-size: 21px;
  font-weight: 700;
  overflow-wrap: anywhere;
}

.category-count {
  flex-shrink: 0;
  padding: 5px 12px;
  border-radius: 20px;
  background: var(--category-soft);
  color: var(--category-accent);
  font-size: 13px;
}

.board-list {
  overflow: hidden;
  border: 1px solid var(--forum-border);
  border-radius: 16px;
  background: var(--forum-card-bg);
  box-shadow: 0 4px 18px rgba(30, 50, 80, 0.03);
}

.board-row {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  color: #253349;
  text-decoration: none;
  transition: background-color 0.2s ease;
}

.board-row + .board-row {
  border-top: 1px solid #edf0f5;
}

.board-row:hover {
  background: var(--category-soft);
}

.board-row:focus-visible {
  outline: 2px solid var(--category-accent);
  outline-offset: -2px;
}

.board-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 52px;
  height: 52px;
  border-radius: 15px;
  background: var(--category-soft);
  font-size: 27px;
}

/* 左側名稱與說明 */
.board-info {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 7px;
  min-width: 0;
}

.board-name {
  font-size: 17px;
  font-weight: 700;
  line-height: 1.5;
  overflow-wrap: anywhere;
}
.board-description {
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  color: #6b7789;
  font-size: 14px;
  line-height: 1.6;
}
/* 右側最新文章：保留約四成寬度 */
.latest-post {
  display: flex;
  align-items: center;
  flex: 0 1 20%;
  gap: 12px;
  min-width: 0;
  padding-left: 24px;
  border-left: 1px solid #e3e8ef;
}

.latest-post-label {
  flex-shrink: 0;
  color: #6b7789;
  font-size: 14px;
}

.latest-post-title,
.latest-post-empty {
  min-width: 0;
  overflow: hidden;
  font-size: 16px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.latest-post-title {
  color: var(--category-accent);
  font-weight: 500;
}

.latest-post-empty {
  color: #6b7789;
}

.board-count {
  flex-shrink: 0;
  color: #6b7789;
  font-size: 13px;
}

.board-arrow {
  flex-shrink: 0;
  color: #8593a5;
}

.board-row:hover .board-name,
.board-row:hover .board-arrow {
  color: var(--category-accent);
}

.empty-category {
  padding: 28px;
  color: #6b7789;
  font-size: 14px;
  text-align: center;
}

/* 小螢幕：最新文章移到下一行 */
@media (max-width: 760px) {
  .board-row {
    flex-wrap: wrap;
    gap: 12px;
    padding: 18px 16px;
  }

  .board-icon {
    width: 42px;
    height: 42px;
    font-size: 23px;
  }

  .board-name {
    font-size: 16px;
  }

  .latest-post {
    order: 1;
    flex: 0 0 100%;
    padding-left: 54px;
    border-left: none;
  }

  .latest-post-title,
  .latest-post-empty {
    font-size: 15px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .board-row {
    transition: none;
  }
}
.latest-post-content {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  flex: 1;
}

.latest-post-type {
  flex-shrink: 0;
}
</style>
