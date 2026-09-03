import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: () => import("../view/HomeView.vue"),
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
  ],
});

export default router;
