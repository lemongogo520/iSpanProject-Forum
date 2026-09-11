<template>
  <div class="friend-item">
    <v-avatar
      :badge="{ color: 'success', location: 'bottom end' }"
      size="default"
      color="#000000"
      image="https://p2.itc.cn/q_70/images03/20211223/1ed60e7b9d0f49e9b8e508aff5d61ddd.jpeg"
    >
      <!-- 紙娃娃頭像 -->
    </v-avatar>
    <div class="friend-info">
      <span class="name"> {{ friend.displayName }} </span>
    </div>
  </div>
</template>
<script setup>
const props = defineProps({
  friend: {
    type: Object,
    required: true,
  },
});
</script>
<style scoped>
.friend-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: var(--color-card);
  border-bottom: 1px solid var(--color-border);
  cursor: pointer;
  transition: background-color 0.2s;
}
.friend-info {
  margin-left: 16px;
}
.name {
  color: var(--color-text);
  font-weight: 600;
}
</style>
