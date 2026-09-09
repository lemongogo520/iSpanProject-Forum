<template>
  <aside class="sidebar">

    <div class="logo">
      <RouterLink to="/home">
        回首頁
      </RouterLink>
    </div>

    <ul class="menu">

      <li>
        <RouterLink to="/config/setting">
          全站設定
        </RouterLink>
      </li>

      <li>
        <RouterLink to="/config/board">
          討論版設定
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
        <RouterLink to="/home">
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
import { useRoute, useRouter } from 'vue-router'
import httpClient from '@/api/httpClient'
import '@/assets/css/config/sidebar.css'

const route = useRoute()
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