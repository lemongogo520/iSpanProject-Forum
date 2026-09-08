import httpClient from '@/api/httpClient'

export const userService = {

  async getUserDetail(account) {

    console.log('account:', account)

    const response = await httpClient({
      method: 'get',
      url: '/api/users/detail',
      params: {
        account: account
      }
    })

    return response.data
  }
}