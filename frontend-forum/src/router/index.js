import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
      path: "/login",
      name: "Login",
      component: () => import("../views/login/LoginView.vue"),
  },
  {
      path: "/register",
      name: "Register",
      component: () => import("../views/login/RegisterView.vue"),
  },
  {
      path: "/home",
      name: "Homepage",
      component: () => import("../views/HomepageView.vue"),
  },
  {
    path: "/api/admin/categories",
    name: "categories",
    component: () => import("../view/AdminCategoryView.vue"),
  },
  {
    path: "/api/categories",
    name: "userCategories",
    component: () => import("../view/CategoryListView.vue"),
  },
  {
    path: "/boards/:boardId",
    name: "board",
    component: () => import("../view/BoardView.vue"),
  },
  {
    path: "/post/create",
    name: "PostCreate",
    component: () => import("../view/PostCreateView.vue"),
  },
  {
    path: "/posts/:postId/edit",
    name: "post-edit",
    component: () => import("@/view/PostCreateView.vue"),
  },
  {
    path: "/post/:postId",
    name: "PostDetail",
    component: () => import("../view/PostDetailView.vue"),
  },
]
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach(async (to) => {

  const authStore = useAuthStore()

  // 登入、註冊頁不用檢查
  if (
    to.path === '/login' ||
    to.path === '/register'
  ) {
    return true
  }

  // 其他頁面只檢查一次
  const isLogin = await authStore.checkLogin()

  // 沒登入 → 登入頁
  if (!isLogin) {
    return '/login'
  }

  // 有登入 → 正常進入
  return true
})

export default router;
