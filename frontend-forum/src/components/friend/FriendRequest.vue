<template>
  <div class="request-area">
    <FriendItem :friend="request.friend" />
    <div class="request-actions">
      <span v-if="processing[request.id] === 'accepted'" class="btn result"
        >已接受
      </span>
      <span v-else-if="processing[request.id] === 'rejected'" class="btn result"
        >已拒絕
      </span>

      <template v-else>
        <v-btn
          class="btn accept"
          :loading="processing[request.id] === 'accepting'"
          @click="acceptFriendRequest(request.id)"
          >接受
        </v-btn>
        <v-btn
          class="btn reject"
          :loading="processing[request.id] === 'rejecting'"
          @click="rejectFriendRequest(request.id)"
          >拒絕
        </v-btn>
      </template>
    </div>
  </div>
</template>
<script setup>
import FriendItem from "./FriendItem.vue";
import { useFriendStore } from "@/stores/friend";
import { storeToRefs } from "pinia";

defineProps({
  request: {
    type: Object,
    required: true,
  },
});

const friendStore = useFriendStore();
const { processing } = storeToRefs(friendStore);
const { acceptFriendRequest, rejectFriendRequest } = friendStore;
</script>
<style>
.request-actions {
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn {
  padding: 8px 16px;
  margin: 8px;

  border-radius: 10px;
  border: 2px solid;
  color: white;

  font-weight: 700;
  cursor: pointer;

  transition: 0.1s;
}
/* 接受 */
.btn.accept {
  background: #8bbce0;
  box-shadow: 0 3px 0 #5f91b5;
  border-color: #5f91b5;
}
.btn.accept:hover {
  background: #a6cce8;
}

/* 拒絕  */
.btn.reject {
  background: #d99a9a;
  box-shadow: 0 3px 0 #b97878;
  border-color: #b97878;
}

.btn.reject:hover {
  background: #e2aaaa;
}
/* 按下 */
.btn:active {
  transform: translateY(3px);
  box-shadow: 0 0 0 currentColor;
}

/* 已處理 */
.btn.result {
  background: #b8b8b8;
  border-color: #8f8f8f;
  box-shadow: 0 3px 0 #8f8f8f;

  cursor: default;
}
</style>
