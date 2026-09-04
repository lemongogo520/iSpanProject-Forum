<template>
  <v-container class="py-5">
    <v-card class="pa-5" elevation="2">
      <v-card-title class="text-h5 mb-4">
        系統管理
      </v-card-title>
      <v-card-text>
        <v-btn color="primary" variant="outlined" @click="goToConfig" class="mb-4">
          系統管理介面
        </v-btn>
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
import axios from 'axios'

const router = useRouter()

const goToConfig = () => {
  router.push('/config')
}

const logout = async () => {
  try {
    await axios.post('/logout')
    router.push('/login')
  } catch (error) {
    console.error('登出失敗:', error)
  }
}
</script>
