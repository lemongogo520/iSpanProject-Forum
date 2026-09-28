<template>
  <v-dialog v-model="dialog" max-width="500">
    <v-card>
      <v-card-title>選擇使用者</v-card-title>

      <v-card-text>
        <v-autocomplete
        v-model="selectedUser"
        :items="users"
        :loading="loading"
        :item-title="userTitle"
        item-value="uid"
        return-object
        placeholder="輸入姓名或帳號"
        prepend-inner-icon="mdi-magnify"
        variant="outlined"
        clearable
        no-data-text="找不到使用者"
        :custom-filter="filterUsers"
        >
        <template #item="{ props, item }">
            <v-list-item
            v-bind="props"
            :title="displayName(item)"
            :subtitle="item?.account"
            >
                <template #prepend>
                    <DollAvatar v-if="item.uid" :user-id="item.uid" :size="32" :scale="1.2"
                      class="comment-avatar" @click.stop.prevent/>
                    <v-avatar v-else size="40">
                        <v-icon>
                        mdi-account
                        </v-icon>
                    </v-avatar>
                </template>
            </v-list-item>
        </template>

        <template #selection="{ item }">
            <div
                v-if="item"
                class="selected-user"
            >
                <DollAvatar v-if="item.uid" :user-id="item.uid" :size="32" :scale="1.2"
                  class="comment-avatar" @click.stop.prevent/>
                <v-avatar v-else size="32">
                <v-icon>
                    mdi-account
                </v-icon>
                </v-avatar>

                <span>
                {{ displayName(item) }}
                </span>
            </div>
        </template>
        </v-autocomplete>
      </v-card-text>

      <v-card-actions>
        <v-spacer />

        <v-btn
          color="primary"
          :disabled="!selectedUser"
          @click="confirmUser"
        >
          確認
        </v-btn>
        <v-btn
          variant="text"
          @click="closeDialog"
        >
          取消
        </v-btn>

      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import httpClient from '@/services/httpClient'
import DollAvatar from "@/components/doll/DollAvatar.vue";

//types=0:取得全部使用者
//types=1:取得系統管理者
//types=2:取得討論版管理者
//types=3:取得一般使用者
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  types: {
    type: Array,
    default: () => [0],
    validator: values =>
      values.every(value => [0, 1, 2, 3].includes(value))
  }
})

const emit = defineEmits([
  'update:modelValue',
  'select'
])

const dialog = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

const users = ref([])
const selectedUser = ref(null)
const loading = ref(false)

function userTitle(user) {
  return displayName(user)
}

function displayName(user) {
  if (user?.name?.trim()) {
    return `${user.name}`
  }

  return user?.account || '未知使用者'
}

function filterUsers(_value, query, item) {
  if (!query) {
    return true
  }

  const keyword = query.toLowerCase().trim()
  const user = item.raw ?? item

  return (
    user?.name?.toLowerCase().includes(keyword) ||
    user?.account?.toLowerCase().includes(keyword)
  )
}

async function getAllUsers() {
  loading.value = true

  try {
    const response = await httpClient({
      method: 'get',
      url: '/api/users/allUsers',
      params: {
        types: props.types
      }
    })

    users.value = response.data
  } catch (error) {
    console.error('取得使用者失敗:', error)
    console.error('status:', error.response?.status)
    console.error('data:', error.response?.data)
  } finally {
    loading.value = false
  }
}

function closeDialog() {
  dialog.value = false
}

function confirmUser() {
  if (!selectedUser.value) {
    return
  }

  emit('select', selectedUser.value)
  dialog.value = false
}

watch(
  () => props.modelValue,
  value => {
    if (value) {
      selectedUser.value = null
      getAllUsers()
    }
  }
)
</script>

<style scoped>
.selected-user {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>