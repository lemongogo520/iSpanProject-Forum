<template>
  <v-main class="auth-page">
    <v-container fluid class="register-container">
      <v-row class="fill-height" align="center" justify="center">
        <v-col cols="12" sm="8" md="5" lg="4">
          <v-card class="auth-card register-card" elevation="8" rounded="lg">
            <v-card-text class="pa-8">
              <!-- 標題 -->
              <div class="text-center mb-6">
                <h2 class="auth-title text-h4 font-weight-bold">
                  建立帳號
                </h2>
                <p class="auth-subtitle text-body-2 mt-2">
                  請填寫以下資料完成註冊
                </p>
              </div>

              <!-- 錯誤訊息 -->
              <v-alert
                v-if="registerError"
                type="error"
                variant="tonal"
                density="comfortable"
                class="mb-5"
              >
                {{ registerError }}
              </v-alert>

              <!-- 註冊表單 -->
              <v-form ref="registerForm" @submit.prevent="register">
                <!-- 帳號 -->
                <v-text-field
                  v-model="account"
                  label="帳號"
                  placeholder="請輸入帳號"
                  prepend-inner-icon="mdi-account"
                  variant="outlined"
                  maxlength="20"
                  :rules="accountRules"
                  @input="clearError"
                />

                <!-- 密碼 -->
                <v-text-field
                  v-model="password"
                  label="密碼"
                  placeholder="請輸入密碼"
                  prepend-inner-icon="mdi-lock"
                  :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                  :type="showPassword ? 'text' : 'password'"
                  variant="outlined"
                  maxlength="20"
                  :rules="passwordRules"
                  class="mt-2"
                  @click:append-inner="showPassword = !showPassword"
                  @input="clearError"
                />

                <!-- 確認密碼 -->
                <v-text-field
                  v-model="confirmPassword"
                  label="確認密碼"
                  placeholder="請再次輸入密碼"
                  prepend-inner-icon="mdi-lock-check"
                  :append-inner-icon="showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  variant="outlined"
                  maxlength="20"
                  :rules="confirmPasswordRules"
                  class="mt-2"
                  @click:append-inner="showConfirmPassword = !showConfirmPassword"
                  @input="clearError"
                />

                <!-- 註冊按鈕 -->
                <v-btn
                  type="submit"
                  color="primary"
                  size="large"
                  block
                  class="auth-button mt-4"
                  :loading="loading"
                >
                  註冊
                </v-btn>
              </v-form>

              <!-- 返回登入 -->
              <div class="text-center mt-6">
                <span class="text-body-2">
                  已經有帳號？
                </span>
                <v-btn
                  variant="text"
                  color="primary"
                  class="text-none"
                  @click="goToLogin"
                >
                  返回登入
                </v-btn>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-main>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import httpClient from '@/services/httpClient'
import { useConfigStore } from '@/stores/config'

const router = useRouter()
const configStore = useConfigStore();

// =========================
// 表單資料
// =========================

const account = ref('')
const password = ref('')
const confirmPassword = ref('')

// =========================
// UI 狀態
// =========================

const showPassword = ref(false)
const showConfirmPassword = ref(false)
const registerError = ref('')
const loading = ref(false)
const registerForm = ref(null)

// =========================
// 帳號驗證
// =========================

const accountRules = [
  value => {
    if (!value || !value.trim()) {
      return '請輸入帳號'
    }

    const min = configStore.settings.account_min_length
    const max = configStore.settings.account_max_length

    if (value.length < min || value.length > max) {
      return `帳號長度必須介於 ${min}~${max} 個字元`
    }

    if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z0-9_]+$/.test(value)) {
      return '帳號只能使用英文、數字、底線，並且包含英文和數字'
    }

    return true
  }
]

// =========================
// 密碼驗證
// =========================

const passwordRules = [
  value => {
    if (!value || !value.trim()) {
      return '請輸入密碼'
    }


    const min = configStore.settings.password_min_length

    if (value.length < min) {
      return `密碼長度必須至少 ${min} 個字元`
    }

    if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z0-9_]+$/.test(value)) {
      return '密碼只能使用英文、數字、底線，並且包含英文或數字'
    }

    return true
  }
]

// =========================
// 確認密碼驗證
// =========================

const confirmPasswordRules = [
  value => {
    if (!value || !value.trim()) {
      return '請再次輸入密碼'
    }

    if (value !== password.value) {
      return '兩次輸入的密碼不一致'
    }

    return true
  }
]

// =========================
// 清除錯誤訊息
// =========================

function clearError() {
  registerError.value = ''
}

// =========================
// 註冊
// =========================
async function register() {

  registerError.value = ''

  const { valid } = await registerForm.value.validate()

  if (!valid) {
    return
  }

  loading.value = true

  httpClient({
    method: 'post',
    url: '/api/register',
    data: {
      account: account.value.trim(),
      password: password.value,
      confirmPassword: confirmPassword.value
    }
  })
    .then(response => {

      console.log('註冊成功:', response.data)

      registerError.value = ''

      // 註冊成功後前往登入頁
      router.push('/login')

    })
    .catch(error => {

      console.error('註冊失敗:', error)

      if (error.response?.data?.message) {
        registerError.value = error.response.data.message
      } else {
        registerError.value = '註冊失敗，請稍後再試'
      }

    })
    .finally(() => {

      loading.value = false

    })
}

// =========================
// 返回登入
// =========================

function goToLogin() {
  router.push('/login')
}
</script>

<style src="@/assets/css/login/register.css"></style>