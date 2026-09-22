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

    <v-slide-group v-else show-arrows class="hot-post-slider">
      <v-slide-group-item v-for="post in hotPosts" :key="post.id">
        <v-card
          class="post-card ma-2"
          width="320"
          variant="outlined"
          @click="goToPost(post.id)"
        >
          <v-img
            :src="post.coverImageUrl || defaultPostImage"
            height="180"
            cover
          />
          <v-card-text>
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
                :size="36"
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
      </v-slide-group-item>
    </v-slide-group>
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
  padding: 24px;
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

.hot-post-slider {
  margin: 0 -8px;
}

.post-card {
  width: 320px;
  height: 390px;
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
  min-height: 52px;
  margin: 14px 0;
  font-size: 18px;
  line-height: 1.45;
}

.author-row,
.post-stats {
  display: flex;
  align-items: center;
  gap: 10px;
}

.post-stats {
  justify-content: flex-end;
  color: #757575;
}
</style>
