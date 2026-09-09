<template>

  <!-- User Detail Dialog -->
  <v-dialog
    v-model="dialog"
    max-width="650"
    :persistent="!nameExistsInDatabase"
  >
    <v-card>

      <!-- 標題 -->
      <v-card-title class="text-h5">
        使用者資訊
      </v-card-title>

      <v-divider />

      <v-card-text>
        <v-form ref="formRef">

            <!-- 帳號 -->
            <div class="field-row">
                <div class="field-label">
                    帳號
                </div>

                <div class="field-content">
                    <v-text-field
                    v-model="form.account"
                    disabled
                    hide-details
                    />

                    <v-btn
                    variant="outlined"
                    class="ml-2"
                    @click="changePassword"
                    >
                    更改密碼
                    </v-btn>
                </div>
                </div>

                <!-- 名稱 -->
                <div class="field-row">
                <div class="field-label">
                    名稱
                </div>

                <div class="field-content">
                    <v-text-field
                    v-model="form.name"
                    label="名稱"
                    :rules="nameRules"
                    @update:model-value="markDirty"
                    />
                </div>
                </div>

                <!-- 暱稱 -->
                <div class="field-row">
                <div class="field-label">
                    暱稱
                </div>

                <div class="field-content">
                    <v-text-field
                    v-model="form.nickname"
                    label="暱稱"
                    :rules="nicknameRules"
                    @update:model-value="markDirty"
                    />

                    <v-checkbox
                    v-model="form.showNickname"
                    label="站內以暱稱為顯示名稱"
                    hide-details
                    @update:model-value="markDirty"
                    />
                </div>
                </div>

                <!-- 生日 -->
                <div class="field-row">
                <div class="field-label">
                    生日
                </div>

                <div class="field-content">
                    <v-text-field
                    v-model="form.birthday"
                    type="date"
                    label="生日"
                    :rules="birthdayRules"
                    @update:model-value="markDirty"
                    />

                    <v-checkbox
                    v-model="form.showBirthday"
                    label="他人檢視是否顯示"
                    hide-details
                    @update:model-value="markDirty"
                    />
                </div>
            </div>

          <!-- Email -->
            <div class="field-row">
                <div class="field-label">
                    Email
                </div>

                <div class="field-content">
                    <v-text-field
                    v-model="form.email"
                    label="Email"
                    type="email"
                    :rules="emailRules"
                    @update:model-value="markDirty"
                    />

                    <v-checkbox
                    v-model="form.showEmail"
                    label="他人檢視是否顯示"
                    hide-details
                    @update:model-value="markDirty"
                    />
                </div>
            </div>
          <!-- 照片 -->
            <div class="field-row">
                <div class="field-label">
                照片
                </div>

                <div class="field-content photo-area">

                <v-img
                    v-if="form.photoUrl"
                    :src="form.photoUrl"
                    width="150"
                    height="150"
                    cover
                    class="rounded"
                />

                <div
                    v-else
                    class="no-photo"
                >
                    尚無照片
                </div>

                </div>
            </div>
        </v-form>
      </v-card-text>

      <v-divider />

      <!-- 按鈕 -->
      <v-card-actions class="dialog-actions">

        <v-btn
          variant="outlined"
          @click="save"
        >
          儲存
        </v-btn>

        <v-btn
          variant="flat"
          color="primary"
          @click="saveAndClose"
        >
          儲存並關閉
        </v-btn>

        <v-spacer />

        <v-btn
          variant="text"
          @click="requestClose"
        >
          關閉
        </v-btn>

      </v-card-actions>

    </v-card>
  </v-dialog>

  <!-- 尚未儲存確認 Dialog -->
  <v-dialog
    v-model="confirmCloseDialog"
    max-width="450"
  >
    <v-card>

      <v-card-title>
        尚未儲存
      </v-card-title>

      <v-card-text>
        目前有尚未儲存的修改，確定要關閉嗎？
      </v-card-text>

      <v-card-actions>

        <v-spacer />

        <v-btn
          variant="text"
          @click="confirmCloseDialog = false"
        >
          取消
        </v-btn>

        <v-btn
          color="error"
          variant="flat"
          @click="confirmClose"
        >
          確定關閉
        </v-btn>

      </v-card-actions>

    </v-card>
  </v-dialog>

</template>

<script setup>

import { ref, reactive, computed, watch } from 'vue'
import httpClient from '@/services/httpClient'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { userService } from '@/services/userService'

const router = useRouter()
const userStore = useUserStore()
const nameExistsInDatabase = ref(false)

/*
|--------------------------------------------------------------------------
| Props / Emits
|--------------------------------------------------------------------------
*/
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  account: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['update:modelValue'])

watch(
  () => props.modelValue,
  (value) => {

    if (value) {
      openDialog()
    }

  }
)


/*
|--------------------------------------------------------------------------
| Dialog
|--------------------------------------------------------------------------
*/

const dialog = computed({
  get() {
    return props.modelValue
  },

  set(value) {
    emit('update:modelValue', value)
  }
})

function closeDialog() {
  if (!nameExistsInDatabase.value) {
    return
  }

  dialog.value = false
}

const confirmCloseDialog = ref(false)


/*
|--------------------------------------------------------------------------
| Form
|--------------------------------------------------------------------------
*/

const formRef = ref(null)

const form = reactive({
  account: '',
  name: '',
  nickname: '',
  showNickname: false,

  birthday: '',
  showBirthday: false,

  email: '',
  showEmail: false,

  photoUrl: ''
})


/*
|--------------------------------------------------------------------------
| 原始資料
|--------------------------------------------------------------------------
|
| 用來判斷使用者是否修改過資料
|
*/

const originalForm = ref(null)

const isDirty = ref(false)


/*
|--------------------------------------------------------------------------
| Validation Rules
|--------------------------------------------------------------------------
*/

/*
 * 名稱
 * 必填
 */
const nameRules = [
  value => {
    if (!value || !value.trim()) {
      return '名稱不能為空'
    }

    return true
  }
]


/*
 * 暱稱
 * 可以為空
 */
const nicknameRules = [
  value => {
    return true
  }
]


/*
 * 生日
 * 可以為空
 */
const birthdayRules = [
  value => {
    return true
  }
]


/*
 * Email
 * 可以為空
 * 有輸入時才檢查格式
 */
const emailRules = [
  value => {

    // Email 可以為空
    if (!value) {
      return true
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

    return emailRegex.test(value) || 'Email 格式不正確'
  }
]


/*
|--------------------------------------------------------------------------
| 開啟 Dialog
|--------------------------------------------------------------------------
*/

async function openDialog() {
  try {
    const user = await userService.getUserDetail(userStore.account)

    console.log('取得使用者資料：', user)

    Object.assign(form, {
      account: user.account ?? '',
      name: user.name ?? '',
      nickname: user.nickname ?? '',
      showNickname: user.showNickname ?? false,

      birthday: user.birthday ?? '',
      showBirthday: user.showBirthday ?? false,

      email: user.email ?? '',
      showEmail: user.showEmail ?? false,

      photoUrl: user.photoUrl ?? ''
    })

    originalForm.value = JSON.parse(
      JSON.stringify(form)
    )

    isDirty.value = false
    dialog.value = true
    nameExistsInDatabase.value = !!user.name?.trim()

  } catch (error) {
    console.error('取得使用者資料失敗：', error)
    router.push('/login')
  }
}


/*
|--------------------------------------------------------------------------
| 標記資料已修改
|--------------------------------------------------------------------------
*/

function markDirty() {
  isDirty.value = true
}


/*
|--------------------------------------------------------------------------
| 驗證表單
|--------------------------------------------------------------------------
*/

async function validateForm() {

  if (!formRef.value) {
    return false
  }

  const result = await formRef.value.validate()

  return result.valid
}


/*
|--------------------------------------------------------------------------
| 儲存
|--------------------------------------------------------------------------
*/

async function save() {

  /*
   * 先驗證
   */
  const valid = await validateForm()

  if (!valid) {
    return
  }

  await userService.saveUserDetail(form)

  /*
   * 更新原始資料
   */
  originalForm.value = JSON.parse(
    JSON.stringify(form)
  )

  /*
   * 已經沒有未儲存修改
   */
  isDirty.value = false
}


/*
|--------------------------------------------------------------------------
| 儲存並關閉
|--------------------------------------------------------------------------
*/

async function saveAndClose() {

  /*
   * 先驗證
   */
  const valid = await validateForm()

  if (!valid) {
    return
  }

  await userService.saveUserDetail(form)

  /*
   * 儲存關閉不需要更新原始資料
   */
  // originalForm.value = JSON.parse(
  //   JSON.stringify(form)
  // )

  isDirty.value = false

  /*
   * 關閉 Dialog
   */
  dialog.value = false
}


/*
|--------------------------------------------------------------------------
| 關閉 Dialog
|--------------------------------------------------------------------------
*/

async function requestClose() {

  /*
   * 沒有修改
   * → 直接關閉
   */
  if (!isDirty.value) {

    dialog.value = false

    return
  }


  /*
   * 有修改
   * → 先驗證資料
   */
  const valid = await validateForm()


  /*
   * 驗證失敗
   * → 不關閉
   */
  if (!valid) {
    return
  }


  /*
   * 驗證成功
   * → 詢問是否放棄修改
   */
  confirmCloseDialog.value = true
}


/*
|--------------------------------------------------------------------------
| 確定關閉
|--------------------------------------------------------------------------
*/

function confirmClose() {

  /*
   * 關閉確認 Dialog
   */
  confirmCloseDialog.value = false


  /*
   * 還原資料
   */
  if (originalForm.value) {

    Object.assign(
      form,
      JSON.parse(
        JSON.stringify(originalForm.value)
      )
    )
  }


  /*
   * 清除修改狀態
   */
  isDirty.value = false


  /*
   * 關閉主要 Dialog
   */
  dialog.value = false
}


/*
|--------------------------------------------------------------------------
| 更改密碼
|--------------------------------------------------------------------------
*/

function changePassword() {

  /*
   * TODO:
   *
   * 之後可以：
   *
   * 1. 開另一個 ChangePasswordDialog
   * 2. 或 router.push('/change-password')
   */

  console.log('開啟更改密碼')
}

</script>


<style scoped>

.field-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 18px;
}

.field-label {
  width: 80px;
  padding-top: 16px;
  font-weight: 500;
}

.field-content {
  flex: 1;
  display: flex;
  align-items: center;
}

.photo-area {
  min-height: 150px;
}

.no-photo {
  width: 150px;
  height: 150px;

  display: flex;
  align-items: center;
  justify-content: center;

  background: #f5f5f5;
  border: 1px solid #ddd;
}

.dialog-actions {
  padding: 16px;
}

</style>
