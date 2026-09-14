import httpClient from "@/services/httpClient";

export const userService = {

  //取得使用者明細
  async getUserDetail(account) {

    console.log('account:', account)

    const response = await httpClient({
      method: 'get',
      url: '/api/users/detail',
      params: {
        account: account
      }
    })

    console.log(response.data);

    return response.data
  }, 

  // 儲存使用者明細 
  async saveUserDetail(userDetail) { 

    console.log(userDetail);
    const response = await httpClient({ 
      method: 'post', 
      url: '/api/users/detail', 
      data: userDetail }) 
      return response.data 
  }
}