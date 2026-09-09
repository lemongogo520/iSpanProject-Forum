import { defineStore } from 'pinia'
import { ref } from 'vue'
import httpClient from '@/services/httpClient'

export const useAuthStore = defineStore('auth', () => {

  const isLogin = ref(false)

  // 是否已經確認過登入狀態
  const checked = ref(false)

  const checkLogin = async () => {

    // 已經確認過就不要再檢查
    if (checked.value) {
      return isLogin.value
    }

    try {

      await httpClient({
        method: 'get',
        url: '/api/auth/me'
      })

      isLogin.value = true

    } catch (error) {

      isLogin.value = false

    } finally {

      checked.value = true

    }

    return isLogin.value
  }

    // 登入成功後使用
    const setLogin = () => {
        isLogin.value = true
        checked.value = true
    }

    // 登出成功後使用
    const setLogout = () => {
        isLogin.value = false
        checked.value = true
    }

  return {
    isLogin,
    checked,
    checkLogin,
    setLogout,
    setLogin
  }
})