<template>
  <div class="layout">
    <Sidebar />

    <div class="content">
      <!-- Header -->
      <div class="header">
        <div class="header-title">
          權限管理
        </div>
      </div>

      <!-- Content -->
      <div class="permission-content">

        <!-- 系統管理員 -->
        <v-card class="permission-card">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">
              mdi-shield-crown
            </v-icon>
            系統管理員

            <v-spacer />

            <v-btn
              color="primary"
              prepend-icon="mdi-account-plus"
              @click="openUserDialog(1)"
            >
              新增
            </v-btn>
          </v-card-title>

          <v-divider />

          <v-list v-if="systemAdmins.length">
            <v-list-item
              v-for="user in systemAdmins"
              :key="user.id"
            >
              <template #prepend>
                <DollAvatar v-if="user.uid" :user-id="user.uid" :size="42" :scale="1.4"
                class="comment-avatar" @click.stop.prevent/>
                <v-avatar v-else size="42">
                  <v-icon>
                    mdi-account
                  </v-icon>
                </v-avatar>
              </template>

              <v-list-item-title>
                {{ user.name || user.account }}
              </v-list-item-title>

              <v-list-item-subtitle>
                {{ user.account }}
              </v-list-item-subtitle>

              <template #append>
                <v-btn v-if="user.account != 'admin'"
                  icon="mdi-delete"
                  variant="text"
                  color="error"
                  @click="removePermission(user)"
                />
              </template>
            </v-list-item>
          </v-list>

          <div
            v-else
            class="empty-text"
          >
            目前沒有系統管理員
          </div>
        </v-card>


        <!-- 論壇管理員 -->
        <v-card class="permission-card">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">
              mdi-shield-account
            </v-icon>
            論壇管理員

            <v-spacer />

            <v-btn
              color="primary"
              prepend-icon="mdi-account-plus"
              @click="openUserDialog(2)"
            >
              新增
            </v-btn>
          </v-card-title>

          <v-divider />

          <v-list v-if="forumAdmins.length">
            <v-list-item
              v-for="user in forumAdmins"
              :key="user.uid"
            >
              <template #prepend>
                <DollAvatar v-if="user.uid" :user-id="user.uid" :size="42" :scale="1.4"
                class="comment-avatar" @click.stop.prevent/>
                <v-avatar v-else size="42">
                  <v-icon>
                    mdi-account
                  </v-icon>
                </v-avatar>
              </template>

              <v-list-item-title>
                {{ user.name || user.account }}
              </v-list-item-title>

              <v-list-item-subtitle>
                {{ user.account }}
              </v-list-item-subtitle>

              <template #append>
                <v-btn
                  icon="mdi-delete"
                  variant="text"
                  color="error"
                  @click="removePermission(user)"
                />
              </template>
            </v-list-item>
          </v-list>

          <div
            v-else
            class="empty-text"
          >
            目前沒有論壇管理員
          </div>
        </v-card>

      </div>
    </div>

    <!-- 選擇使用者 -->
    <GetAllUsersDialog
      v-model="userDialog"
      :types="[0]"
      @select="selectUser"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userService } from '@/services/userService'
import Sidebar from '@/components/config/ConfigSidebar.vue'
import GetAllUsersDialog from '@/components/user/GetAllUsersDialog.vue'
import DollAvatar from "@/components/doll/DollAvatar.vue";

const loading = ref(false)
const systemAdmins = ref([])
const forumAdmins = ref([])
const users = ref([])

const userDialog = ref(false)
const selectedPermission = ref(null)

const openUserDialog = (permission) => {
  selectedPermission.value = permission
  userDialog.value = true
}

const selectUser = async (user) => {
  if (selectedPermission.value === 1) {
    systemAdmins.value.push(user)
  }

  if (selectedPermission.value === 2) {
    forumAdmins.value.push(user)
  }

  await userService.updateUserPermission(user.uid, selectedPermission.value)
  userDialog.value = false
  await loadUsers()
}

const removePermission = async (user) => {
  if (user.permission === 1) {
    systemAdmins.value = systemAdmins.value.filter(
      item => item.id !== user.id
    )
  }

  if (user.permission === 2) {
    forumAdmins.value = forumAdmins.value.filter(
      item => item.id !== user.id
    )
  }

  await userService.updateUserPermission(user.uid, 3)
  await loadUsers()
}

const loadUsers = async () => {
  loading.value = true

  try {
    users.value = await userService.getUsers('')

    systemAdmins.value = users.value.filter(
      user => user.permission === 1
    )

    forumAdmins.value = users.value.filter(
      user => user.permission === 2
    )

    console.log('取得使用者:', users)
  } catch (error) {
    console.error('取得使用者失敗:', error)
  } finally {
    loading.value = false
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
  min-height: 100vh;
}

.content {
  flex: 1;
}

.header {
  display: flex;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #ddd;
}

.header-title {
  font-size: 20px;
  font-weight: 600;
}

.permission-content {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.permission-card {
  width: 100%;
}

.empty-text {
  padding: 32px;
  text-align: center;
  color: #888;
}
</style>