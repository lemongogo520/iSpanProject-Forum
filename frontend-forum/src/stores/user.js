import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    uid: "", 
    account: '',
    name: '',
    permission: '',
    initialized: false
  }),

  //設定登入者的帳號以及身分, 1=系統管理員, 2=討論版管理員, 3=一般使用者
  actions: {
    setAccount(uid, account, name, permission) {
      this.uid = uid,
      this.account = account,
      this.name = name,
      this.permission = permission
    },

     // 新增一個獨立方法
    setUid(uid) {
      this.uid = uid ?? "";
    },
    clearUser() {
      this.uid = "";
      this.account = '',
      this.name = '',
      this.permission = ''
    },

    setInitialized(value) {
      this.initialized = value
    }
  }
})

