import { createRouter, createWebHistory } from 'vue-router'

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
  }
]
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
