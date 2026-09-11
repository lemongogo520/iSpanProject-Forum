<template>
  <div class="layout">
    <Sidebar />

    <main class="content">
      <h1>使用者管理</h1>
    </main>
  </div>
</template>

<script setup>
import Sidebar from '@/components/config/ConfigSidebar.vue'
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
