<template>
  <div class="layout">
    <Sidebar />

    <div class="content">
      <!-- Header -->
      <div class="header">
        <div class="header-title">
          使用者管理
        </div>

        <v-btn
          color="primary"
          prepend-icon="mdi-account-plus"
          @click="openCreateDialog"
        >
          新增使用者
        </v-btn>
      </div>

      <!-- Main -->
      <div class="main-content">

        <!-- Search -->
        <div class="search-section">
          <v-text-field
            v-model="search"
            label="搜尋使用者"
            placeholder="輸入帳號或名稱"
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            hide-details
            clearable
            @keyup.enter="searchUsers"
          />

          <v-btn
            color="primary"
            @click="searchUsers"
          >
            搜尋
          </v-btn>

          <v-btn
            variant="outlined"
            @click="clearSearch"
          >
            清除
          </v-btn>
        </div>

        <!-- User Table -->
        <div class="table-container">
          <v-data-table
            :headers="headers"
            :items="users"
            :loading="loading"
            item-value="id"
            hover
          >
            <template #item.permission="{ item }">
              <v-chip
                size="small"
                variant="tonal"
              >
                {{ permissionText[item.permission] ?? '未知' }}
              </v-chip>
            </template>

            <template #item.actions="{ item }">
              <div class="actions">
                <v-btn
                  icon="mdi-pencil"
                  size="small"
                  variant="text"
                  color="primary"
                  @click="editUser(item)"
                />

                <v-btn
                  icon="mdi-delete"
                  size="small"
                  variant="text"
                  color="error"
                  @click="deleteUser(item)"
                />
              </div>
            </template>

            <template #no-data>
              <div class="empty">
                找不到使用者
              </div>
            </template>
          </v-data-table>
        </div>
      </div>
    </div>

    <UserDetailView
      v-model="userDetailDialog"
      :account="selectedAccount"
      @saved="reloadPage"
    />
    <!-- Create User Dialog -->
    <v-dialog
      v-model="showCreateDialog"
      max-width="600"
    >
      <v-card>
        <v-card-title class="dialog-title">
          新增使用者
        </v-card-title>

        <v-divider />

        <v-card-text class="dialog-content">

          <v-text-field
            v-model="newUser.account"
            label="帳號"
            variant="outlined"
            density="compact"
            class="mb-4"
          />

          <v-text-field
            v-model="newUser.password"
            label="密碼"
            type="password"
            variant="outlined"
            density="compact"
            class="mb-4"
          />

          <v-text-field
            v-model="newUser.name"
            label="名稱"
            variant="outlined"
            density="compact"
            class="mb-4"
          />

          <v-text-field
            v-model="newUser.email"
            label="Email"
            type="email"
            variant="outlined"
            density="compact"
            class="mb-4"
          />

          <v-select
            v-model="newUser.permission"
            label="權限"
            :items="[
              { title: '一般使用者', value: 3 },
              { title: '討論版管理員', value: 2 },
              { title: '系統管理員', value: 1 }
            ]"
            variant="outlined"
            density="compact"
          />

        </v-card-text>

        <v-divider />

        <v-card-actions class="dialog-actions">
          <v-spacer />

          <v-btn
            variant="text"
            @click="showCreateDialog = false"
          >
            取消
          </v-btn>

          <v-btn
            color="primary"
            @click="createUser"
          >
            新增
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userService } from '@/services/userService'
import Sidebar from '@/components/config/ConfigSidebar.vue'
import UserDetailView from "@/views/user/UserDetailView.vue";

const users = ref([])
const search = ref('')
const loading = ref(false)
const userDetailDialog = ref(false)
const selectedAccount = ref('')

const showCreateDialog = ref(false)

const newUser = ref({
  account: '',
  password: '',
  name: '',
  email: '',
  permission: 3
})

const headers = [
  { title: '帳號', key: 'account' },
  { title: '名稱', key: 'name' },
  { title: 'Email', key: 'email' },
  { title: '權限', key: 'permission' },
  { title: '操作', key: 'actions', sortable: false, align: 'center' }
]

const permissionText = {
  1: '系統管理員',
  2: '討論版管理員',
  3: '一般使用者'
}

const loadUsers = async () => {
  loading.value = true

  try {
    users.value = await userService.getUsers(search.value)
    console.log('取得使用者:', users)
  } catch (error) {
    console.error('取得使用者失敗:', error)
  } finally {
    loading.value = false
  }
}

const searchUsers = async () => {
  await loadUsers()
}

const clearSearch = async () => {
  search.value = ''
  await loadUsers()
}

const openCreateDialog = () => {
  newUser.value = {
    account: '',
    password: '',
    name: '',
    email: '',
    permission: 3
  }

  showCreateDialog.value = true
}

const createUser = async () => {
  try {
    await userService.createUser(newUser.value)

    showCreateDialog.value = false

    await loadUsers()
  } catch (error) {
    console.error('新增使用者失敗:', error)
  }
}

//編輯使用者
const editUser = (user) => {
  selectedAccount.value = user.account
  userDetailDialog.value = true
}

//刪除使用者
const deleteUser = async (user) => {
  try {
    await userService.deleteUser(user.uid)
    reloadPage()
  } catch (error) {
    console.error('刪除使用者失敗:', error)
  }
}

const reloadPage = async () => {
  await loadUsers()
}

onMounted(() => {
  loadUsers()
})

</script>


<style scoped>
.layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.header {
  height: 64px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: white;
  border-bottom: 1px solid #e0e0e0;
}

.header-title {
  font-size: 1.4rem;
  font-weight: 700;
}

.main-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.search-section {
  display: flex;
  align-items: center;
  gap: 12px;
  background: white;
  padding: 16px;
  border-radius: 8px;
}

.search-section .v-text-field {
  flex: 1;
}

.table-container {
  margin-top: 20px;
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 4px;
}

.empty {
  padding: 40px;
  text-align: center;
  color: #777;
}

.dialog-title {
  padding: 18px 24px;
  font-size: 1.25rem;
  font-weight: 700;
}

.dialog-content {
  padding: 24px;
}

.dialog-actions {
  padding: 16px 24px;
}
</style>