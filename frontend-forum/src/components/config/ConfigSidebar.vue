<template>
  <aside class="config-sidebar">
    <div class="logo">
      <RouterLink to="/home">
        回首頁
      </RouterLink>
    </div>

    <ul class="menu">
      <li>
      <RouterLink to="/config/forum-template">
        論壇初始化
      </RouterLink>
      </li>
      <li>
        <RouterLink to="/config/setting">
          全站設定
        </RouterLink>
      </li>
      <li>
        <RouterLink to="/config/users">
          使用者管理
        </RouterLink>
      </li>
      <li>
        <RouterLink to="/config/permissions">
          權限管理
        </RouterLink>
      </li>
      <li>
        <RouterLink to="/config/doll">
          紙娃娃設定
        </RouterLink>
      </li>
      <li>
        <a href="#" @click.prevent="logout">
          登出
        </a>
      </li>
    </ul>
  </aside>
</template>

<script setup>
import { useRouter } from 'vue-router'
import httpClient from '@/services/httpClient'

const router = useRouter()

async function logout() {
  try {
    await httpClient({
      method: 'post',
      url: '/logout'
    })
    router.push('/login')
  } catch (error) {
    console.error('登出失敗:', error)
  }
}
</script>

<style scoped>
.config-sidebar {
  width: 240px;
  min-height: 100vh;
  background: #1f2937;
  color: #fff;
}

.config-sidebar * {
  box-sizing: border-box;
  font-family: "Segoe UI", "Microsoft JhengHei", sans-serif;
}

.config-sidebar a {
  text-decoration: none;
  color: inherit;
}

.logo {
  background: #f3f4f6;
}

.logo a {
  display: block;
  width: 100%;
  padding: 20px 24px;
  color: #374151;
  font-size: 20px;
  font-weight: bold;
  text-align: left;
}

.logo a:hover {
  background: #e5e7eb;
}

.menu {
  list-style: none;
  margin: 0;
  padding: 0;
}

.menu li a {
  display: block;
  padding: 16px 24px;
  transition: .2s;
}

.menu li a:hover {
  background: #374151;
}

.menu li a.router-link-exact-active {
  background: #2563eb;
}
</style>