<script setup>
import { computed, ref, watch } from "vue";
import { RouterLink, useRoute } from "vue-router";
import { useDisplay } from "vuetify";

// 組員串接：傳入會員資料與路由，或監聽 profile、logout 事件。
const props = defineProps({
  member: { type: Object, default: () => ({ name: "", account: "", avatarUrl: "", points: null }) },
  profileTo: { type: String, default: "" },
  pointsTo: { type: String, default: "/member/transactions" },
  logoutReady: { type: Boolean, default: false },
});
const emit = defineEmits(["profile", "logout"]);
const route = useRoute();
const { mdAndUp } = useDisplay();
const drawer = ref(false);
const memberMenu = ref(false);
const noticeOpen = ref(false);
const noticeText = ref("");
const avatarFailed = ref(false);
const displayName = computed(() => props.member?.name?.trim() || props.member?.account?.trim() || "會員");
const initial = computed(() => Array.from(displayName.value)[0]);
const avatarUrl = computed(() => props.member?.avatarUrl || "");
const pointsText = computed(() => {
  const value = props.member?.points;
  if (value === null || value === undefined || value === "" || (typeof value === "string" && !value.trim())) return "—";
  const number = Number(value);
  return Number.isFinite(number) && number >= 0 ? number.toLocaleString("zh-TW") : "—";
});
const navItems = [
  { title: "首頁", icon: "mdi-home-outline", to: "/home" },
  { title: "討論區", icon: "mdi-forum-outline", to: "/forum" },
  { title: "點數", icon: "mdi-star-circle-outline", to: "/member/transactions" },
  { title: "我的角色", icon: "mdi-account-circle-outline", to: "/avatar" },
  { title: "商城", icon: "mdi-storefront-outline", to: "/shop" },
  { title: "任務活動", icon: "mdi-clipboard-check-outline", to: "/member/tasks" },
];
function isActive(path) { return route.path === path || route.path.startsWith(`${path}/`); }
function closeMenus() { memberMenu.value = false; drawer.value = false; }
function showNotice(message) { noticeText.value = message; noticeOpen.value = true; }
function openProfile() {
  closeMenus();
  emit("profile");
  if (!props.profileTo) showNotice("會員個人資料功能尚未開放。");
}
function logout() {
  closeMenus();
  if (!props.logoutReady) { showNotice("登出功能尚未開放。"); return; }
  emit("logout");
}
watch(avatarUrl, () => { avatarFailed.value = false; });
watch(() => route.fullPath, closeMenus);
watch(mdAndUp, value => { if (value) drawer.value = false; });
</script>

<template>
  <v-app-bar color="#0f1f35" height="72" elevation="2">
    <div class="header-container">
      <v-app-bar-nav-icon v-if="!mdAndUp" color="white" aria-label="開啟導覽選單" @click="drawer = !drawer; memberMenu = false" />
      <RouterLink to="/home" class="brand">xx論壇</RouterLink>
      <nav v-if="mdAndUp" class="main-nav" aria-label="主要導覽">
        <v-btn v-for="item in navItems" :key="item.to" :to="item.to" :class="{ 'nav-button--active': isActive(item.to) }" class="nav-button" color="#c8d1de" variant="text" rounded="0">
          <v-icon :icon="item.icon" size="18" class="mr-1" />{{ item.title }}
        </v-btn>
      </nav>
      <v-spacer />
      <!-- 桌面與手機共用同一個會員選單。 -->
      <v-menu v-model="memberMenu" location="bottom end" :close-on-content-click="false" :offset="10" max-width="340">
        <template #activator="{ props: menuProps }">
          <v-btn v-bind="menuProps" class="user-button" variant="text" color="white" aria-label="開啟會員選單" @click="drawer = false">
            <v-avatar color="#3266a9" size="36">
              <img v-if="avatarUrl && !avatarFailed" :src="avatarUrl" alt="" class="avatar-image" @error="avatarFailed = true" />
              <span v-else class="avatar-text">{{ initial }}</span>
            </v-avatar>
            <span class="user-summary"><span class="user-name">{{ displayName }}</span><span class="user-points"><v-icon icon="mdi-star-circle" color="amber" size="14" />{{ pointsText }} 點</span></span>
            <v-icon :icon="memberMenu ? 'mdi-chevron-up' : 'mdi-chevron-down'" size="18" class="menu-chevron" />
          </v-btn>
        </template>
        <v-card class="profile-menu" rounded="xl" elevation="8">
          <div class="profile-heading">
            <v-avatar color="#e8eff9" size="48">
              <img v-if="avatarUrl && !avatarFailed" :src="avatarUrl" alt="" class="avatar-image" @error="avatarFailed = true" />
              <span v-else class="profile-initial">{{ initial }}</span>
            </v-avatar>
            <div class="profile-identity"><div class="profile-name">{{ displayName }}</div><div class="profile-caption">歡迎回來，開啟你的社群日常</div></div>
          </div>
          <div class="point-summary"><div><div class="point-label"><v-icon icon="mdi-star-circle" size="18" />目前點數</div><div class="point-value">{{ pointsText }}<span>點</span></div></div><v-btn :to="pointsTo" size="small" color="#8a621a" variant="text" append-icon="mdi-chevron-right" @click="closeMenus">查看紀錄</v-btn></div>
          <v-list class="member-actions" aria-label="會員功能">
            <v-list-item :to="profileTo || undefined" rounded="lg" prepend-icon="mdi-account-outline" title="會員個人資料" @click="openProfile"><template #append><v-icon icon="mdi-chevron-right" size="18" /></template></v-list-item>
            <v-list-item to="/friends" rounded="lg" prepend-icon="mdi-account-group-outline" title="好友" @click="closeMenus">
  <template #append>
    <v-icon icon="mdi-chevron-right" size="18" />
  </template>
</v-list-item>
            <v-divider class="my-2" />
            <v-list-item rounded="lg" prepend-icon="mdi-logout" title="登出" class="logout-item" @click="logout" />
          </v-list>
        </v-card>
      </v-menu>
    </div>
  </v-app-bar>

  <v-navigation-drawer v-model="drawer" color="#0f1f35" temporary width="280">
    <div class="drawer-title">功能導覽</div>
    <v-divider />
    <v-list nav>
      <v-list-item v-for="item in navItems" :key="item.to" :to="item.to" :title="item.title" :prepend-icon="item.icon" rounded="lg" @click="drawer = false" />
    </v-list>
  </v-navigation-drawer>
  <v-snackbar v-model="noticeOpen" :timeout="3000" location="bottom">{{ noticeText }}<template #actions><v-btn variant="text" @click="noticeOpen = false">關閉</v-btn></template></v-snackbar>
</template>

<style scoped>
.header-container { display: flex; align-items: center; width: 100%; max-width: 1400px; height: 100%; margin: 0 auto; padding: 0 24px; }
.brand { flex-shrink: 0; margin-right: 28px; color: white; font-size: 22px; font-weight: 700; text-decoration: none; }
.main-nav { display: flex; align-items: stretch; height: 100%; }
.nav-button { height: 72px !important; padding: 0 13px !important; font-size: 14px; letter-spacing: 0; text-transform: none; }
.nav-button--active { color: white !important; background: #ffffff0d; box-shadow: inset 0 -3px #649aea; }
.user-button { height: 58px !important; padding: 0 8px !important; letter-spacing: 0; text-transform: none; }
.user-summary { display: flex; flex-direction: column; align-items: flex-start; margin: 0 10px; line-height: 1.5; }
.user-name { max-width: 130px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: white; font-size: 14px; font-weight: 600; }
.user-points { display: flex; align-items: center; gap: 4px; color: #edcf85; font-size: 12px; }
.avatar-image { width: 100%; height: 100%; object-fit: cover; }
.avatar-text { color: white; font-weight: 700; }
.profile-menu { width: 340px; max-width: calc(100vw - 24px); color: #243954; }
.profile-heading { display: flex; align-items: center; gap: 12px; padding: 22px 20px 18px; }
.profile-identity { min-width: 0; }
.profile-name { overflow-wrap: anywhere; font-size: 17px; font-weight: 700; }
.profile-initial { color: #3266a9; font-size: 21px; font-weight: 700; }
.profile-caption { margin-top: 4px; color: #78879a; font-size: 11px; }
.point-summary { display: flex; align-items: center; justify-content: space-between; gap: 8px; margin: 0 16px 12px; padding: 16px; border: 1px solid #ead9ae; border-radius: 14px; background: #faf3e2; }
.point-label { display: flex; align-items: center; gap: 5px; color: #8a621a; font-size: 12px; }
.point-value { margin-top: 5px; color: #755014; font-size: 27px; font-weight: 700; overflow-wrap: anywhere; }
.point-value span { margin-left: 6px; font-size: 12px; font-weight: 400; }
.member-actions { padding: 0 10px 12px; }
.logout-item { color: #b34d4d; }
.drawer-title { padding: 24px; color: white; font-weight: 700; }
@media (max-width: 1100px) { .nav-button { padding: 0 7px !important; font-size: 13px; } .brand { margin-right: 14px; } }
@media (max-width: 599px) { .header-container { padding: 0 8px; } .brand { margin-right: 4px; font-size: 17px; } .user-summary { margin: 0 6px; } .user-name { max-width: 65px; font-size: 12px; } .user-points { font-size: 11px; } .user-button { padding: 0 4px !important; } .menu-chevron { display: none; } }
</style>
