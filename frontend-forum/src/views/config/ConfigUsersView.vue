<template>
  <div class="layout">
    <Sidebar />

    <main class="content">
        <h1>使用者管理</h1>
        <GetAllUsers
        :types="[1,2]"
        @select="UserSelect"
        />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Sidebar from '@/components/config/ConfigSidebar.vue'
import GetAllUsers from '@/components/user/GetAllUsers.vue';

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
