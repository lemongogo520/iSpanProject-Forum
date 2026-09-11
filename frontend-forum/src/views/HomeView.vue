<script setup>
import { ref } from "vue";


const chatOpen = ref(false);

const features = [
  { title: "討論區", description: "瀏覽文章、參與討論，與大家一起交流學習", icon: "mdi-forum-outline", color: "blue", to: "/forum" },
  { title: "好友", description: "好友管理、即時聊天，和夥伴保持聯繫", icon: "mdi-account-group-outline", color: "cyan", to: "/friends" },
  { title: "我的角色", description: "紙娃娃、裝備與背包，打造專屬角色", icon: "mdi-account-circle-outline", color: "deep-purple", to: "/avatar" },
  { title: "商城", description: "使用點數購買商品、道具與裝飾", icon: "mdi-storefront-outline", color: "green", to: "/shop" },
  { title: "任務活動", description: "參與任務與活動，獲得豐富獎勵", icon: "mdi-trophy-outline", color: "pink", to: "/tasks" },
];

const topics = [
  { category: "程式交流", color: "blue", title: "Spring Boot 啟動失敗問題求解", user: "KevinChen", views: "1,245", comments: 23, time: "2 小時前" },
  { category: "新手發問", color: "green", title: "請問如何在 Java 中連接 SQL Server？", user: "小白學習中", views: 856, comments: 15, time: "3 小時前" },
  { category: "心得分享", color: "deep-purple", title: "自學 Java 的心路歷程分享", user: "程式狂人", views: 632, comments: 12, time: "5 小時前" },
  { category: "技術討論", color: "orange", title: "關於 Thread 和 Runnable 的疑問", user: "多執行緒達人", views: 498, comments: 8, time: "昨天" },
  { category: "作品展示", color: "red", title: "我的紙娃娃角色設計分享！", user: "設計小天才", views: 367, comments: 6, time: "昨天" },
];

const announcements = [
  "9/09 系統維護公告",
  "點數獎勵活動開跑！",
  "新版商城上線囉 🎉",
];

const chatMessages = [
  { avatar: "K", name: "KevinChen", message: "關於 Spring Boot 的問題……", time: "14:32" },
  { avatar: "小", name: "小白學習中", message: "謝謝你的解答！", time: "14:28" },
];
</script>

<template>
  <div class="home-page">
   

    <div class="home-content">
      <v-container fluid class="pa-0">
        <v-card class="hero-card" elevation="1" rounded="lg">
          <div class="hero-content">
            <h1>歡迎來到 <span>iSpanProject</span></h1>
            <p>論壇・點數・紙娃娃・商城整合平台</p>

            <div class="d-flex flex-wrap ga-4">
              <v-btn color="primary" size="large" to="/forum" append-icon="mdi-arrow-right">前往討論區</v-btn>
              <v-btn color="white" size="large" variant="elevated" prepend-icon="mdi-book-open-page-variant-outline">新手指南</v-btn>
            </div>
          </div>
        </v-card>

        <v-row class="mt-1" justify="center">
          <v-col v-for="feature in features" :key="feature.title" cols="12" sm="6" md="4" lg="2">
            <v-card class="feature-card fill-height" :to="feature.to" elevation="1" rounded="lg" hover>
              <v-card-item>
                <template #prepend>
                  <v-avatar :color="`${feature.color}-lighten-5`" size="58">
                    <v-icon :color="feature.color" :icon="feature.icon" size="30" />
                  </v-avatar>
                </template>

                <v-card-title>{{ feature.title }}</v-card-title>
              </v-card-item>

              <v-card-text>{{ feature.description }}</v-card-text>

              <v-card-actions>
                <v-spacer />
                <v-btn :color="feature.color" variant="text" icon="mdi-arrow-right" />
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>

        <v-row class="mt-1">
          <v-col cols="12" lg="8">
            <v-card elevation="1" rounded="lg">
              <v-card-title class="section-title">
                <v-icon color="red" icon="mdi-fire" class="mr-2" />
                熱門討論
              </v-card-title>

              <v-divider />

              <v-list lines="two">
                <template v-for="(topic, index) in topics" :key="topic.title">
                  <v-list-item class="topic-item">
                    <template #prepend>
                      <v-chip :color="topic.color" size="small" variant="tonal">{{ topic.category }}</v-chip>
                    </template>

                    <v-list-item-title class="topic-title">{{ topic.title }}</v-list-item-title>

                    <v-list-item-subtitle>
                      {{ topic.user }} ・
                      <v-icon icon="mdi-eye-outline" size="small" />
                      {{ topic.views }} ・
                      <v-icon icon="mdi-comment-outline" size="small" />
                      {{ topic.comments }} ・ {{ topic.time }}
                    </v-list-item-subtitle>

                    <template #append>
                      <v-icon color="primary" icon="mdi-chevron-right" />
                    </template>
                  </v-list-item>

                  <v-divider v-if="index < topics.length - 1" />
                </template>
              </v-list>

              <v-card-actions class="justify-center">
                <v-btn color="primary" variant="text" to="/forum" append-icon="mdi-arrow-right">查看全部文章</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>

          <v-col cols="12" lg="4">
            <v-card elevation="1" rounded="lg">
              <v-card-title class="section-title">
                <v-icon color="primary" icon="mdi-account-outline" class="mr-2" />
                個人資訊
              </v-card-title>

              <v-divider />

              <v-card-text>
                <div class="d-flex align-center ga-4">
                  <v-avatar color="blue-grey-lighten-4" size="66">
                    <span class="profile-avatar-text">A</span>
                  </v-avatar>

                  <div class="flex-grow-1">
                    <div class="text-h6 font-weight-bold">Admin</div>
                    <div class="text-primary text-caption mb-2">等級：Lv.15</div>
                    <v-progress-linear color="primary" :model-value="62.5" height="9" rounded />
                    <div class="text-caption text-medium-emphasis mt-1">1,250 / 2,000</div>
                  </div>
                </div>

                <v-sheet class="point-box mt-5 pa-3" color="blue-grey-lighten-5" rounded="lg">
                  <div class="d-flex align-center">
                    <v-avatar color="amber" size="40">
                      <v-icon color="white" icon="mdi-star-circle" />
                    </v-avatar>

                    <div class="ml-3">
                      <div class="text-caption text-medium-emphasis">點數餘額</div>
                      <div class="text-h6 font-weight-bold text-amber-darken-3">2,580</div>
                    </div>

                    <v-spacer />

                    <v-btn color="primary" size="small" variant="outlined" to="/coin">交易紀錄</v-btn>
                  </div>
                </v-sheet>
              </v-card-text>
            </v-card>

            <v-card class="mt-4" elevation="1" rounded="lg">
              <v-card-title class="section-title">
                <v-icon color="orange" icon="mdi-bullhorn-outline" class="mr-2" />
                系統公告
              </v-card-title>

              <v-divider />

              <v-list density="compact">
                <v-list-item v-for="announcement in announcements" :key="announcement" :title="announcement" prepend-icon="mdi-circle-small" />
              </v-list>

              <v-card-actions class="justify-end">
                <v-btn color="primary" variant="text" append-icon="mdi-arrow-right">查看全部公告</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </div>

    <v-footer class="footer">
      <v-container class="d-flex flex-wrap justify-space-between align-center ga-2">
        <span>© 2026 iSpanProject. All rights reserved.</span>
        <span>關於我們 ｜ 使用條款 ｜ 隱私權政策 ｜ 聯絡我們</span>
      </v-container>
    </v-footer>

    <v-dialog v-model="chatOpen" max-width="420">
      <v-card rounded="xl">
        <v-card-title class="d-flex align-center">
          <v-icon icon="mdi-chat-outline" class="mr-2" />
          好友聊天
          <v-spacer />
          <v-btn icon="mdi-close" variant="text" @click="chatOpen = false" />
        </v-card-title>

        <v-divider />

        <v-list lines="two">
          <v-list-item v-for="chat in chatMessages" :key="chat.name" :title="chat.name" :subtitle="chat.message">
            <template #prepend>
              <v-avatar color="blue-grey-lighten-4">{{ chat.avatar }}</v-avatar>
            </template>

            <template #append>
              <span class="text-caption text-medium-emphasis">{{ chat.time }}</span>
            </template>
          </v-list-item>
        </v-list>

        <v-divider />

        <v-card-text>
          <v-text-field label="輸入訊息" variant="outlined" density="comfortable" hide-details append-inner-icon="mdi-send" />
        </v-card-text>
      </v-card>
    </v-dialog>

    <v-badge class="chat-floating" color="error" content="2">
      <v-btn color="primary" icon="mdi-chat" size="x-large" elevation="8" @click="chatOpen = true" />
    </v-badge>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  color: #15243a;
  background: #f4f7fb;
  font-family: "Microsoft JhengHei", Arial, sans-serif;
}

.home-content {
  padding: 15px 28px 28px;
}

.hero-card {
  min-height: 320px;
  overflow: hidden;
  background:
    linear-gradient(90deg, rgba(223, 246, 255, 0.98) 0%, rgba(223, 246, 255, 0.9) 28%, rgba(223, 246, 255, 0.48) 50%, rgba(223, 246, 255, 0.05) 75%),
    url("@/assets/home/hero-banner.png") center right / cover no-repeat;
}

.hero-content {
  max-width: 720px;
  padding: 58px 66px;
}

.hero-content h1 {
  margin: 0 0 12px;
  font-size: clamp(32px, 4vw, 42px);
}

.hero-content h1 span {
  color: #1265da;
}

.hero-content p {
  margin: 0 0 28px;
  font-size: 21px;
}

.feature-card {
  display: flex;
  flex-direction: column;
  min-height: 215px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
}

.feature-card .v-card-text {
  flex: 1;
  color: #526176;
  line-height: 1.7;
}

.section-title {
  display: flex;
  align-items: center;
  min-height: 60px;
  font-size: 19px;
  font-weight: 700;
}

.topic-item {
  min-height: 68px;
}

.topic-item :deep(.v-list-item__prepend) {
  width: 105px;
}

.topic-title {
  color: #122039;
  font-weight: 500;
}

.profile-avatar-text {
  color: #506174;
  font-size: 26px;
  font-weight: 700;
}

.point-box {
  border: 1px solid #dbe2eb;
}

.footer {
  min-height: 72px;
  color: white;
  background: #0c2745;
  font-size: 13px;
}

.chat-floating {
  position: fixed;
  right: 30px;
  bottom: 35px;
  z-index: 50;
}

@media (max-width: 959px) {
  .home-content {
    padding: 14px;
  }

  .hero-content {
    padding: 45px 30px;
  }
}

@media (max-width: 599px) {
  .hero-card {
    min-height: 360px;
    background-position: 62% center;
  }

  .hero-content {
    padding: 40px 22px;
  }

  .hero-content p {
    font-size: 17px;
  }

  .topic-item :deep(.v-list-item__prepend) {
    width: auto;
    margin-right: 12px;
  }

  .chat-floating {
    right: 20px;
    bottom: 24px;
  }

  .footer .v-container {
    flex-direction: column;
    align-items: flex-start !important;
  }
}
</style>