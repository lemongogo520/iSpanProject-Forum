<template>
  <div class="user-selector">
    <div class="user-selector-title">
      選擇使用者
    </div>

    <v-autocomplete v-model="selectedUser" :items="availableUsers" :loading="loading" :item-title="userTitle" item-value="uid"
      return-object placeholder="輸入姓名或帳號" prepend-inner-icon="mdi-magnify" variant="outlined" clearable
      no-data-text="找不到使用者" :custom-filter="filterUsers" @update:model-value="handleUserChange">
      <template #item="{ props, item }">
        <v-list-item v-bind="props" :title="displayName(item)" :subtitle="item?.account">
          <template #prepend>
            <v-avatar size="40">
              <v-img v-if="item?.photoUrl" :src="item.photoUrl" cover />
              <v-icon v-else>
                mdi-account
              </v-icon>
            </v-avatar>
          </template>
        </v-list-item>
      </template>

      <template #selection="{ item }">
        <div v-if="item" class="selected-user">
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

    <div v-if="!immediateSelect" class="user-selector-actions">
      <v-btn variant="text" @click="clearUser">
        清除
      </v-btn>

      <v-btn color="primary" :disabled="!selectedUser" @click="confirmUser">
        確認
      </v-btn>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import httpClient from '@/services/httpClient'
import DollAvatar from "@/components/doll/DollAvatar.vue";

//types=0:取得全部使用者
//types=1:取得系統管理者
//types=2:取得討論版管理者
//types=3:取得一般使用者
const props = defineProps({
  types: {
    type: Array,
    default: () => [0],
    validator: values =>
      values.every(value => [0, 1, 2, 3].includes(value))
  },
  immediateSelect: {
    type: Boolean,
    default: false,
  },
  // 添加篩選 沒傳入不會怎樣
  excludeIds: {
    type: Array,
    default: () => [],
  },
})

const availableUsers = computed(() => {
  const excluded = new Set(props.excludeIds)
  return users.value.filter((u) => !excluded.has(u.uid))
})

const emit = defineEmits([
  'select'
])

const users = ref([])
const selectedUser = ref(null)
const loading = ref(false)

function userTitle(user) {
  if (user?.name?.trim()) {
    return `${user.name} ${user.account}`
  }

  return user?.account || '未知使用者'
}

function displayName(user) {
  if (user?.name?.trim()) {
    return user.name
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

function clearUser() {
  selectedUser.value = null
}

function confirmUser() {
  if (!selectedUser.value) {
    return
  }

  emit('select', selectedUser.value)
}

function handleUserChange(user) {
  if (props.immediateSelect) {
    emit("select", user);
  }
}

defineExpose({ clearUser, reload: getAllUsers })

onMounted(() => {
  getAllUsers()
})
</script>

<style scoped>
.user-selector {
  width: 100%;
  max-width: 500px;
}

.user-selector-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
}

.selected-user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-selector-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}
</style>