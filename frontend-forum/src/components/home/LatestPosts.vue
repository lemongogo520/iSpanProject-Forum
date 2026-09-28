<!-- 使用教學

1. <LatestPosts class="mt-6" />在要放元件的地方插入

2. 在script的地方加入import LatestPosts from "@/components/home/LatestPosts.vue"; -->

<script setup>
import { computed, onMounted, ref } from "vue";
import { getLatestPosts } from "@/services/postService";

const posts = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const activeType = ref("全部");

const tabs = ["全部", "討論", "心得", "攻略"];

const visiblePosts = computed(() => {
  if (activeType.value === "全部") return posts.value;
  return posts.value.filter((post) => post.postType === activeType.value);
});

const fetchLatestPosts = async () => {
  loading.value = true;
  errorMessage.value = "";

  try {
    const response = await getLatestPosts(6);
    posts.value = Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error("最新文章讀取失敗：", error);
    errorMessage.value = "目前無法載入最新文章";
  } finally {
    loading.value = false;
  }
};

onMounted(fetchLatestPosts);
</script>

<template>
  <section class="latest-panel" aria-labelledby="latest-title">
    <h2 id="latest-title" class="sr-only">最新文章</h2>

    <div class="latest-tabs" role="tablist" aria-label="最新文章類型">
      <button
        v-for="tab in tabs"
        :key="tab"
        type="button"
        role="tab"
        class="latest-tab"
        :class="{ active: activeType === tab }"
        :aria-selected="activeType === tab"
        @click="activeType = tab"
      >
        {{ tab === "全部" ? "最新文章" : tab }}
      </button>
    </div>

    <div v-if="loading" class="latest-state">
      <v-progress-circular indeterminate color="success" size="30" />
    </div>

    <div v-else-if="errorMessage" class="latest-state error-text">
      {{ errorMessage }}
    </div>

    <div v-else-if="visiblePosts.length === 0" class="latest-state">
      目前沒有符合條件的文章
    </div>

    <ul v-else class="latest-list">
      <li v-for="post in visiblePosts" :key="post.id" class="latest-row">
        <span class="row-dot" aria-hidden="true"></span>

        <RouterLink :to="`/post/${post.id}`" class="post-link">
          <span class="post-title">{{ post.title }}</span>
        </RouterLink>

        <span class="board-name">{{ post.boardName }}</span>

        <span class="post-author" :title="post.userName">
          {{ post.userName }}
        </span>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.latest-panel {
  width: 100%;
  overflow: hidden;
  border: 1px solid var(--forum-border);
  border-radius: 18px;
  background: var(--forum-card-bg);
  color: #233750;
  box-shadow: 0 8px 24px rgba(32, 58, 87, 0.06);
  font-family: "Segoe UI", "Microsoft JhengHei", sans-serif;
}

.latest-tabs {
  display: flex;
  overflow-x: auto;
  border-bottom: 1px solid #e7edf5;
  background: #f6f8fc;
  scrollbar-width: none;
}

.latest-tabs::-webkit-scrollbar {
  display: none;
}

.latest-tab {
  flex: 0 0 auto;
  min-height: 44px;
  padding: 0 18px;
  border: 0;
  border-right: 1px solid #e7edf5;
  background: transparent;
  color: #738094;
  font: inherit;
  font-size: 13px;
  cursor: pointer;
}

.latest-tab:hover {
  background: #eef5ff;
  color: #3478d4;
}

.latest-tab.active {
  background: var(--forum-card-bg);
  color: #3478d4;
  font-weight: 700;
  box-shadow: inset 0 -3px #3478d4;
}

.latest-tab:focus-visible,
.post-link:focus-visible {
  outline: 3px solid #3478d4;
  outline-offset: -2px;
}

.latest-list {
  margin: 0;
  padding: 10px 14px;
  list-style: none;
}

.latest-row {
  display: grid;
  grid-template-columns: 6px minmax(0, 1fr) auto 88px;
  align-items: center;
  column-gap: 8px;
  min-height: 34px;
  padding: 4px 6px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.45;
}

.latest-row:hover {
  background: #f4f8fe;
}

.row-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #8bbce0;
}

.post-link {
  min-width: 0;
  overflow: hidden;
  color: #2f435c;
  text-decoration: none;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-link:hover .post-title {
  color: #3478d4;
  text-decoration: underline;
}

.board-name {
  max-width: 100px;
  overflow: hidden;
  color: #7895ad;
  font-size: 11px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-author {
  overflow: hidden;
  color: #8996a8;
  text-align: right;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.latest-state {
  display: grid;
  min-height: 180px;
  place-items: center;
  color: #8a9299;
  font-size: 13px;
}

.error-text {
  color: #c33;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

@media (max-width: 600px) {
  .latest-tab {
    padding: 0 13px;
  }

  .latest-row {
    grid-template-columns: 6px minmax(0, 1fr) 72px;
  }

  .board-name {
    display: none;
  }
}
</style>
