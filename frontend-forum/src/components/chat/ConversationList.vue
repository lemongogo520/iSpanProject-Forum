<template>
  <p class="title">近期對話</p>
  <div class="chat-list">
    <div v-if="chatList.length === 0" class="empty-text">
      點擊一名好友開始聊天吧
      <template>
        <FriendItem
          v-for="friend in friendList"
          :key="friend.userId"
          :friend="friend"
          @click="emit('select-friend', friend)"
        />
      </template>
    </div>
    <div v-else></div>
  </div>
</template>
<script setup></script>
<style></style>
