<template>
  <div class="requests_list">
    <div v-if="requestList.length === 0" class="empty-text">
      目前沒有好友邀請
    </div>
    <div v-else class="request-scroll">
      <div
        v-for="request in requestList"
        :key="request.id"
        class="request-item"
      >
        <FriendRequest :request="request" />
      </div>
    </div>
  </div>
</template>
<script setup>
import { onMounted } from "vue";
import { useFriendStore } from "@/stores/friend";
import { storeToRefs } from "pinia";
import FriendRequest from "./FriendRequest.vue";

const friendStore = useFriendStore();
const { requestList } = storeToRefs(friendStore);
const { getRequestList } = friendStore;

onMounted(() => {
  getRequestList();
});
</script>

<style>
.requests_list {
  width: 100%;
  flex-direction: column;
}

.request-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  width: 100%;
  padding: 4px 2px 12px;
  box-sizing: border-box;
}

.request-item {
  flex: 0 0 200px;
  padding: 12px;
  /* border: 2px solid var(--color-border);
  border-radius: 8px; */
  box-sizing: border-box;
  background: var(--color-card);
  /* background: #62a6d3; */
}
</style>
