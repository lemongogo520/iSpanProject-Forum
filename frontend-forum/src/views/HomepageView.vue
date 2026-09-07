<template>
  <v-container class="py-5">
    <v-card class="pa-5" elevation="2">
      <v-card-title class="text-h5 mb-4">
        首頁
      </v-card-title>
      <v-card-text>
        <br>
        <v-btn color="error" variant="flat" @click="logout">
          登出
        </v-btn>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import httpClient from '@/api/httpClient'

const router = useRouter()

const goToConfig = () => {
  router.push('/config')
}

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
