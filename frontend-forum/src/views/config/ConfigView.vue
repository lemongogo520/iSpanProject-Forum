<template>
  <div class="layout">
    <Sidebar />

    <div class="content">
      <div class="header">
        <h1>網站設定</h1>
        <v-btn
          color="primary"
          :loading="saving"
          @click="saveSettings"
        >
          儲存設定
        </v-btn>
      </div>

      <v-tabs v-model="currentTab" class="tabs">
        <v-tab value="basic">基本設定</v-tab>
        <v-tab value="user">使用者設定</v-tab>
        <v-tab value="forum">討論區設定</v-tab>
      </v-tabs>

      <v-window v-model="currentTab" class="tab-content">
        <!-- 基本設定 -->
        <v-window-item value="basic">
          <section class="setting-section">
            <div class="setting-item">
              <label>網站名稱</label>
              <v-text-field
                v-model="basicSettings.webSiteName"
                variant="outlined"
                density="compact"
                hide-details
              />
            </div>

            <div class="setting-item">
              <label>網站描述</label>
              <v-textarea
                v-model="basicSettings.webSiteDescription"
                variant="outlined"
                density="compact"
                rows="3"
                hide-details
              />
            </div>

            <div class="setting-item">
              <label>登入頁 Logo</label>
              <v-text-field
                v-model="basicSettings.loginLogo"
                variant="outlined"
                density="compact"
                hide-details
              />
            </div>

            <div class="setting-item">
              <label>Token 有效天數</label>
              <v-select
                v-model="basicSettings.tokenActive"
                :items="[30, 60, 90]"
                suffix="天"
                variant="outlined"
                density="compact"
                hide-details
                class="number-input"
              />
            </div>
          </section>
        </v-window-item>

        <!-- 使用者設定 -->
        <v-window-item value="user">
          <section class="setting-section">
            <div class="setting-item">
              <label>開放會員註冊</label>
              <v-switch
                v-model="userSettings.allowRegister"
                color="primary"
                hide-details
              />
            </div>

            <div class="setting-item">
              <label>帳號最小長度</label>
              <v-text-field
                v-model="userSettings.accountMinLength"
                type="number"
                variant="outlined"
                density="compact"
                hide-details
                class="number-input"
              />
            </div>

            <div class="setting-item">
              <label>帳號最大長度</label>
              <v-text-field
                v-model="userSettings.accountMaxLength"
                type="number"
                variant="outlined"
                density="compact"
                hide-details
                class="number-input"
              />
            </div>

            <div class="setting-item">
              <label>密碼最小長度</label>
              <v-text-field
                v-model="userSettings.passwordMinLength"
                type="number"
                variant="outlined"
                density="compact"
                hide-details
                class="number-input"
              />
            </div>

            <div class="setting-item">
              <label>允許修改密碼</label>
              <v-switch
                v-model="userSettings.allowChangePassword"
                color="primary"
                hide-details
              />
            </div>
          </section>
        </v-window-item>

        <!-- 討論區設定 -->
        <v-window-item value="forum">
          <section class="setting-section">
            <div class="setting-item">
              <label>每頁文章數</label>
              <v-text-field
                v-model="forumSettings.postPaperPage"
                type="number"
                variant="outlined"
                density="compact"
                hide-details
                class="number-input"
              />
            </div>

            <div class="setting-item">
              <label>每頁留言數</label>
              <v-text-field
                v-model="forumSettings.commentPaperPage"
                type="number"
                variant="outlined"
                density="compact"
                hide-details
                class="number-input"
              />
            </div>

            <div class="setting-item">
              <label>允許編輯文章</label>
              <v-switch
                v-model="forumSettings.allowEditPost"
                color="primary"
                hide-details
              />
            </div>

            <div class="setting-item">
              <label>編輯文章日期限制</label>
              <v-text-field
                v-model="forumSettings.editPostDays"
                type="number"
                suffix="天"
                variant="outlined"
                density="compact"
                hide-details
                class="number-input"
              />
            </div>

            <div class="setting-item">
              <label>允許刪除文章</label>
              <v-switch
                v-model="forumSettings.allowDeletePost"
                color="primary"
                hide-details
              />
            </div>
          </section>
        </v-window-item>
      </v-window>
    </div>

    <!-- 離開確認 -->
    <v-dialog v-model="showLeaveDialog" max-width="450" persistent>
      <v-card>
        <v-card-title>尚有未儲存的修改</v-card-title>

        <v-card-text>
          目前的設定尚未儲存，確定要離開嗎？
        </v-card-text>

        <v-card-actions>
          <v-spacer />

          <v-btn @click="cancelLeave">
            取消
          </v-btn>

          <v-btn @click="discardAndLeave">
            放棄修改
          </v-btn>

          <v-btn
            color="primary"
            :loading="saving"
            @click="saveAndLeave"
          >
            儲存並離開
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref , onMounted } from 'vue'
import { onBeforeRouteLeave } from 'vue-router'
import Sidebar from '@/components/config/ConfigSidebar.vue'
import { configService } from '@/services/configService'

const currentTab = ref('basic')
const saving = ref(false)

const basicSettings = ref({
  webSiteName: '',
  webSiteDescription: '',
  loginLogo: '',
  tokenActive: null
})

const userSettings = ref({
  allowRegister: false,
  accountMinLength: null,
  accountMaxLength: null,
  passwordMinLength: null,
  allowChangePassword: false
})

const forumSettings = ref({
  postPaperPage: null,
  commentPaperPage: null,
  allowEditPost: false,
  editPostDays: null,
  allowDeletePost: false
})

const originalSettings = ref(null)

const showLeaveDialog = ref(false)
const pendingLeave = ref(null)

const getCurrentSettings = () => ({
  basic: {
    webSiteName: basicSettings.value.webSiteName,
    webSiteDescription: basicSettings.value.webSiteDescription,
    loginLogo: basicSettings.value.loginLogo,
    tokenActive: basicSettings.value.tokenActive
  },
  user: {
    allowRegister: userSettings.value.allowRegister,
    accountMinLength: userSettings.value.accountMinLength,
    accountMaxLength: userSettings.value.accountMaxLength,
    passwordMinLength: userSettings.value.passwordMinLength,
    allowChangePassword: userSettings.value.allowChangePassword
  },
  forum: {
    postPaperPage: forumSettings.value.postPaperPage,
    commentPaperPage: forumSettings.value.commentPaperPage,
    allowEditPost: forumSettings.value.allowEditPost,
    editPostDays: forumSettings.value.editPostDays,
    allowDeletePost: forumSettings.value.allowDeletePost
  }
})

const bindSettings = (settings) => {
  settings.forEach(setting => {
    switch (setting.item) {
      case 'web_site_name':
        basicSettings.value.webSiteName = setting.value
        break
      case 'web_site_description':
        basicSettings.value.webSiteDescription = setting.value
        break
      case 'login_logo':
        basicSettings.value.loginLogo = setting.value
        break
      case 'token_active':
        basicSettings.value.tokenActive = Number(setting.value)
        break

      case 'allow_register':
        userSettings.value.allowRegister = setting.value === 'true'
        break
      case 'account_min_length':
        userSettings.value.accountMinLength = Number(setting.value)
        break
      case 'account_max_length':
        userSettings.value.accountMaxLength = Number(setting.value)
        break
      case 'password_min_length':
        userSettings.value.passwordMinLength = Number(setting.value)
        break
      case 'allow_change_password':
        userSettings.value.allowChangePassword = setting.value === 'true'
        break

      case 'post_paper_page':
        forumSettings.value.postPaperPage = Number(setting.value)
        break
      case 'comment_paper_page':
        forumSettings.value.commentPaperPage = Number(setting.value)
        break
      case 'allow_edit_post':
        forumSettings.value.allowEditPost = setting.value === 'true'
        break
      case 'edit_post_days':
        forumSettings.value.editPostDays = Number(setting.value)
        break
      case 'allow_delete_post':
        forumSettings.value.allowDeletePost = setting.value === 'true'
        break
    }
  })
}

const loadSettings = async () => {
  try {
    const settings = await configService.getSettings()

    bindSettings(settings)

    originalSettings.value = getCurrentSettings()
  } catch (error) {
    console.error('取得設定失敗:', error)
  }
}

onMounted(() => {
  loadSettings()
})

const hasChanges = () => {
  if (!originalSettings.value) {
    return false
  }

  return JSON.stringify(getSettings()) !== JSON.stringify(originalSettings.value)
}

const saveSettings = async () => {
  if (saving.value) {
    return
  }

  saving.value = true

  try {
    /*
     * 這裡之後接你的 API
     *
     * const response = await configService.updateSettings(getSettings())
     *
     * 假設後端回傳儲存後的完整設定：
     * const savedSettings = response.data
     */

    const savedSettings = getSettings()

    // 重新 bind v-model
    bindSettings(savedSettings)

    // 更新「最後一次儲存」的資料
    originalSettings.value = structuredClone(savedSettings)
  } catch (error) {
    console.error('儲存設定失敗：', error)
  } finally {
    saving.value = false
  }
}

const saveAndLeave = async () => {
  await saveSettings()

  if (!hasChanges() && pendingLeave.value) {
    const next = pendingLeave.value
    pendingLeave.value = null
    showLeaveDialog.value = false
    next()
  }
}

const discardAndLeave = () => {
  if (!pendingLeave.value) {
    return
  }

  const next = pendingLeave.value

  pendingLeave.value = null
  showLeaveDialog.value = false

  next()
}

const cancelLeave = () => {
  pendingLeave.value = null
  showLeaveDialog.value = false
}

onBeforeRouteLeave((to, from, next) => {
  if (!hasChanges()) {
    next()
    return
  }

  pendingLeave.value = next
  showLeaveDialog.value = true
})
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.content {
  flex: 1;
  padding: 30px;
  max-width: 1000px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.header h1 {
  margin: 0;
}

.tabs {
  border-bottom: 1px solid #ddd;
}

.tab-content {
  padding-top: 30px;
}

.setting-section {
  margin-bottom: 35px;
}

.setting-item {
  display: flex;
  align-items: center;
  min-height: 64px;
  padding: 12px 10px;
  border-bottom: 1px solid #eeeeee;
}

.setting-item label {
  width: 220px;
  flex-shrink: 0;
  font-size: 15px;
  color: #444;
}

.setting-item :deep(.v-input) {
  max-width: 500px;
}

.setting-item .number-input {
  width: 100px !important;
  max-width: 150px !important;
}

</style>