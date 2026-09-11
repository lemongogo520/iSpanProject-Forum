<script setup>
import { ref } from "vue";
import { useDisplay } from "vuetify";

const drawer = ref(false);
const { mdAndUp } = useDisplay();

const navItems = [
  { title: "首頁", icon: "mdi-home-outline", to: "/" },
  { title: "討論區", icon: "mdi-forum-outline", to: "/forum" },
  { title: "點數", icon: "mdi-star-circle-outline", to: "/coin" },
  { title: "我的角色", icon: "mdi-account-circle-outline", to: "/avatar" },
  { title: "商城", icon: "mdi-storefront-outline", to: "/shop" },
  { title: "任務活動", icon: "mdi-clipboard-check-outline", to: "/tasks" },
];
</script>

<template>
  <v-app-bar class="top-header" color="#0f1f35" height="72" elevation="2">
    <v-container class="header-container d-flex align-center" max-width="1200">
      <v-app-bar-nav-icon v-if="!mdAndUp" color="white" aria-label="開啟導覽選單" @click="drawer = !drawer" />

      <RouterLink to="/" class="brand">iSpanProject</RouterLink>

      <nav v-if="mdAndUp" class="main-nav">
        <v-btn
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          :class="{ 'nav-button--active': $route.path === item.to }"
          class="nav-button"
          color="#c8d1de"
          variant="text"
          rounded="0"
        >
          {{ item.title }}
        </v-btn>
      </nav>

      <v-spacer />

      <div class="user-area">
        <v-avatar color="primary" size="34">
          <span class="avatar-text">A</span>
        </v-avatar>

        <span v-if="mdAndUp">哈囉，Admin</span>
      </div>
    </v-container>
  </v-app-bar>

  <v-navigation-drawer v-model="drawer" color="#0f1f35" location="left" temporary width="280">
    <div class="drawer-user">
      <v-avatar color="primary" size="42">
        <span class="avatar-text">A</span>
      </v-avatar>

      <div>
        <div class="drawer-user-name">Admin</div>
        <div class="drawer-user-caption">歡迎回來</div>
      </div>
    </div>

    <v-divider color="#56677c" />

    <v-list nav density="comfortable">
      <v-list-item
        v-for="item in navItems"
        :key="item.to"
        :title="item.title"
        :prepend-icon="item.icon"
        :to="item.to"
        color="primary"
        rounded="lg"
        @click="drawer = false"
      />
    </v-list>
  </v-navigation-drawer>
</template>

<style scoped>
.top-header {
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  width: 100%;
  height: 100%;
  padding: 0 24px;
}

.brand {
  flex-shrink: 0;
  margin-right: 30px;
  color: white;
  font-size: 22px;
  font-weight: 700;
  text-decoration: none;
}

.main-nav {
  display: flex;
  align-items: stretch;
  height: 100%;
}

.nav-button {
  position: relative;
  height: 72px !important;
  padding: 0 14px !important;
  font-size: 15px;
  letter-spacing: 0;
  text-transform: none;
}

.nav-button:hover {
  color: white !important;
}

.nav-button--active {
  color: white !important;
}

.nav-button--active::after {
  position: absolute;
  right: 14px;
  bottom: 0;
  left: 14px;
  height: 3px;
  background: #3b82f6;
  content: "";
}

.user-area {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #e5ebf3;
  font-size: 14px;
  white-space: nowrap;
}

.avatar-text {
  color: white;
  font-weight: 700;
}

.drawer-user {
  display: flex;
  align-items: center;
  gap: 13px;
  padding: 24px 20px;
}

.drawer-user-name {
  color: white;
  font-size: 16px;
  font-weight: 700;
}

.drawer-user-caption {
  margin-top: 2px;
  color: #9eacbd;
  font-size: 13px;
}

@media (max-width: 959px) {
  .header-container {
    padding: 0 12px;
  }

  .brand {
    margin-right: 0;
    font-size: 20px;
  }
}
</style>