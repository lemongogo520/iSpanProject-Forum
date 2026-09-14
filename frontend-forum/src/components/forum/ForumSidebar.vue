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

        <v-divider class="my-5" />
      </div>

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
    </nav>
  </aside>
</template>

<script setup>
defineProps({
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
  border: 1px solid #e3e8ef;
  border-radius: 16px;
  background: #ffffff;
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
  background: #eff6ff;
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

@media (max-width: 959px) {
  .category-sidebar {
    position: static;
  }

  .category-nav {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
    max-height: none;
    overflow-x: auto;
    padding: 12px;
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
