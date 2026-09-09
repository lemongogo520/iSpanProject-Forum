<template>
  <v-container class="py-5">
    <v-card class="pa-5" elevation="2">
      <!-- 標題 -->
      <v-card-title class="text-h5 mb-4">
        首頁
      </v-card-title>
      <v-card-text>
        <!-- 使用者資訊 -->
        <v-btn
          variant="text"
          @click="openUserDetail"
        >
          使用者資訊
        </v-btn>
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

const router = useRouter()
const userDetailDialog = ref(false)
const userStore = useUserStore()

//初始化確認使用者是否有填寫第一次資料
onMounted(async () => {
  await checkUserDetail()
})

async function checkUserDetail() {
  try {
    const user = await userService.getUserDetail(userStore.account)

    console.log('目前使用者資料：', user)

    // 沒有名稱
    if (!user.name || user.name.trim() === '') {
      userDetailDialog.value = true
    }

  } catch (error) {
    console.error('取得使用者資料失敗：', error)
  }
}

//前往後台設定
const goToConfig = () => {
  router.push('/config')
}

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

//開啟明細設定
function openUserDetail() {
  userDetailDialog.value = true
}

</script>
