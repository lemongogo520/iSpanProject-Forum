import httpClient from "@/services/httpClient";

export const userService = {

  //取得使用者明細
  async getUserDetail(account) {

    const response = await httpClient({
      method: 'get',
      url: '/api/users/detail',
      params: {
        account: account
      }
    })

    return response.data
  }, 

  // 儲存使用者明細 
  async saveUserDetail(userDetail) { 

    const response = await httpClient({ 
      method: 'post', 
      url: '/api/users/detail', 
      data: userDetail }) 
      
      return response.data 
  },

  //關鍵字取得使用者集合
  async getUsers(keyword = '') {
    const response = await httpClient({
      method: 'get',
      url: '/api/users/admin/allUsers',
      params: {
        keyword
      }
    })

    console.log(response.data)
    return response.data
  },

  //建立使用者
  async createUser(user) {
    const response = await httpClient({
      method: 'post',
      url: '/api/users',
      data: user
    })

    return response.data
  },

  //
  async updateUser(uid, user) {
    const response = await httpClient({
      method: 'put',
      url: `/api/users/${uid}`,
      data: user
    })

    return response.data
  },

  async deleteUser(uid) {
    const response = await httpClient({
      method: 'delete',
      url: `/api/users/${uid}`
    })

    return response.data
  }
}