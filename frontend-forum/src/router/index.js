import { createRouter, createWebHistory } from "vue-router";

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
    path: "/admin/categories",
    name: "categories",
    component: () => import("../view/AdminCategoryView.vue"),
  },
  {
    path: "/categories",
    name: "userCategories",
    component: () => import("../view/HomeView.vue"),
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

export default router;
