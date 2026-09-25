<!--使用方法-->
<!--scrpt import下面-->
<!-- import HotPostsCarousel from "@/components/home/HotPostsCarousel.vue"; -->
<!--放在你要放的地方 limit=多少代表查幾篇文章-->
<!--  <HotPostsCarousel :limit="9" />  -->

<template>
  <section class="hot-posts">
    <div class="section-header">
      <h2>🔥 熱門討論</h2>

      <RouterLink to="/categories"> 查看全部文章 → </RouterLink>
    </div>

    <v-progress-linear v-if="loading" indeterminate color="primary" />

    <v-alert v-else-if="errorMessage" type="error" variant="tonal">
      {{ errorMessage }}
    </v-alert>

    <div v-else-if="hotPosts.length" class="hot-post-viewport">
      <div
        class="hot-post-track"
        :style="{
          '--scroll-duration': `${Math.max(hotPosts.length * 5, 20)}s`,
        }"
      >
        <!-- 第一組文章 -->
        <div class="hot-post-list">
          <v-card
            v-for="post in hotPosts"
            :key="`original-${post.id}`"
            class="post-card"
            variant="outlined"
            @click="goToPost(post.id)"
          >
            <v-img
              :src="post.coverImageUrl || defaultPostImage"
              height="145"
              cover
            />

            <v-card-text class="pa-3">
              <v-chip color="primary" variant="tonal" size="small">
                {{ post.boardName }}
              </v-chip>

              <h3 class="post-title">
                {{ post.title }}
              </h3>

              <div class="author-row">
                <DollAvatar
                  v-if="post.userId"
                  :user-id="post.userId"
                  :size="30"
                  :scale="1"
                />

                <span>{{ post.userName }}</span>
              </div>

              <div class="post-stats">
                <span>👁 {{ post.views }}</span>
                <span>💬 {{ post.commentCount }}</span>
              </div>
            </v-card-text>
          </v-card>
        </div>

        <!-- 複製一組，讓動畫能夠無縫循環 -->
        <div class="hot-post-list" aria-hidden="true">
          <v-card
            v-for="post in hotPosts"
            :key="`copy-${post.id}`"
            class="post-card"
            variant="outlined"
            tabindex="-1"
            @click="goToPost(post.id)"
          >
            <v-img
              :src="post.coverImageUrl || defaultPostImage"
              height="145"
              cover
            />

            <v-card-text class="pa-3">
              <v-chip color="primary" variant="tonal" size="small">
                {{ post.boardName }}
              </v-chip>

              <h3 class="post-title">
                {{ post.title }}
              </h3>

              <div class="author-row">
                <DollAvatar
                  v-if="post.userId"
                  :user-id="post.userId"
                  :size="30"
                  :scale="1"
                />

                <span>{{ post.userName }}</span>
              </div>

              <div class="post-stats">
                <span>👁 {{ post.views }}</span>
                <span>💬 {{ post.commentCount }}</span>
              </div>
            </v-card-text>
          </v-card>
        </div>
      </div>
    </div>

    <v-alert v-else type="info" variant="tonal"> 目前還沒有熱門文章 </v-alert>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

import DollAvatar from "@/components/doll/DollAvatar.vue";
import { getHotPosts } from "@/services/postService";

import defaultPostImage from "@/assets/images/default-post.png";
const props = defineProps({
  limit: {
    type: Number,
    default: 9,
  },
});

const router = useRouter();

const hotPosts = ref([]);
const loading = ref(false);
const errorMessage = ref("");

const loadHotPosts = async () => {
  loading.value = true;
  errorMessage.value = "";

  try {
    const response = await getHotPosts(props.limit);
    hotPosts.value = response.data;
  } catch (error) {
    console.error("熱門文章載入失敗：", error);
    errorMessage.value = "目前無法載入熱門文章";
  } finally {
    loading.value = false;
  }
};

const goToPost = (postId) => {
  router.push(`/post/${postId}`);
};

onMounted(loadHotPosts);
</script>

<style scoped>
.hot-posts {
  padding: 16px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-header h2 {
  margin: 0;
}

.hot-post-viewport {
  width: 100%;
  overflow: hidden;
  padding: 8px 0 20px;
}

.hot-post-track {
  display: flex;
  width: max-content;
  animation: hot-post-scroll var(--scroll-duration) linear infinite;
  will-change: transform;
}

.hot-post-list {
  display: flex;
  gap: 12px;
  padding-right: 12px;
}

.hot-post-viewport:hover .hot-post-track,
.hot-post-viewport:focus-within .hot-post-track {
  animation-play-state: paused;
}

@keyframes hot-post-scroll {
  from {
    transform: translateX(0);
  }

  to {
    transform: translateX(-50%);
  }
}

@media (prefers-reduced-motion: reduce) {
  .hot-post-track {
    animation: none;
  }

  .hot-post-viewport {
    overflow-x: auto;
  }
}

.post-card {
  flex: 0 0 240px;
  width: 240px;
  height: 285px;
  overflow: hidden;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.post-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgb(0 0 0 / 10%);
}

.post-title {
  display: block;
  width: 100%;
  margin: 8px 0;
  overflow: hidden;
  color: #333;
  font-size: 15px;
  line-height: 1.4;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.author-row {
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 30px;
  margin-top: 6px;
  font-size: 13px;
  line-height: 1;
}

.author-row span {
  display: block;
  color: #555;
  line-height: 30px;
}

.post-stats {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 20px;
  margin-top: 6px;
  color: #757575;
  font-size: 12px;
}

.post-stats span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
</style>
