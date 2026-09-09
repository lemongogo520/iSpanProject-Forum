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
      component: () => import("@/views/HomepageView.vue"),
  },
  {
    path: "/admin/categories",
    name: "categories",
    component: () => import("@/views/admin/AdminCategoryView.vue"),
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


  //管理者設定
  {
    path: '/config/setting',
    name: 'Config',
    component: () => import('@/views/config/ConfigView.vue')
  },
  {
    path: '/shop',
    name: 'shop',
    component: () =>
      import('../views/ShopView.vue')
  }

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
