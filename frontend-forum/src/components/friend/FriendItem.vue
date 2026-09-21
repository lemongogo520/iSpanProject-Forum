<template>
  <DollAvatar :userId="friend.userId" :scale="1.5" :size="size" />

  <div class="text-h6 mt-4">{{ friend.displayName }}</div>
  <div class="text-body-2 text-medium-emphasis">
    @{{ friend.account }}
  </div>
</template>
<script setup>
import { VIcon, VImg } from "vuetify/components";
import DollAvatar from "@/components/doll/DollAvatar.vue";

defineProps({
  friend: {
    type: Object,
    required: true,
  },
  size:{
    type: Number,
    required:true
  }
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

  /* background: #f7fbff;
  border: 2px solid #dcecf8;
  border-radius: 10px; */

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
