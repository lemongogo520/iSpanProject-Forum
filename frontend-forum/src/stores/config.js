import { defineStore } from 'pinia'
import { ref } from 'vue'
import { configService } from '@/services/configService'

export const useConfigStore = defineStore('config', () => {

  const settings = ref({})
  const loaded = ref(false)

  // 取得設定
  const loadSettings = async () => {
    if (loaded.value) {
      return Object.entries(settings.value).map(([item, value]) => ({
        item,
        value
      }))
    }

    return await refreshSettings()
  }

  // 儲存設定
  const saveSettings = async (newSettings) => {
    await configService.saveSettings(newSettings)

    // DB 儲存成功後，同步更新 Pinia
    await refreshSettings()
  }

  // 強制重新取得設定
  const refreshSettings = async () => {
    const data = await configService.getSettings()

    settings.value = Object.fromEntries(
      data.map(item => [item.item, item.value])
    )

    loaded.value = true

    return data
  }

  return {
    settings,
    loaded,
    loadSettings,
    refreshSettings,
    saveSettings
  }
})