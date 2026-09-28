<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import httpClient from "@/services/httpClient";
import UserDetailView from "@/views/user/UserDetailView.vue";
import { useUserStore } from "@/stores/user";
import { userService } from "@/services/userService";
import { useAuthStore } from "@/stores/auth";
import { useDisplay } from "vuetify";
import { useConfigStore } from '@/stores/config'
import DollAvatar from "@/components/doll/DollAvatar.vue";
import NotificationCenter from "@/components/common/NotificationCenter.vue";


const router = useRouter();
const route = useRoute();
const { mdAndUp } = useDisplay();

const forumCategories = ref([]);
const userDetailDialog = ref(false);
const userStore = useUserStore();
const authStore = useAuthStore();
const configStore = useConfigStore();

const isNavbarCompact = ref(false);
let lastScrollY = 0;

const drawer = ref(false);
const memberMenu = ref(false);
const noticeOpen = ref(false);
const noticeText = ref("");

// ==============================
// Scroll 邏輯
// ==============================
const handleWindowScroll = () => {
  const currentScrollY = window.scrollY;

  if (currentScrollY <= 12) {
    isNavbarCompact.value = false;
  } else if (currentScrollY > lastScrollY + 4) {
    isNavbarCompact.value = true;
  } else if (currentScrollY < lastScrollY - 4) {
    isNavbarCompact.value = false;
  }

  lastScrollY = currentScrollY;
};

// ==============================
// 第一次資料檢查
// ==============================
async function checkUserDetail() {
  try {
    const user = await userService.getUserDetail(userStore.account);

    if (!user.name || user.name.trim() === "") {
      userDetailDialog.value = true;
    }
  } catch (error) {
    console.error("取得使用者資料失敗：", error);
  }
}

// ==============================
// 論壇選單 API
// ==============================
const fetchForumMenu = () => {
  return httpClient({
    method: "get",
    url: "/api/categories",
  })
    .then((response) => {
      const categories = response.data ?? [];

      return Promise.all(
        categories.map((category) =>
          httpClient({
            method: "get",
            url: `/api/boards/category/${category.id}`,
          }).then((boardResponse) => ({
            ...category,
            boards: boardResponse.data ?? [],
          }))
        )
      );
    })
    .then((categories) => {
      forumCategories.value = categories;
    })
    .catch((error) => {
      console.error("取得論壇選單失敗：", error);
      forumCategories.value = [];
    });
};

// ==============================
// 使用者畫面資料
// ==============================
const user = computed(() => ({
  name: userStore.name,
  account: userStore.account,
  role: userStore.permission,
}));

// ==============================
// 權限判斷
// ==============================
const canManageForum = computed(() => {
  return [1, 2].includes(Number(userStore.permission));
});

const isAdmin = computed(() => {
  return userStore.permission === 1;
});

// ==============================
// 登出
// ==============================
const logout = () => {
  httpClient({
    method: "post",
    url: "/api/logout",
  })
    .then((response) => {
      console.log("登出成功:", response.data);

      userStore.clearUser();
      authStore.setLogout();
      router.push("/login");
    })
    .catch((error) => {
      console.error("登出失敗:", error);
    });
};

// ==============================
// 會員明細
// ==============================
function openUserDetail() {
  userDetailDialog.value = true;
}

// ==============================
// 路由設定
// ==============================
function goTo(path) {
  router.push(path);
}

// ==============================
// 重新整理
// ==============================
function reloadPage() {
  window.location.reload();
}

// ==============================
// 第一篇版面需要的 UI 行為
// 這些不會改變第二篇的資料邏輯
// ==============================
function closeMenus() {
  memberMenu.value = false;
  drawer.value = false;
}

function openProfile() {
  closeMenus();
  openUserDetail();
}

const isActive = (path) => {
  return route.path === path || route.path.startsWith(`${path}/`);
};

onMounted(async () => {
  lastScrollY = window.scrollY;

  window.addEventListener("scroll", handleWindowScroll, {
    passive: true,
  });

  await Promise.all([
    checkUserDetail(),
    fetchForumMenu(),
  ]);

  await configStore.loadSettings()
});

onBeforeUnmount(() => {
  window.removeEventListener("scroll", handleWindowScroll);
});
</script>

<template>
  <v-app-bar
    color="#0f1f35"
    :height="isNavbarCompact ? 60 : 72"
    elevation="2"
    :class="{ 'navbar-compact': isNavbarCompact }"
  >
    <div class="header-container">
      <!-- 手機版選單 -->
      <v-app-bar-nav-icon
        v-if="!mdAndUp"
        color="white"
        aria-label="開啟導覽選單"
        @click="drawer = !drawer; memberMenu = false"
      />

      <!-- Logo -->
      <span
        class="brand"
        @click="goTo('/home')"
      >
        {{ configStore.settings.web_site_name }}
      </span>

      <!-- 桌面版主導覽 -->
      <nav
        v-if="mdAndUp"
        class="main-nav"
        aria-label="主要導覽"
      >
        <v-btn
          v-for="item in [
            { title: '討論區', icon: 'mdi-forum-outline', to: '/categories' },
            { title: '點數', icon: 'mdi-star-circle-outline', to: '/member/transactions' },
            { title: '我的角色', icon: 'mdi-account-circle-outline', to: '/doll/user' },
            { title: '商城', icon: 'mdi-storefront-outline', to: '/shop' },
            { title: '任務活動', icon: 'mdi-clipboard-check-outline', to: '/member/tasks' },
          ]"
          :key="item.to"
          :to="item.to"
          :class="{ 'nav-button--active': isActive(item.to) }"
          class="nav-button"
          color="#c8d1de"
          variant="text"
          rounded="0"
        >
          <v-icon
            :icon="item.icon"
            size="18"
            class="mr-1"
          />
          {{ item.title }}
        </v-btn>

        <!-- 論壇管理權限 -->
        <v-btn
          v-if="canManageForum"
          class="nav-button"
          color="#c8d1de"
          variant="text"
          rounded="0"
          @click="goTo('/admin/categories')"
        >
          <v-icon
            icon="mdi-shield-account"
            size="18"
            class="mr-1"
          />
          論壇管理
        </v-btn>

        <!-- 後台管理權限 -->
        <v-btn
          v-if="isAdmin"
          class="nav-button"
          color="#c8d1de"
          variant="text"
          rounded="0"
          @click="goTo('/config/setting')"
        >
          <v-icon
            icon="mdi-shield-crown"
            size="18"
            class="mr-1"
          />
          後台管理
        </v-btn>
      </nav>

      <v-spacer />
      <NotificationCenter/>
      <!-- 會員選單 -->
      <v-menu
        v-model="memberMenu"
        location="bottom end"
        :close-on-content-click="false"
        :offset="10"
        max-width="340"
      >
        <template #activator="{ props: menuProps }">
          <v-btn
          v-bind="menuProps"
          class="user-button"
          variant="text"
          color="white"
          aria-label="開啟會員選單"
          @click="drawer = false"
          >
              <DollAvatar v-if="userStore.uid" :userId="userStore.uid" :size="36" :scale="1.0"/>
              <v-avatar v-else
              color="#3266a9"
              size="36"
              >
              <v-icon
              icon="mdi-account"
              size="23"
              color="white"
              />
            </v-avatar>

            <span class="user-summary">
              <span class="user-name">
                {{ user.name || user.account }}
              </span>              
            </span>

            <v-icon
              :icon="
                memberMenu
                  ? 'mdi-chevron-up'
                  : 'mdi-chevron-down'
              "
              size="18"
              class="menu-chevron"
            />
          </v-btn>
        </template>

        <!-- 會員卡片版面 -->
        <v-card
          class="profile-menu"
          rounded="xl"
          elevation="8"
        >
          <div class="profile-heading">
            <DollAvatar v-if="userStore.uid" :user-id="userStore.uid" :size="48" :scale="1.8"/>
            <v-avatar v-else
              color="#e8eff9"
              size="48"
            >
              <v-icon
                icon="mdi-account"
                size="28"
                color="#3266a9"
              />
            </v-avatar>

            <div class="profile-identity">
              <div class="profile-name">
                {{ user.name || user.account }}
              </div>

              <div class="profile-caption">
                歡迎回來，開啟你的社群日常
              </div>
            </div>
          </div>         

          <v-list
            class="member-actions"
            aria-label="會員功能"
          >
            <!-- 使用openUserDetail -->
            <v-list-item
              rounded="lg"
              prepend-icon="mdi-account-outline"
              title="會員個人資料"
              @click="openProfile"
            >
              <template #append>
                <v-icon
                  icon="mdi-chevron-right"
                  size="18"
                />
              </template>
            </v-list-item>

            <v-list-item
              to="/friends"
              rounded="lg"
              prepend-icon="mdi-account-group-outline"
              title="好友"
              @click="closeMenus"
            >
              <template #append>
                <v-icon
                  icon="mdi-chevron-right"
                  size="18"
                />
              </template>
            </v-list-item>

            <v-divider class="my-2" />

            <!-- logout -->
            <v-list-item
              rounded="lg"
              prepend-icon="mdi-logout"
              title="登出"
              class="logout-item"
              @click="logout"
            />
          </v-list>
        </v-card>
      </v-menu>
    </div>
  </v-app-bar>

  <!-- 手機版 Drawer -->
  <v-navigation-drawer
    v-model="drawer"
    color="#0f1f35"
    temporary
    width="280"
  >
    <div class="drawer-title">
      功能導覽
    </div>

    <v-divider />

    <v-list nav>
      <v-list-item
        to="/home"
        title="首頁"
        prepend-icon="mdi-home-outline"
        rounded="lg"
        @click="drawer = false"
      />

      <v-list-item
        to="/categories"
        title="討論區"
        prepend-icon="mdi-forum-outline"
        rounded="lg"
        @click="drawer = false"
      />

      <v-list-item
        to="/member/transactions"
        title="點數"
        prepend-icon="mdi-star-circle-outline"
        rounded="lg"
        @click="drawer = false"
      />

      <v-list-item
        to="/doll/user"
        title="我的角色"
        prepend-icon="mdi-account-circle-outline"
        rounded="lg"
        @click="drawer = false"
      />

      <v-list-item
        to="/shop"
        title="商城"
        prepend-icon="mdi-storefront-outline"
        rounded="lg"
        @click="drawer = false"
      />

      <v-list-item
        to="/member/tasks"
        title="任務活動"
        prepend-icon="mdi-clipboard-check-outline"
        rounded="lg"
        @click="drawer = false"
      />

      <v-divider class="my-2" />

      <v-list-item
        v-if="canManageForum"
        title="論壇管理"
        prepend-icon="mdi-shield-account"
        rounded="lg"
        @click="goTo('/admin/categories')"
      />

      <v-list-item
        v-if="isAdmin"
        title="後台管理"
        prepend-icon="mdi-shield-crown"
        rounded="lg"
        @click="goTo('/config/setting')"
      />
    </v-list>
  </v-navigation-drawer>

  <!--UserDetailView -->
  <UserDetailView
    v-model="userDetailDialog"
    :account="userStore.account"
    @saved="reloadPage"
  />

  <v-snackbar
    v-model="noticeOpen"
    :timeout="3000"
    location="bottom"
  >
    {{ noticeText }}

    <template #actions>
      <v-btn
        variant="text"
        @click="noticeOpen = false"
      >
        關閉
      </v-btn>
    </template>
  </v-snackbar>
</template>

<style scoped>
.header-container {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 1400px;
  height: 100%;
  margin: 0 auto;
  padding: 0 24px;
}

.brand {
  flex-shrink: 0;
  margin-right: 28px;
  color: white;
  font-size: 22px;
  font-weight: 700;
  text-decoration: none;
  cursor: pointer;
  transition:
    font-size 0.2s ease,
    margin-right 0.2s ease;
}

.main-nav {
  display: flex;
  align-items: stretch;
  height: 100%;
}

.nav-button {
  height: 72px !important;
  padding: 0 13px !important;
  font-size: 14px;
  letter-spacing: 0;
  text-transform: none;
}

.nav-button--active {
  color: white !important;
  background: #ffffff0d;
  box-shadow: inset 0 -3px #649aea;
}

.user-button {
  height: 58px !important;
  padding: 0 8px !important;
  letter-spacing: 0;
  text-transform: none;
}

.user-summary {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin: 0 10px;
  line-height: 1.5;
}

.user-name {
  max-width: 130px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: white;
  font-size: 14px;
  font-weight: 600;
}

.user-points {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #edcf85;
  font-size: 12px;
}

.profile-menu {
  width: 340px;
  max-width: calc(100vw - 24px);
  color: #243954;
}

.profile-heading {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 22px 20px 18px;
}

.profile-identity {
  min-width: 0;
}

.profile-name {
  overflow-wrap: anywhere;
  font-size: 17px;
  font-weight: 700;
}

.profile-caption {
  margin-top: 4px;
  color: #78879a;
  font-size: 11px;
}

.point-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin: 0 16px 12px;
  padding: 16px;
  border: 1px solid #ead9ae;
  border-radius: 14px;
  background: #faf3e2;
}

.point-label {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #8a621a;
  font-size: 12px;
}

.point-value {
  margin-top: 5px;
  color: #755014;
  font-size: 27px;
  font-weight: 700;
  overflow-wrap: anywhere;
}

.point-value span {
  margin-left: 6px;
  font-size: 12px;
  font-weight: 400;
}

.member-actions {
  padding: 0 10px 12px;
}

.logout-item {
  color: #b34d4d;
}

.drawer-title {
  padding: 24px;
  color: white;
  font-weight: 700;
}

/* 保留第二篇原本的縮小 Navbar 行為 */
/* .navbar-compact .brand {
  margin-right: 14px;
  font-size: 18px;
} */

.navbar-compact .nav-button {
  min-height: 36px;
}

@media (max-width: 1100px) {
  .nav-button {
    padding: 0 7px !important;
    font-size: 13px;
  }

  .brand {
    margin-right: 14px;
  }
}

@media (max-width: 599px) {
  .header-container {
    padding: 0 8px;
  }

  .brand {
    margin-right: 4px;
    font-size: 17px;
  }

  .user-summary {
    margin: 0 6px;
  }

  .user-name {
    max-width: 65px;
    font-size: 12px;
  }

  .user-points {
    font-size: 11px;
  }

  .user-button {
    padding: 0 4px !important;
  }

  .menu-chevron {
    display: none;
  }
}
</style>