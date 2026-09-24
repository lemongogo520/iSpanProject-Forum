import { defineStore } from 'pinia'
import { ref } from 'vue'
import { configService } from '@/services/configService'

export const useConfigStore = defineStore('config', () => {

  const settings = ref({})
  const loaded = ref(false)

  const loadSettings = async () => {
    if (loaded.value) {
      return
    }

    const data = await configService.getSettings()

    settings.value = Object.fromEntries(data.map(item => [item.item, item.value]))
    loaded.value = true
  }

  const refreshSettings = async () => {
    const data = await configService.getSettings()

    settings.value = Object.fromEntries(
      data.map(item => [
        item.item,
        item.item === 'login_logo'
          ? JSON.parse(item.value)
          : item.value
      ])
    )

    loaded.value = true
  }

  // 儲存設定
  const saveSettings = async (newSettings) => {

    await configService.saveSettings(newSettings)

    // DB 儲存成功後，同步更新 Pinia
    const data = await configService.getSettings()
    settings.value = Object.fromEntries(data.map(item => [item.item, item.value]))
    loaded.value = true
  }

  return {
    settings,
    loaded,
    loadSettings,
    refreshSettings,
    saveSettings
  }
})