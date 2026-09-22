import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { useUserStore } from "@/stores/user";

const routes = [
  {
    path: "/",
    name: "home",
    component: () => import("@/views/HomeView.vue"),
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/login/LoginView.vue"),
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/login/RegisterView.vue"),
  },
  {
    path: "/home",
    name: "Homepage",
    component: () => import("@/views/HomeView.vue"),
  },
  {
    path: "/admin/categories",
    name: "ForumAdminView",
    component: () => import("@/views/admin/ForumAdminView.vue"),
    meta: {
      requiresForumManager: true,
    },
  },
  {
    path: "/categories",
    name: "userCategories",
    component: () => import("@/views/forum/CategoryListView.vue"),
  },
  {
    path: "/boards/:boardId",
    name: "board",
    component: () => import("@/views/forum/BoardView.vue"),
  },
  {
    path: "/post/create",
    name: "PostCreate",
    component: () => import("@/views/post/PostCreateView.vue"),
  },
  {
    path: "/posts/:postId/edit",
    name: "post-edit",
    component: () => import("@/views/post/PostCreateView.vue"),
  },
  {
    path: "/post/:postId",
    name: "PostDetail",
    component: () => import("@/views/post/PostDetailView.vue"),
  },
  {
    path: "/doll/categories",
    name: "dollCategories",
    component: () => import("@/views/doll/DollCategoriesView.vue"),
  },
  {
    path: "/doll/user",
    name: "dolluser",
    component: () => import("@/views/doll/DollUserItemsView.vue"),
  },
  {
    path: "/friends",
    name: "Friend",
    component: () => import("@/views/friend/FriendView.vue"),
  },



  //管理者設定
  {
    path: "/config/setting",
    name: "ConfigSetting",
    component: () => import("@/views/config/ConfigView.vue"),
  },
  {
    path: "/config/users",
    name: "ConfigUsers",
    component: () => import("@/views/config/ConfigUsersView.vue"),
  },
  {
    path: "/config/permissions",
    name: "ConfigPermissions",
    component: () => import("@/views/config/ConfigPermissionsView.vue"),
  },
  {
    path: "/config/doll",
    name: "ConfigDoll",
    component: () => import("@/views/doll/DollCategoriesView.vue"),
  },
  {
    path: "/config/doll/newitem",
    name: "dollNewItem",
    component: () => import("@/views/doll/DollNewItemsView.vue"),
  },
  {
    path: "/config/doll/adminitems",
    name: "dollAdminItems",
    component: () => import("@/views/doll/DollAdminItemsView.vue"),
  },
  {
    path: "/config/forum-template",
    name: "ForumTemplate",
    component: () => import("@/views/config/ForumTemplateView.vue"),
    meta: {
      requiresSystemAdmin: true,
    },
  },

  //商城
  {
    path: "/shop",
    name: "shop",
    component: () => import("@/views/ShopView.vue"),
  },
  {
    path: "/member/tasks",
    name: "DailyTasks",
    component: () => import("@/views/coin/DailyTasksView.vue"),
  },
  {
    path: "/member/transactions",
    name: "CoinTransactions",
    component: () => import("@/views/coin/CoinTransactionsView.vue"),
  },
];
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

router.beforeEach(async (to) => {
  const authStore = useAuthStore();
  const userStore = useUserStore();

  // 登入、註冊頁不用檢查
  if (to.path === "/login" || to.path === "/register") {
    return true;
  }
  // 其他頁面只檢查一次
  const isLogin = await authStore.checkLogin();

  // 沒登入 → 登入頁
  if (!isLogin) {
    return "/login";
  }
  if (
    to.meta.requiresForumManager &&
    ![1, 2].includes(Number(userStore.permission))
  ) {
    return "/home";
  }
   // 系統管理員 判斷
  if (to.meta.requiresSystemAdmin && Number(userStore.permission) !== 1) {
    return "/home";
  }

  // 有登入 → 正常進入
  return true;
});

export default router;
