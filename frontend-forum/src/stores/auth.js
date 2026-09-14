import { defineStore } from 'pinia'
import { ref } from 'vue'
import httpClient from '@/services/httpClient'
import { useUserStore } from '@/stores/user'

export const useAuthStore = defineStore('auth', () => {

  const isLogin = ref(false)
  const checked = ref(false)

  const checkLogin = async () => {

    if (checked.value) {
      return isLogin.value
    }

    const userStore = useUserStore()

    try {
      const response = await httpClient({
        method: 'get',
        url: '/api/auth/me'
      })

      userStore.setUid(response.data.uid);
      
      userStore.setAccount(
        response.data.account,
        response.data.name,
        response.data.permission
      )

      isLogin.value = true

    } catch (error) {

      userStore.clearUser()
      isLogin.value = false

    } finally {
      checked.value = true
    }

    return isLogin.value
  }

  const setLogin = () => {
    isLogin.value = true
    checked.value = true
  }

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