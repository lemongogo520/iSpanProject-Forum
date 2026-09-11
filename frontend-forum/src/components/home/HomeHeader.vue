<template>
  <v-app-bar elevation="2" color="white" height="64">
    <v-container fluid class="navbar-container">
      <div class="navbar-left">
        <div class="logo" @click="goTo('/')">
          XX論壇
        </div>

        <v-btn variant="text" @click="goTo('/categories')">
          <v-icon start>mdi-forum</v-icon>
          討論版
        </v-btn>

        <v-btn variant="text" @click="goTo('/paper-doll')">
          <v-icon start>mdi-account-star</v-icon>
          紙娃娃系統
        </v-btn>

        <v-btn variant="text" @click="goTo('/daily-task')">
          <v-icon start>mdi-calendar-check</v-icon>
          每日任務
        </v-btn>

        <v-btn
          v-if="isAdmin"
          variant="text"
          @click="goTo('/admin')"
        >
          <v-icon start>mdi-shield-crown</v-icon>
          後台管理
        </v-btn>
      </div>

      <v-spacer />

      <v-menu>
        <template #activator="{ props }">
          <v-btn
            v-bind="props"
            variant="text"
            class="user-menu-btn"
          >
            <v-avatar size="38" class="mr-2">
              <v-img
                v-if="user.avatar"
                :src="user.avatar"
                alt="使用者頭像"
              />
              <v-icon v-else size="26">
                mdi-account
              </v-icon>
            </v-avatar>

            <span class="username">
              {{ user.name }}
            </span>

            <v-icon class="ml-1">
              mdi-chevron-down
            </v-icon>
          </v-btn>
        </template>

        <v-list min-width="180">
            <v-list-item @click="openUserDetail">
            <UserDetailView
                v-model="userDetailDialog"
                :account="userStore.account"
            />

            <template #prepend>
                <v-icon>mdi-account-circle</v-icon>
            </template>

            <v-list-item-title>使用者資訊</v-list-item-title>
            </v-list-item>

          <v-list-item @click="goTo('/friends')">
            <template #prepend>
              <v-icon>mdi-account-multiple</v-icon>
            </template>
            <v-list-item-title>好友</v-list-item-title>
          </v-list-item>

          <v-divider />

          <v-list-item @click="logout">
            <template #prepend>
              <v-icon>mdi-logout</v-icon>
            </template>
            <v-list-item-title>登出</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-container>
  </v-app-bar>

  <v-dialog v-model="userDialog" max-width="500">
    <v-card>
      <v-card-title class="dialog-title">
        使用者資訊
      </v-card-title>

      <v-card-text>
        <div class="user-info">
          <v-avatar size="90">
            <v-img
              v-if="user.avatar"
              :src="user.avatar"
              alt="使用者頭像"
            />
            <v-icon v-else size="55">
              mdi-account
            </v-icon>
          </v-avatar>

          <div class="user-info-text">
            <div class="user-name">
              {{ user.name }}
            </div>

            <div class="user-account">
              {{ user.account }}
            </div>
          </div>
        </div>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          variant="text"
          @click="userDialog = false"
        >
          關閉
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
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

  console.log(userStore.account)
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

//塞畫面資料
const user = computed(() => ({
  name: userStore.account,
  account: userStore.account,
  role: userStore.permission
}))

//登出
const logout = () => {
  httpClient({
    method: 'post',
    url: '/api/logout'
  })
    .then(response => {
      console.log('登出成功:', response.data)
      userStore.clearUser();
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

const userDialog = ref(false)

const isAdmin = computed(() => {
  return userStore.permission === 1
})

function goTo(path) {
  router.push(path)
}

</script>

<style scoped>
.navbar-container {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 24px;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 4px;
}

.logo {
  font-size: 22px;
  font-weight: 700;
  margin-right: 20px;
  cursor: pointer;
  color: #1976d2;
  white-space: nowrap;
}

.user-menu-btn {
  text-transform: none;
  min-width: auto;
  padding: 4px 8px;
}

.username {
  font-size: 15px;
  font-weight: 500;
}

.dialog-title {
  font-size: 20px;
  font-weight: 600;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px 0;
}

.user-info-text {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.user-name {
  font-size: 20px;
  font-weight: 600;
}

.user-account {
  font-size: 14px;
  color: #777;
}
</style>
