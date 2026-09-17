<template>
  <div class="friend-item">
    <v-avatar :badge="{ color: 'success', location: 'bottom end' }" size="default">
      <v-img v-if="friend.photo" src="@/assets/avatar.jpg" width="100%"></v-img>
      <v-icon v-else icon="mdi-account-circle" size="x-large"></v-icon>
      <!-- 紙娃娃頭像 -->
    </v-avatar>
    <div class="friend-info">
      <span class="name"> {{ friend.displayName }} </span>
    </div>
  </div>
</template>
<script setup>
import { VIcon, VImg } from 'vuetify/components';

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

  width: 100%;
  min-height: 64px;

  padding: 12px 16px;
  margin-bottom: 8px;

  background: #f7fbff;
  border: 2px solid #dcecf8;
  border-radius: 10px;

  cursor: pointer;
  transition: 0.15s;
  box-sizing: border-box;

}

/* 滑過 */
.friend-item:hover {
  background: #eaf4ff;
  border-color: #b8d8ed;

  transform: translateX(-2px);
}

/* 頭像 */
.friend-avatar {
  flex-shrink: 0;
  margin-right: 10px;

  border: 2px solid #dcecf8;
}

.friend-info {
  min-width: 0;
  flex: 1;
}

.name {
  color: #355c7d;
  font-size: 15px;
  font-weight: 700;

  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
