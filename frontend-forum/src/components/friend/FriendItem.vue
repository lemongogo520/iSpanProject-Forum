<template>
  <div class="friend-item">
    <v-avatar
      :badge="{ color: 'success', location: 'bottom end' }"
      size="default"
    >
    <img src="@/assets/avatar.jpg" width="100%">
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
<style>
.friend-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: var(--color-card);
  cursor: pointer;
  transition: background-color 0.2s;
}
.friend-info {
  margin-left: 15px;
}
.name {
  color: var(--color-title);
  font-weight: 600;
}
</style>
