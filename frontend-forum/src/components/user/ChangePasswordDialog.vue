<template>
  <v-dialog
    :model-value="modelValue"
    max-width="450"
    persistent
    @update:model-value="emit('update:modelValue', $event)"
  >
    <v-card>
      <v-card-title>更改密碼</v-card-title>

      <v-card-text>
        <v-text-field
          v-model="oldPassword"
          label="舊密碼"
          :type="showOldPassword ? 'text' : 'password'"
          :error-messages="oldPasswordError"
          autocomplete="current-password"
          :append-inner-icon="showOldPassword ? 'mdi-eye-off' : 'mdi-eye'"
          @click:append-inner="showOldPassword = !showOldPassword"
        />

        <v-text-field
          v-model="newPassword"
          label="新密碼"
          :type="showNewPassword ? 'text' : 'password'"
          :error-messages="newPasswordError"
          autocomplete="new-password"
          :append-inner-icon="showNewPassword ? 'mdi-eye-off' : 'mdi-eye'"
          @click:append-inner="showNewPassword = !showNewPassword"
        />

        <v-text-field
          v-model="confirmPassword"
          label="確認新密碼"
          :type="showConfirmPassword ? 'text' : 'password'"
          :error-messages="confirmPasswordError"
          autocomplete="new-password"
          :append-inner-icon="showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'"
          @click:append-inner="showConfirmPassword = !showConfirmPassword"
        />
      </v-card-text>

      <v-card-actions>
        <v-spacer />

        <v-btn
          variant="text"
          :disabled="loading"
          @click="close"
        >
          取消
        </v-btn>

        <v-btn
          color="primary"
          :loading="loading"
          @click="changePassword"
        >
          確認
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import httpClient from '@/services/httpClient'

const props = defineProps({
  modelValue: Boolean
})

const emit = defineEmits(['update:modelValue', 'success'])

const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const showOldPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const loading = ref(false)

const oldPasswordError = ref('')
const newPasswordError = ref('')
const confirmPasswordError = ref('')

const resetForm = () => {
  oldPassword.value = ''
  newPassword.value = ''
  confirmPassword.value = ''

  oldPasswordError.value = ''
  newPasswordError.value = ''
  confirmPasswordError.value = ''

  showOldPassword.value = false
  showNewPassword.value = false
  showConfirmPassword.value = false
}

watch(() => props.modelValue, (value) => {
  if (value) {
    resetForm()
  }
})

const close = () => {
  emit('update:modelValue', false)
}

const validate = () => {
  oldPasswordError.value = ''
  newPasswordError.value = ''
  confirmPasswordError.value = ''

  let valid = true

  if (!oldPassword.value) {
    oldPasswordError.value = '請輸入舊密碼'
    valid = false
  }

  if (!newPassword.value) {
    newPasswordError.value = '請輸入新密碼'
    valid = false
  } else if (newPassword.value.length < 6 || newPassword.value.length > 20) {
    newPasswordError.value = '密碼長度需為 6～20 個字元'
    valid = false
  } else if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]+$/.test(newPassword.value)) {
    newPasswordError.value = '密碼需包含英文及數字'
    valid = false
  }

  if (!confirmPassword.value) {
    confirmPasswordError.value = '請確認新密碼'
    valid = false
  } else if (newPassword.value !== confirmPassword.value) {
    confirmPasswordError.value = '兩次輸入的新密碼不一致'
    valid = false
  }

  if (oldPassword.value && newPassword.value === oldPassword.value) {
    newPasswordError.value = '新密碼不能與舊密碼相同'
    valid = false
  }

  return valid
}

const changePassword = async () => {
  if (!validate()) {
    return
  }

  loading.value = true

  try {
    await httpClient({
      method: 'post',
      url: '/api/users/change-password',
      data: {
        oldPassword: oldPassword.value,
        newPassword: newPassword.value,
        confirmPassword: confirmPassword.value
      }
    })

    emit('success')
    close()

  } catch (error) {
    const message = error.response?.data?.message || '修改密碼失敗'

    if (message.includes('舊密碼')) {
      oldPasswordError.value = message
    } else if (message.includes('新密碼')) {
      newPasswordError.value = message
    } else {
      confirmPasswordError.value = message
    }

  } finally {
    loading.value = false
  }
}
</script>