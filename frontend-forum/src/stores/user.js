import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    account: ''
  }),

  actions: {
    setAccount(account) {
      this.account = account
    },

    clearUser() {
      this.account = ''
    }
  }
})