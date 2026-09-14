<template>
  <v-main class="auth-page">
    <v-container fluid class="fill-height pa-0">
      <v-row no-gutters class="fill-height">
        <v-col
          cols="12"
          md="6"
          class="brand-section d-flex align-center justify-center"
        >
          <div class="brand-content text-center">
            <v-avatar size="90" class="brand-logo mb-6">
              <v-icon size="50">mdi-cpu</v-icon>
            </v-avatar>

            <h1>XX論壇</h1>
            <p class="auth-subtitle">論壇登入頁面</p>

            <div class="feature-list">
              <div class="feature-item">
                <v-icon>mdi-shield-check</v-icon>
                <span>最佳討論平台</span>
              </div>

              <div class="feature-item">
                <v-icon>mdi-database-check</v-icon>
                <span>最棒使用者體驗</span>
              </div>

              <div class="feature-item">
                <v-icon>mdi-code-square</v-icon>
                <span>無限制上傳內容</span>
              </div>
            </div>
          </div>
        </v-col>

        <v-col
          cols="12"
          md="6"
          class="login-section d-flex align-center justify-center"
        >
          <v-card class="auth-card login-card" elevation="0">
            <v-card-title class="auth-title login-title">歡迎回來</v-card-title>
            <v-card-subtitle class="auth-subtitle login-subtitle"
              >請登入您的帳號</v-card-subtitle
            >

            <v-alert
              v-if="loginError"
              type="error"
              variant="tonal"
              class="mb-5"
            >
              {{ loginError }}
            </v-alert>

            <v-form ref="loginForm" @submit.prevent="handleLogin">
              <v-text-field
                v-model="account"
                label="帳號"
                placeholder="請輸入帳號"
                prepend-inner-icon="mdi-account"
                variant="outlined"
                density="comfortable"
                :rules="[accountRule]"
                class="mb-3"
              />

              <v-text-field
                v-model="password"
                label="密碼"
                placeholder="請輸入密碼"
                prepend-inner-icon="mdi-lock"
                :type="showPassword ? 'text' : 'password'"
                :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                @click:append-inner="showPassword = !showPassword"
                variant="outlined"
                density="comfortable"
                :rules="[passwordRule]"
                class="mb-3"
              />

              <div class="remember-row">
                <v-checkbox
                  v-model="remember"
                  label="記住我"
                  density="compact"
                  hide-details
                />
                <v-btn
                  variant="text"
                  color="primary"
                  size="small"
                  @click="forgotPassword"
                >
                  忘記密碼？
                </v-btn>
              </div>

              <v-btn
                type="submit"
                color="primary"
                size="large"
                block
                rounded="lg"
                class="auth-button login-btn"
                :loading="loading"
              >
                登入
              </v-btn>
            </v-form>

            <div class="register-link">
              <span>還沒有帳號？</span>
              <v-btn variant="text" color="primary" @click="goToRegister">
                立即註冊
              </v-btn>
            </div>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-main>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import httpClient from "@/services/httpClient";
import { useAuthStore } from "@/stores/auth";
import { useUserStore } from "@/stores/user";

const authStore = useAuthStore();
const router = useRouter();
const userStore = useUserStore();

// ========================
// 表單資料
// ========================

const account = ref("");
const password = ref("");
const remember = ref(false);

const loginError = ref("");
const loading = ref(false);
const showPassword = ref(false);

const loginForm = ref(null);

// ========================
// 表單驗證
// ========================

const accountRule = (value) => {
  if (!value || value.trim() === "") {
    return "請輸入帳號";
  }

  return true;
};

const passwordRule = (value) => {
  if (!value || value.trim() === "") {
    return "請輸入密碼";
  }

  return true;
};

// ========================
// 登入
// ========================

const handleLogin = async () => {
  loginError.value = "";
  const { valid } = await loginForm.value.validate();

  if (!valid) {
    return;
  }

  loading.value = true;

  httpClient({
    method: "post",
    url: "/api/login",
    data: {
      account: account.value.trim(),
      account: account.value.trim(),
      password: password.value,
      remember: remember.value,
    },
  })
    .then((response) => {
      loginError.value = "";
      // 登入成功後前往首頁
      authStore.setLogin();
      userStore.setUid(response.data.uid);
      userStore.setAccount(account.value);
      authStore.setLogin() 
      userStore.setAccount(response.data.uid
      , response.data.account
      , response.data.name
      , response.data.permission);

      router.push("/home");
    })
    .catch((error) => {
      console.error("登入失敗:", error);

      if (error.response?.data?.message) {
        loginError.value = error.response.data.message;
      } else {
        loginError.value = "帳號或密碼錯誤";
      }
    })
    .finally(() => {
      loading.value = false;
    });
};

// ========================
// 註冊
// ========================

const goToRegister = () => {
  router.push("/register");
};

// ========================
// 忘記密碼
// ========================

const forgotPassword = () => {
  console.log("忘記密碼");
};
</script>

<style src="@/assets/css/login/login.css"></style>
