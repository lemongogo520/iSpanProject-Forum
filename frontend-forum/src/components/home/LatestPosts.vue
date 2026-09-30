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
      <li
        v-for="post in visiblePosts"
        :key="post.id"
        class="latest-row"
      >
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
  position: relative;
  width: 100%;
  min-width: 0;
  height: 300px;
  overflow: hidden;
  border: 1px solid rgba(94, 129, 172, 0.22);
  border-radius: 20px;
  background:
    radial-gradient(
      circle at 100% 0%,
      rgba(82, 137, 210, 0.12),
      transparent 35%
    ),
    linear-gradient(145deg, #ffffff 0%, #f8fbff 100%);
  color: #233750;
  box-shadow:
    0 16px 38px rgba(28, 55, 91, 0.11),
    0 3px 10px rgba(28, 55, 91, 0.05);
  font-family: "Segoe UI", "Microsoft JhengHei", sans-serif;
}

.latest-panel::before {
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  z-index: 2;
  height: 3px;
  background: linear-gradient(
    90deg,
    #3478d4,
    #6a72dc,
    #43a6ba
  );
  content: "";
}

/* 分類頁籤 */
.latest-tabs {
  display: flex;
  align-items: center;
  gap: 5px;
  min-height: 54px;
  padding: 7px 12px;
  overflow-x: auto;
  border-bottom: 1px solid rgba(214, 226, 240, 0.9);
  background: rgba(245, 249, 254, 0.88);
  backdrop-filter: blur(12px);
  scrollbar-width: none;
}

.latest-tabs::-webkit-scrollbar {
  display: none;
}

.latest-tab {
  position: relative;
  flex: 0 0 auto;
  min-height: 36px;
  padding: 0 15px;
  border: 1px solid transparent;
  border-radius: 10px;
  background: transparent;
  color: #708096;
  font: inherit;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition:
    color 0.2s ease,
    border-color 0.2s ease,
    background 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.2s ease;
}

.latest-tab:hover {
  border-color: #d5e5f7;
  background: #ffffff;
  color: #3478d4;
  transform: translateY(-1px);
}

.latest-tab.active {
  border-color: rgba(52, 120, 212, 0.28);
  background: linear-gradient(135deg, #3478d4, #596fd1);
  color: #ffffff;
  box-shadow: 0 6px 14px rgba(52, 120, 212, 0.24);
}

/* 文章列表 */
.latest-list {
  display: flex;
  flex-direction: column;
  gap: 3px;
  margin: 0;
  padding: 8px 12px;
  list-style: none;
}

.latest-row {
  position: relative;
  display: grid;
  grid-template-columns: 32px minmax(0, 1fr) auto 86px;
  align-items: center;
  gap: 10px;
  min-height: 36px;
  padding: 2px 10px 2px 6px;
  overflow: hidden;
  border: 1px solid transparent;
  border-radius: 10px;
  font-size: 13px;
  transition:
    border-color 0.2s ease,
    background 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.2s ease;
}

.latest-row::after {
  position: absolute;
  top: 8px;
  bottom: 8px;
  left: 0;
  width: 3px;
  border-radius: 0 4px 4px 0;
  background: linear-gradient(#3478d4, #69a6e8);
  content: "";
  opacity: 0;
  transform: scaleY(0.4);
  transition:
    opacity 0.2s ease,
    transform 0.2s ease;
}

.latest-row:hover {
  border-color: #dce9f7;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 6px 16px rgba(40, 79, 124, 0.08);
  transform: translateX(4px);
}

.latest-row:hover::after {
  opacity: 1;
  transform: scaleY(1);
}
/* 標題 */
.post-link {
  min-width: 0;
  overflow: hidden;
  color: #283f5b;
  font-weight: 650;
  text-decoration: none;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-title {
  transition: color 0.2s ease;
}

.latest-row:hover .post-title {
  color: #286bbd;
}

/* 討論版標籤 */
.board-name {
  max-width: 105px;
  padding: 3px 9px;
  overflow: hidden;
  border: 1px solid #d8e9f8;
  border-radius: 999px;
  background: linear-gradient(135deg, #edf7ff, #f1f4ff);
  color: #527da8;
  font-size: 10px;
  font-weight: 700;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 作者 */
.post-author {
  overflow: hidden;
  color: #8794a6;
  font-size: 11px;
  text-align: right;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-author::before {
  margin-right: 4px;
  color: #abb5c2;
  content: "by";
  font-size: 9px;
  font-weight: 600;
}

/* 狀態 */
.latest-state {
  display: grid;
  min-height: 246px;
  place-items: center;
  padding: 24px;
  color: #8291a5;
  font-size: 13px;
  text-align: center;
}

.error-text {
  color: #c24141;
}

.latest-tab:focus-visible,
.post-link:focus-visible {
  border-radius: 6px;
  outline: 3px solid rgba(52, 120, 212, 0.35);
  outline-offset: 2px;
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

.row-dot {
  width: 7px;
  height: 7px;
  justify-self: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #3478d4, #65a9e8);
  box-shadow: 0 0 0 4px rgba(52, 120, 212, 0.1);
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.latest-row:hover .row-dot {
  box-shadow: 0 0 0 5px rgba(52, 120, 212, 0.14);
  transform: scale(1.15);
}
@media (max-width: 1100px) {
  .latest-row {
    grid-template-columns: 32px minmax(0, 1fr) 80px;
  }

  .board-name {
    display: none;
  }
}

@media (max-width: 767px) {
  .latest-panel {
    height: auto;
    min-height: 300px;
  }

  .latest-tabs {
    padding: 7px 8px;
  }

  .latest-tab {
    padding: 0 12px;
  }

  .latest-list {
    padding: 8px;
  }

  .latest-row {
    grid-template-columns: 32px minmax(0, 1fr);
    min-height: 44px;
  }

  .board-name,
  .post-author {
    display: none;
  }

  .latest-row:hover {
    transform: none;
  }
}

@media (prefers-reduced-motion: reduce) {
  .latest-row,
  .latest-row::after,
  .latest-tab,
  .row-number,
  .post-title {
    transition: none;
  }
}
</style>
