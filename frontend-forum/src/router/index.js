import { createRouter, createWebHistory } from "vue-router";

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
    path: "/admin/categories",
    name: "categories",
    component: () => import("@/views/admin/AdminCategoryView.vue"),
  },
  {
    path: "/categories",
    name: "userCategories",
    component: () => import("@/views/forum/HomeView.vue"),
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
]
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router;
