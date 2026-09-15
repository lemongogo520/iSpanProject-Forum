<template>
  <v-app-bar
    elevation="2"
    color="white"
    :height="isNavbarCompact ? 48 : 64"
    :class="{ 'navbar-compact': isNavbarCompact }"
  >
    <v-container fluid class="navbar-container">
      <div class="navbar-left">
        <div class="logo" @click="goTo('/')">XX論壇</div>

        <v-menu open-on-hover :open-delay="100" :close-delay="200">
          <template #activator="{ props }">
            <v-btn v-bind="props" variant="text" @click="goTo('/categories')">
              <v-icon start>mdi-forum</v-icon>
              討論版
              <v-icon end>mdi-chevron-down</v-icon>
            </v-btn>
          </template>

          <div
            v-for="(category, index) in forumCategories"
            :key="category.id"
            class="forum-category"
            :class="`category-color-${index % 4}`"
          >
            <div class="forum-category-title">
              <div class="category-icon">
                <v-icon size="18">mdi-shape-outline</v-icon>
              </div>

              <span>{{ category.name }}</span>

              <span class="board-count">
                {{ category.boards?.length ?? 0 }}
              </span>
            </div>

            <div v-if="category.boards?.length" class="forum-board-list">
              <button
                v-for="board in category.boards"
                :key="board.id"
                type="button"
                class="forum-board-item"
                @click="goTo(`/boards/${board.id}`)"
              >
                <div class="board-icon">
                  <v-icon size="18">mdi-chat-outline</v-icon>
                </div>

                <span>{{ board.name }}</span>

                <v-icon class="forum-board-arrow" size="18">
                  mdi-arrow-right
                </v-icon>
              </button>
            </div>

            <div v-else class="forum-empty">目前沒有討論版</div>
          </div>
        </v-menu>
        <v-btn
          v-if="canManageForum"
          variant="text"
          @click="goTo('/admin/categories')"
        >
          <v-icon start>mdi-shield-account</v-icon>
          論壇管理
        </v-btn>
        <v-btn variant="text" @click="goTo('/paper-doll')">
          <v-icon start>mdi-account-star</v-icon>
          紙娃娃系統
        </v-btn>

        <v-btn variant="text" @click="goTo('/daily-task')">
          <v-icon start>mdi-calendar-check</v-icon>
          每日任務
        </v-btn>

        <v-btn v-if="isAdmin" variant="text" @click="goTo('/config/setting')">
          <v-icon start>mdi-shield-crown</v-icon>
          後台管理
        </v-btn>
      </div>

      <v-spacer />

      <v-menu>
        <template #activator="{ props }">
          <v-btn v-bind="props" variant="text" class="user-menu-btn">
            <v-avatar
              :size="isNavbarCompact ? 30 : 38"
              class="mr-2 navbar-avatar"
            >
              <v-img v-if="user.avatar" :src="user.avatar" alt="使用者頭像" />
              <v-icon v-else size="26"> mdi-account </v-icon>
            </v-avatar>

            <span class="username">
              {{ user.name || user.account }}
            </span>

            <v-icon class="ml-1"> mdi-chevron-down </v-icon>
          </v-btn>
        </template>

        <v-list min-width="180">
          <v-list-item @click="openUserDetail">
            <template #prepend>
              <v-icon>mdi-account-circle</v-icon>
            </template>

            <v-list-item-title>使用者資訊</v-list-item-title>
          </v-list-item>

          <v-list-item @click="goTo('/friends')">
            <template #prepend>
              <v-icon>mdi-account-multiple</v-icon>
            </template>
            <v-list-item-title>好友</v-list-item-title>
          </v-list-item>

          <v-divider />

          <v-list-item @click="logout">
            <template #prepend>
              <v-icon>mdi-logout</v-icon>
            </template>
            <v-list-item-title>登出</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>

      <UserDetailView
        v-model="userDetailDialog"
        :account="userStore.account"
        @saved="reloadPage"
      />
    </v-container>
  </v-app-bar>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from "vue";
import { useRouter } from "vue-router";
import httpClient from "@/services/httpClient";
import UserDetailView from "@/views/user/UserDetailView.vue";
import { useUserStore } from "@/stores/user";
import { userService } from "@/services/userService";

const forumCategories = ref([]);

const router = useRouter();
const userDetailDialog = ref(false);
const userStore = useUserStore();
const isNavbarCompact = ref(false);
let lastScrollY = 0;

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

//初始化確認使用者是否有填寫第一次資料
onMounted(async () => {
  lastScrollY = window.scrollY;
  window.addEventListener("scroll", handleWindowScroll, { passive: true });
  await Promise.all([checkUserDetail(), fetchForumMenu()]);
});

onBeforeUnmount(() => {
  window.removeEventListener("scroll", handleWindowScroll);
});

async function checkUserDetail() {
  try {
    const user = await userService.getUserDetail(userStore.account);

    console.log("目前使用者資料：", user);

    // 沒有名稱
    if (!user.name || user.name.trim() === "") {
      userDetailDialog.value = true;
    }
  } catch (error) {
    console.error("取得使用者資料失敗：", error);
  }
}

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
          })),
        ),
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

//塞畫面資料
const user = computed(() => ({
  name: userStore.name,
  account: userStore.account,
  role: userStore.permission,
}));
// 系統管理員或討論版管理員
const canManageForum = computed(() => {
  return [1, 2].includes(Number(userStore.permission));
});
//登出
const logout = () => {
  httpClient({
    method: "post",
    url: "/api/logout",
  })
    .then((response) => {
      console.log("登出成功:", response.data);
      userStore.clearUser();
      router.push("/login");
    })
    .catch((error) => {
      console.error("登出失敗:", error);
    });
};

//開啟明細設定
function openUserDetail() {
  userDetailDialog.value = true;
}

//設定管理者才有的顯示
const isAdmin = computed(() => {
  return userStore.permission === 1;
});

//前往路由
function goTo(path) {
  router.push(path);
}

//重新整理介面
function reloadPage() {
  window.location.reload();
}
</script>

<style scoped>
.navbar-container {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 24px;
  transition: padding 0.2s ease;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 4px;
}

.logo {
  font-size: 22px;
  font-weight: 700;
  margin-right: 20px;
  cursor: pointer;
  color: #1976d2;
  white-space: nowrap;
  transition:
    font-size 0.2s ease,
    margin-right 0.2s ease;
}

.user-menu-btn {
  text-transform: none;
  min-width: auto;
  padding: 4px 8px;
}

.navbar-avatar,
.username,
:deep(.v-toolbar__content),
:deep(.v-btn) {
  transition: all 0.2s ease;
}

.navbar-compact .navbar-container {
  padding-block: 0;
}

.navbar-compact .logo {
  margin-right: 12px;
  font-size: 18px;
}

.navbar-compact :deep(.v-btn) {
  min-height: 36px;
}

.navbar-compact .username {
  font-size: 14px;
}

.username {
  font-size: 15px;
  font-weight: 500;
}

.dialog-title {
  font-size: 20px;
  font-weight: 600;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px 0;
}

.user-info-text {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.user-name {
  font-size: 20px;
  font-weight: 600;
}

.user-account {
  font-size: 14px;
  color: #777;
}
.forum-dropdown {
  width: 340px;
  overflow: hidden;
  border: 1px solid #e8eaf0;
  border-radius: 16px !important;
  background: #ffffff;
}

.forum-dropdown-content {
  max-height: 460px;
  padding: 12px;
  overflow-y: auto;
  background: #f8f9fc;
}

.forum-category {
  --category-color: #5c7cfa;
  --category-light: #edf2ff;

  margin-bottom: 12px;
  padding: 10px;
  border: 1px solid color-mix(in srgb, var(--category-color) 20%, white);
  border-radius: 13px;
  background: #ffffff;
  box-shadow: 0 3px 10px rgb(31 41 55 / 5%);
}

.category-color-0 {
  --category-color: #5c7cfa;
  --category-light: #edf2ff;
}

.category-color-1 {
  --category-color: #f06595;
  --category-light: #fff0f6;
}

.category-color-2 {
  --category-color: #20c997;
  --category-light: #e6fcf5;
}

.category-color-3 {
  --category-color: #ff922b;
  --category-light: #fff4e6;
}

.forum-category-title {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 3px 4px 9px;
  color: #343a40;
  font-size: 14px;
  font-weight: 700;
}

.category-icon {
  display: flex;
  width: 30px;
  height: 30px;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  color: var(--category-color);
  background: var(--category-light);
}

.board-count {
  min-width: 25px;
  margin-left: auto;
  padding: 2px 8px;
  border-radius: 999px;
  color: var(--category-color);
  background: var(--category-light);
  font-size: 12px;
  text-align: center;
}

.forum-board-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.forum-board-item {
  display: flex;
  width: 100%;
  align-items: center;
  gap: 10px;
  padding: 9px;
  border: 0;
  border-radius: 10px;
  color: #495057;
  background: transparent;
  cursor: pointer;
  font-family: inherit;
  font-size: 14px;
  text-align: left;
  transition:
    color 0.2s ease,
    background-color 0.2s ease,
    transform 0.2s ease;
}

.forum-board-item:hover {
  color: var(--category-color);
  background: var(--category-light);
  transform: translateX(4px);
}

.board-icon {
  display: flex;
  width: 30px;
  height: 30px;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  color: var(--category-color);
  background: var(--category-light);
}

.forum-board-arrow {
  margin-left: auto;
  opacity: 0;
  transform: translateX(-5px);
  transition:
    opacity 0.2s ease,
    transform 0.2s ease;
}

.forum-board-item:hover .forum-board-arrow {
  opacity: 1;
  transform: translateX(0);
}

.forum-empty {
  padding: 8px 40px;
  color: #adb5bd;
  font-size: 13px;
}
</style>
