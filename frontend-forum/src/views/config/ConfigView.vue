<template>
  <div class="layout">
    <Sidebar />

    <main class="content">
      <h1>系統設定</h1>
      <v-btn @click="showUserDialog = true">
        選擇使用者
      </v-btn>

    </main>
    <GetAllUsersDialog
      v-model="showUserDialog"
      :types="[0]"
      @select="UserSelect"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Sidebar from '@/components/config/ConfigSidebar.vue'
import GetAllUsersDialog from '@/components/user/GetAllUsersDialog.vue';

const showUserDialog = ref(false)
const selectedUser = ref(null)

function UserSelect(user) {
  selectedUser.value = user

  console.log('選擇的使用者：', selectedUser.value)
}

</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.content {
  flex: 1;
  padding: 20px;
}
</style>
