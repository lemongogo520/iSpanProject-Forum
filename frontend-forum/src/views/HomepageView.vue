<template>
  <HomeHeader/>
  <v-container class="py-5">
    <v-card class="pa-5" elevation="2">
      <!-- 標題 -->
      <v-card-title class="text-h5 mb-4">
        首頁
      </v-card-title>
      <v-card-text>
        <br><br>
        <!-- 登出 -->
        <v-btn
          color="error"
          variant="flat"
          @click="logout"
        >
          登出
        </v-btn>
      </v-card-text>
    </v-card>
    <UserDetailView v-model="userDetailDialog" :account="userStore.account" />
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import httpClient from '@/services/httpClient'
import UserDetailView from '@/views/user/UserDetailView.vue'
import { useUserStore } from '@/stores/user'
import { userService } from '@/services/userService'
import Navbar from '@/components/home/Navbar.vue'
import HomeHeader from '@/components/home/HomeHeader.vue'

const router = useRouter()
const userDetailDialog = ref(false)
const userStore = useUserStore()

//登出
const logout = () => {
  httpClient({
    method: 'post',
    url: '/api/logout'
  })
    .then(response => {
      console.log('登出成功:', response.data)
      router.push('/login')
    })
    .catch(error => {
      console.error('登出失敗:', error)
    })
}

</script>
