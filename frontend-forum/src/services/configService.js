import httpClient from "@/services/httpClient";

export const configService = {

  // 取得全部設定
  async getSettings() {
    const response = await httpClient({
      method: 'get',
      url: '/api/config/settings'
    })

    console.log(response.data);
    return response.data
  },

  // 儲存全部設定
  async saveSettings(settings) {

    console.log(settings);
    const response = await httpClient({
      method: 'put',
      url: '/api/config/settings',
      data: settings
    })

    return response.data
  }
}