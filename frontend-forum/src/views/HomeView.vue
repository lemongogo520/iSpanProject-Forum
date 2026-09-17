<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from "vue";
import { RouterLink } from "vue-router";
import heroBanner from "@/assets/home/hero-banner.png";
import forumImage from "@/assets/home/forum.png";
import friendsImage from "@/assets/home/friends.png";
import avatarImage from "@/assets/home/avatar.png";
import shopImage from "@/assets/home/shop.png";
import tasksImage from "@/assets/home/tasks.png";

const featureImages = { forum: forumImage, friends: friendsImage, avatar: avatarImage, shop: shopImage, tasks: tasksImage };

const chatOpen = ref(false);
const infoDialog = ref(null);

const features = [
  { title: "討論區", subtitle: "一起交流，讓靈感發生", description: "瀏覽文章、分享心得，找到一起成長的夥伴。", icon: "mdi-forum-outline", color: "#3478d4", background: "#eaf3ff", to: "/forum", art: "forum" },
  { title: "好友", subtitle: "每次交流，都更靠近", description: "管理好友、開啟對話，和夥伴保持聯繫。", icon: "mdi-account-group-outline", color: "#15979b", background: "#e5f8f5", to: "/friends", art: "friends" },
  { title: "我的角色", subtitle: "打造你的專屬模樣", description: "探索紙娃娃、裝備與背包，展現個人風格。", icon: "mdi-account-circle-outline", color: "#8664cc", background: "#f1ecff", to: "/doll/user", art: "avatar" },
  { title: "商城", subtitle: "把努力變成小小獎勵", description: "使用點數選購商品、道具與角色裝飾。", icon: "mdi-storefront-outline", color: "#3b9465", background: "#eaf7ed", to: "/shop", art: "shop" },
  { title: "任務活動", subtitle: "每天都有新的小目標", description: "參與任務與活動，累積點數、收穫成就。", icon: "mdi-trophy-outline", color: "#cf7591", background: "#fff0f5", to: "/member/tasks", art: "tasks" },
];

// 使用明確的計時器切換圖片；滑鼠停留不會阻止播放。
const activeSlide = ref(0);
const paused = ref(false);
const pageHidden = ref(false);
const slides = [
  { title: "首頁形象", image: heroBanner, to: null },
  ...features.map(feature => ({ ...feature, image: featureImages[feature.art] })),
];
const currentSlide = computed(() => slides[activeSlide.value]);
const intervalMs = 5000;
let carouselTimer = null;
let motionQuery;
function stopCarousel() {
  if (carouselTimer !== null) window.clearInterval(carouselTimer);
  carouselTimer = null;
}
function startCarousel() {
  stopCarousel();
  if (paused.value || pageHidden.value) return;
  carouselTimer = window.setInterval(() => {
    activeSlide.value = (activeSlide.value + 1) % slides.length;
  }, intervalMs);
}
function selectSlide(index) {
  activeSlide.value = (index + slides.length) % slides.length;
  startCarousel();
}
function syncVisibility() { pageHidden.value = document.hidden; }
function syncMotion() { paused.value = motionQuery.matches; }
watch([paused, pageHidden], startCarousel);
onMounted(() => {
  motionQuery = window.matchMedia("(prefers-reduced-motion: reduce)");
  syncMotion();
  syncVisibility();
  motionQuery.addEventListener("change", syncMotion);
  document.addEventListener("visibilitychange", syncVisibility);
  // 預先載入後面的圖片，減少第一次換圖時的等待。
  slides.forEach(slide => { const image = new Image(); image.src = slide.image; });
  startCarousel();
});
onUnmounted(() => {
  stopCarousel();
  motionQuery?.removeEventListener("change", syncMotion);
  document.removeEventListener("visibilitychange", syncVisibility);
});

const topics = [
  { category: "程式交流", color: "blue", title: "Spring Boot 啟動失敗問題求解", user: "KevinChen", views: "1,245", comments: 23 },
  { category: "新手發問", color: "green", title: "請問如何在 Java 中連接 SQL Server？", user: "小白學習中", views: "856", comments: 15 },
  { category: "心得分享", color: "deep-purple", title: "自學 Java 的心路歷程分享", user: "程式狂人", views: "632", comments: 12 },
  { category: "技術討論", color: "orange", title: "關於 Thread 和 Runnable 的疑問", user: "多執行緒達人", views: "498", comments: 8 },
  { category: "作品展示", color: "pink", title: "我的紙娃娃角色設計分享！", user: "設計小天才", views: "367", comments: 6 },
];

const announcements = ["系統維護公告", "點數獎勵活動開跑！", "新版商城上線囉"];
const dialogContent = computed(() => infoDialog.value === "about"
  ? { title: "關於我們", text: "iSpanProject 是結合論壇、好友、角色、點數與商城的交流平台，讓分享與學習成為每天的日常。" }
  : { title: "聯絡我們", text: "聯絡方式尚未公布，請留意後續系統公告。" });
</script>

<template>
  <div class="home-page">
    <div class="home-content">
      <section class="hero-carousel" aria-label="首頁功能輪播" aria-roledescription="輪播" @keydown.left.prevent="selectSlide(activeSlide - 1)" @keydown.right.prevent="selectSlide(activeSlide + 1)">
        <div class="carousel-stage" :aria-live="paused ? 'polite' : 'off'">
          <Transition name="banner-fade" mode="out-in">
            <component :is="currentSlide.to ? RouterLink : 'div'" :key="currentSlide.title" v-bind="currentSlide.to ? { to: currentSlide.to } : {}" :class="['slide-link', { 'slide-display-only': !currentSlide.to }]" :aria-label="currentSlide.to ? `前往${currentSlide.title}` : currentSlide.title" @focus="paused = true">
              <img :src="currentSlide.image" :alt="`${currentSlide.title}主題圖片`" class="slide-image" />
              <div v-if="currentSlide.to" class="slide-shade"></div>
              <div v-if="currentSlide.to" class="slide-copy">
                <span class="slide-eyebrow">iSpanProject · {{ String(activeSlide + 1).padStart(2, "0") }} / {{ String(slides.length).padStart(2, "0") }}</span>
                <h1>{{ currentSlide.title }}</h1>
                <h2>{{ currentSlide.subtitle }}</h2>
                <p>{{ currentSlide.description }}</p>                
              </div>
            </component>
          </Transition>
          <button type="button" class="carousel-arrow arrow-prev" aria-label="上一張圖片" @click="selectSlide(activeSlide - 1)"><v-icon icon="mdi-chevron-left" /></button>
          <button type="button" class="carousel-arrow arrow-next" aria-label="下一張圖片" @click="selectSlide(activeSlide + 1)"><v-icon icon="mdi-chevron-right" /></button>
        </div>
        <button type="button" class="carousel-play-toggle" :aria-label="paused ? '開始自動輪播' : '暫停自動輪播'" :title="paused ? '開始自動輪播' : '暫停自動輪播'" @click="paused = !paused">
          <v-icon :icon="paused ? 'mdi-play' : 'mdi-pause'" size="20" />
        </button>
      </section>

      <section class="feature-section" aria-labelledby="feature-title">
        <div class="section-heading"><h2 id="feature-title">今天，想探索什麼？</h2><span class="muted">從這裡開始你的旅程</span></div>
        <div class="feature-grid">
          <RouterLink v-for="(feature, index) in features" :key="feature.to" :to="feature.to" class="feature-card" :style="{ '--accent': feature.color, '--tint': feature.background, '--delay': `${index * -0.65}s` }">
            <div class="feature-art feature-photo">
              <img :src="featureImages[feature.art]" :alt="`${feature.title}插畫`" loading="lazy" />
            </div>
            <div class="feature-copy"><h3>{{ feature.title }}<v-icon icon="mdi-arrow-top-right" size="19" /></h3><p>{{ feature.description }}</p></div>
          </RouterLink>
        </div>
      </section>

      <div class="community-grid">
        <section class="panel" aria-labelledby="topics-title">
          <div class="panel-heading"><h2 id="topics-title"><v-icon icon="mdi-fire" color="orange" size="23" />熱門討論</h2><v-chip size="x-small" variant="tonal">示範資料</v-chip></div>
          <RouterLink v-for="topic in topics" :key="topic.title" to="/forum" class="topic-item">
            <v-chip :color="topic.color" size="small" variant="tonal">{{ topic.category }}</v-chip>
            <div class="topic-copy"><h3>{{ topic.title }}</h3><p>{{ topic.user }} <span>· {{ topic.views }} 次瀏覽 · {{ topic.comments }} 則回覆</span></p></div>
            <v-icon icon="mdi-chevron-right" size="20" color="grey" />
          </RouterLink>
          <div class="panel-action"><v-btn to="/forum" color="primary" variant="text" append-icon="mdi-arrow-right">查看全部文章</v-btn></div>
        </section>
        <aside class="panel announcement-panel" aria-labelledby="announcement-title">
          <div class="panel-heading"><h2 id="announcement-title"><v-icon icon="mdi-bullhorn-outline" color="primary" size="23" />系統公告</h2><v-chip size="x-small" variant="tonal">示範資料</v-chip></div>
          <ul class="announcement-list"><li v-for="announcement in announcements" :key="announcement">{{ announcement }}</li></ul>
          <div class="friend-invitation"><v-icon icon="mdi-chat-processing-outline" size="36" /><h3>和夥伴聊聊吧</h3><p>讓每個想法，都有分享的對象。</p><v-btn to="/friends" color="primary" variant="tonal" rounded="pill">前往好友</v-btn></div>
        </aside>
      </div>
    </div>

    <footer class="footer"><button type="button" @click="infoDialog = 'about'">關於我們</button><span aria-hidden="true">｜</span><button type="button" @click="infoDialog = 'contact'">聯絡我們</button></footer>

    <v-btn class="chat-floating" color="primary" icon="mdi-chat-outline" size="large" elevation="5" aria-label="開啟好友聊天" @click="chatOpen = true" />
    <v-dialog v-model="chatOpen" max-width="400">
      <v-card rounded="xl">
        <v-card-title class="dialog-heading">好友聊天<v-btn icon="mdi-close" variant="text" aria-label="關閉好友聊天" @click="chatOpen = false" /></v-card-title>
        <v-card-text class="pb-6"><p class="mb-4">到好友頁面查看好友與可用的聊天功能。</p><v-btn block color="primary" to="/friends" @click="chatOpen = false">前往好友</v-btn></v-card-text>
      </v-card>
    </v-dialog>
    <v-dialog :model-value="infoDialog !== null" max-width="440" @update:model-value="value => { if (!value) infoDialog = null; }">
      <v-card rounded="xl"><v-card-title class="dialog-heading">{{ dialogContent.title }}<v-btn icon="mdi-close" variant="text" aria-label="關閉視窗" @click="infoDialog = null" /></v-card-title><v-card-text class="pb-6">{{ dialogContent.text }}</v-card-text></v-card>
    </v-dialog>
  </div>
</template>

<style scoped>
.home-page { min-height: 100vh; display: flex; flex-direction: column; color: #233750; background: #f6f8fc; font-family: "Microsoft JhengHei", Arial, sans-serif; }
.home-content { width: 100%; max-width: 1440px; margin: 0 auto; padding: 18px 36px 56px; flex: 1; }
.home-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 16px; margin-bottom: 20px; }
.home-label { color: #76869b; font-size: 14px; letter-spacing: 2px; }
.member-trigger { text-transform: none; letter-spacing: 0; }
.member-heading { display: flex; gap: 12px; align-items: center; }
.muted { color: #788598; font-size: 13px; }
.balance-row { display: flex; justify-content: space-between; gap: 12px; }
.balance-row strong { color: #bc841c; }
.hero-card { position: relative; display: flex; min-height: 330px; align-items: center; overflow: hidden; border: 1px solid #e0edf7; border-radius: 24px; background: linear-gradient(115deg, #e9f4ff 0%, #eaf8fc 54%, #edeaff 100%); }
.hero-copy { position: relative; z-index: 1; padding: 48px; width: 68%; }
.eyebrow { font-size: 11px; font-weight: 700; color: #6387b2; letter-spacing: 3px; }
.hero-copy h1 { margin: 14px 0; font-size: clamp(26px, 3vw, 40px); line-height: 1.4; }
.hero-copy h1 span { color: #3478d4; }
.hero-copy p { margin-bottom: 24px; color: #627b93; font-size: 16px; line-height: 1.9; }
.hero-decoration { position: absolute; right: 5%; top: 50%; width: 280px; height: 280px; transform: translateY(-50%); }
.orbit { position: absolute; border: 1px solid #ffffffa8; border-radius: 50%; inset: 0; }
.orbit-two { inset: 28px; }
.hero-symbol { position: absolute; inset: 64px; display: grid; place-items: center; border-radius: 38px; color: #6893d5; background: #ffffffde; box-shadow: 0 20px 45px #6d8abc20; transform: rotate(-9deg); }
.floating-symbol { position: absolute; display: grid; place-items: center; width: 66px; height: 66px; border-radius: 22px; background: white; animation: float 4s ease-in-out infinite; }
.symbol-star { top: 10px; left: 26px; color: #e2b047; }
.symbol-heart { top: 47px; right: -4px; color: #d984a1; animation-delay: -1s; }
.symbol-account { bottom: 5px; left: 50px; color: #56a6a5; animation-delay: -2s; }
.feature-section { margin-top: 46px; margin-bottom: 38px; }
.section-heading { display: flex; justify-content: space-between; align-items: center; gap: 12px; margin-bottom: 22px; }
.section-heading h2, .panel-heading h2 { font-size: 19px; font-weight: 700; }
.feature-grid { display: grid; grid-template-columns: repeat(5, minmax(0, 1fr)); gap: 18px; }
.feature-card { display: block; color: inherit; text-decoration: none; border: 1px solid #e7edf5; border-radius: 18px; background: white; overflow: hidden; transition: transform 0.25s ease, box-shadow 0.25s ease; }
.feature-card:hover { transform: translateY(-6px); box-shadow: 0 14px 30px #304b7212; }
.feature-card:focus-visible, .topic-item:focus-visible, .footer button:focus-visible { outline: 3px solid #3478d4; outline-offset: 4px; }
.feature-art { padding: 12px 8px 18px; background: var(--tint); color: var(--accent); text-align: center; }
.feature-svg { display: block; width: 100%; height: 150px; }
.art-float { animation: float 4s ease-in-out infinite; animation-delay: var(--delay); }
.art-caption { font-size: 11px; letter-spacing: 0.5px; }
.feature-copy { padding: 20px 18px; }
.feature-copy h3 { display: flex; justify-content: space-between; align-items: center; color: var(--accent); font-size: 18px; margin-bottom: 10px; }
.feature-copy p { color: #738094; font-size: 13px; line-height: 1.8; }
.community-grid { display: grid; grid-template-columns: minmax(0, 2fr) minmax(0, 1fr); gap: 24px; }
.panel { background: white; border: 1px solid #e7edf5; border-radius: 18px; overflow: hidden; }
.panel-heading { display: flex; align-items: center; justify-content: space-between; gap: 10px; padding: 20px 22px; border-bottom: 1px solid #f0f3f7; }
.panel-heading h2 { display: flex; align-items: center; gap: 8px; }
.topic-item { display: flex; align-items: center; gap: 16px; padding: 18px 22px; color: inherit; text-decoration: none; border-bottom: 1px solid #f0f3f7; }
.topic-item:hover { background: #f8fbff; }
.topic-copy { flex: 1; min-width: 0; }
.topic-copy h3 { font-size: 14px; font-weight: 500; line-height: 1.6; overflow-wrap: anywhere; }
.topic-copy p { color: #8996a8; font-size: 11px; margin-top: 5px; }
.panel-action { padding: 8px; text-align: center; }
.announcement-list { padding: 12px 24px 12px 42px; color: #60738a; }
.announcement-list li { padding: 10px 0; font-size: 14px; }
.announcement-list li::marker { color: #8fb4df; }
.friend-invitation { margin: 12px 20px 20px; padding: 24px 12px; text-align: center; border-radius: 14px; background: #f4f8fe; color: #6795cd; }
.friend-invitation h3 { color: #425e7d; font-size: 16px; margin: 10px 0 6px; }
.friend-invitation p { font-size: 12px; color: #8193a7; margin-bottom: 16px; }
.footer { display: flex; align-items: center; justify-content: center; gap: 14px; min-height: 76px; padding: 20px 80px; background: #203a57; color: #dce5ef; font-size: 13px; }
.footer button { color: inherit; font: inherit; cursor: pointer; }
.footer button:hover { color: white; text-decoration: underline; }
.chat-floating { position: fixed; right: 24px; bottom: 24px; z-index: 50; }
.dialog-heading { display: flex; justify-content: space-between; align-items: center; }
@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-7px); } }
@media (max-width: 1100px) {
  .feature-grid { gap: 12px; }
  .feature-copy { padding: 16px 12px; }
  .hero-decoration { right: 2%; opacity: 0.75; }
  .hero-copy { width: 75%; }
}
@media (max-width: 959px) {
  .home-content { padding: 16px 22px 36px; }
  .feature-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); }
  .community-grid { grid-template-columns: 1fr; }
  .hero-copy { padding: 36px; }
  .hero-decoration { right: -60px; opacity: 0.35; }
}
@media (max-width: 599px) {
  .home-content { padding: 12px 16px 32px; }
  .home-label { font-size: 11px; letter-spacing: 0; }
  .hero-copy { width: 100%; padding: 32px 24px; }
  .hero-copy h1 span { display: block; }
  .hero-decoration { right: -140px; opacity: 0.2; }
  .section-heading { align-items: flex-start; flex-direction: column; }
  .feature-section { margin-top: 34px; }
  .feature-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .feature-svg { height: 120px; }
  .feature-copy h3 { font-size: 16px; }
  .feature-copy p { font-size: 12px; }
  .art-caption { font-size: 10px; }
  .topic-item { gap: 10px; padding: 16px 12px; flex-wrap: wrap; }
  .topic-copy { flex-basis: calc(100% - 110px); }
  .topic-item > .v-icon { display: none; }
  .topic-copy p span { display: block; }
  .panel-heading { padding: 18px 14px; }
  .chat-floating { right: 16px; bottom: 16px; }
}
@media (prefers-reduced-motion: reduce) {
  .art-float, .floating-symbol { animation: none; }
  .feature-card { transition: none; }
  .feature-card:hover { transform: none; }
}

/* 輪播區：桌面保持完整圖片比例，避免裁掉人物；手机改為圖片在上、文字在下。 */
.hero-carousel { overflow: hidden; border-radius: 22px; background: #12243e; box-shadow: 0 16px 36px #10254024; }
.slide-link { position: relative; display: block; height: clamp(360px, 38vw, 520px); color: white; text-decoration: none; overflow: hidden; }
.slide-image { position: absolute; inset: 0; width: 100%; height: 100%; object-fit: contain; object-position: right center; background: #101d35; }
.slide-shade { position: absolute; inset: 0; background: linear-gradient(90deg, #0a172ce8 0%, #0a172cb3 30%, #0a172c20 64%, transparent 100%); }
.slide-copy { position: relative; z-index: 1; display: flex; flex-direction: column; align-items: flex-start; justify-content: center; width: 48%; height: 100%; padding: 42px 60px; }
.slide-eyebrow { color: #d6e7ff; font-size: 12px; letter-spacing: 2px; }
.slide-copy h1 { margin: 16px 0 10px; color: white; font-size: clamp(32px, 3.8vw, 52px); line-height: 1.2; }
.slide-copy h2 { color: #f1f6ff; font-size: clamp(16px, 1.7vw, 23px); line-height: 1.6; }
.slide-copy p { margin: 14px 0 26px; color: #d8e4f1; font-size: 14px; line-height: 1.9; }
.slide-cta { display: inline-flex; align-items: center; gap: 12px; background: white; color: #14315b; border-radius: 30px; padding: 12px 20px; font-size: 14px; font-weight: 700; }
.carousel-controls { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 14px 22px; background: #142b46; }
.slide-tabs { display: flex; flex-wrap: wrap; gap: 8px; }
.slide-tab { color: #c5d4e6; padding: 8px 14px; border-radius: 20px; font-size: 13px; cursor: pointer; }
.slide-tab.active { background: #356ab2; color: white; }
.slide-tab:hover { background: #294969; }
.cycle-toggle { display: flex; align-items: center; gap: 4px; color: white; font-size: 12px; white-space: nowrap; cursor: pointer; }
.cycle-toggle:disabled { opacity: 0.7; cursor: default; }
.slide-link:focus-visible, .slide-tab:focus-visible, .cycle-toggle:focus-visible { outline: 3px solid #f5c86b; outline-offset: -3px; }
.feature-photo { padding: 0; background: #17314c; }
.feature-photo img { display: block; width: 100%; aspect-ratio: 3 / 2; object-fit: cover; }
@media (max-width: 959px) {
  .slide-copy { width: 58%; padding: 30px 48px; }
}
@media (max-width: 599px) {
  .slide-link { height: auto; }
  .slide-image { position: relative; display: block; height: auto; aspect-ratio: 3 / 2; object-fit: contain; }
  .slide-shade { display: none; }
  .slide-copy { width: 100%; height: auto; min-height: 270px; padding: 24px; background: #12243e; }
  .slide-copy h1 { font-size: 30px; }
  .slide-copy p { margin: 10px 0 18px; }
  .carousel-controls { padding: 12px; gap: 8px; align-items: flex-start; }
  .slide-tabs { gap: 4px; }
  .slide-tab { padding: 7px 9px; font-size: 11px; }
  .cycle-toggle { padding-top: 7px; }
}

/* 首頁形象圖只展示原圖，不疊文字、也不提供跳轉。 */
.slide-display-only { cursor: default; }
.slide-display-only .slide-image { object-position: center; }
/* 自訂轮播与按钮样式，避免其他全域 button 样式盖过。 */
.carousel-stage { position: relative; }
.banner-fade-enter-active, .banner-fade-leave-active { transition: opacity 0.25s ease; }
.banner-fade-enter-from, .banner-fade-leave-to { opacity: 0; }
.carousel-controls button { appearance: none; border: 0; font-family: inherit; line-height: 1.5; }
.carousel-controls .slide-tab { background: transparent; color: #d7e6f8; }
.carousel-controls .slide-tab.active { background: #356ab2; color: white; }
.carousel-controls .slide-tab:hover { background: #294969; }
.carousel-controls .cycle-toggle { background: transparent; color: white; }
.carousel-arrow { position: absolute; top: 50%; z-index: 2; transform: translateY(-50%); display: grid; place-items: center; width: 36px; height: 36px; padding: 0; border: 1px solid #ffffff60; border-radius: 50%; background: #12243ecc; color: white; cursor: pointer; }
.arrow-prev { left: 10px; }
.arrow-next { right: 10px; }
.carousel-arrow:focus-visible { outline: 3px solid #f5c86b; outline-offset: 2px; }
@media (prefers-reduced-motion: reduce) {
  .banner-fade-enter-active, .banner-fade-leave-active { transition: none; }
}
/* 輪播縮窄、置中；移除下方文字列，只保留圖片上的操作圖示。 */
.hero-carousel {position: relative;width: 100%;max-width: none;margin: 12px 0 0;}
.slide-link { height: clamp(300px, 32vw, 360px); }
.slide-copy { width: 52%; padding: 28px 48px; }
.slide-copy h1 { font-size: clamp(28px, 3vw, 38px); }
.slide-copy h2 { font-size: 18px; }
.slide-copy p { margin: 10px 0 18px; font-size: 13px; }
.carousel-play-toggle { position: absolute; right: 14px; bottom: 14px; z-index: 3; display: grid; place-items: center; width: 34px; height: 34px; padding: 0; border: 1px solid #ffffff60; border-radius: 50%; background: #12243ecc; color: white; cursor: pointer; }
.carousel-play-toggle:focus-visible { outline: 3px solid #f5c86b; outline-offset: 2px; }
@media (max-width: 599px) {
  .slide-link { height: auto; }
  .slide-copy { width: 100%; min-height: 260px; padding: 24px; }
}
/* 第一張首頁形象圖與頁面背景融合 */
.slide-display-only {
  background: #f6f8fc;
}

.slide-display-only .slide-image {
  background: transparent;
  object-fit: contain;
  object-position: center;
  -webkit-mask-image: none;
  mask-image: none;
}
</style>
