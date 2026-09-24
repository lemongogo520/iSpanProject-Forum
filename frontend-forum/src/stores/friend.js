import httpClient from "@/services/httpClient";
import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useFriendStore = defineStore("friend", () => {
  const friendList = ref([]);
  const requestList = ref([]);
  const sendRequestList = ref([]);
  const selectedFriendId = ref(null);
  const loading = ref(false);
  const processing = ref({});
  const error = ref(null);


  const asArray = (data) => (Array.isArray(data) ? data : []);
  const selectedFriend = computed(() =>
    friendList.value.find((f) => f.userId === selectedFriendId.value) ?? null
  );

  const selectFriend = (userId) => {
    selectedFriendId.value = userId;
  };

  // 取得好友列表
  const getFriendList = async () => {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await httpClient.get("/api/friendship/list");
      friendList.value = asArray(data);
    } catch (e) {
      error.value = "取得好友列表失敗";
      console.log("取得好友列表失敗", e);
    } finally {
      loading.value = false;
    }
  };


  // 收到的好友邀請列表
  const getRequestList = async () => {
    try {
      const { data } = await httpClient.get("/api/friendship/request/received/list");
      requestList.value = asArray(data);
    } catch (e) {
      console.log("取得好友邀請列表失敗", e)
    }
  };

  // 寄出的好友邀請列表
  const getSendRequestList = async () => {
    try {
      const { data } = await httpClient.get("/api/friendship/request/send/list");
      sendRequestList.value = asArray(data);
    } catch (e) {
      console.error("取得寄出邀請列表失敗", e);
    }

  };

  // 接受好友邀請
  const acceptFriendRequest = async (friendshipId) => {
    processing.value[friendshipId] = true;
    try {
      await httpClient.patch(`/api/friendship/request/${friendshipId}/accept`);
      requestList.value = requestList.value.filter((r) => r.id !== friendshipId);
    } catch (e) {
      error.value = "接受好友邀請失敗";
      console.log("接受好友邀請失敗", e);
    } finally {
      delete processing.value[friendshipId];
    }
  };
  // 拒絕好友邀請
  const rejectFriendRequest = async (friendshipId) => {
    processing.value[friendshipId] = true;
    try {
      await httpClient.delete(`/api/friendship/request/${friendshipId}`);
      requestList.value = requestList.value.filter((r) => r.id !== friendshipId);
    } catch (e) {
      error.value = "拒絕好友邀請失敗";
      console.error("拒絕好友邀請失敗", e);
    } finally {
      delete processing.value[friendshipId];
    }
  };



  // 寄出好友邀請
  const sendFriendRequest = async (targetUserId) => {
    try {
      await httpClient.post("/api/friendship/request", { targetUserId });
      await getSendRequestList();
    } catch (e) {
      error.value = "送出好友邀請失敗";
      console.error("送出好友邀請失敗", e);
      throw e;
    }
  };

  const cancelFriendRequest = async (friendshipId) => {
    processing.value[friendshipId] = true;
    try {
      await httpClient.patch(`/api/friendship/request/${friendshipId}/cancel`);
      sendRequestList.value = sendRequestList.value.filter((r) => r.id !== friendshipId);
    } catch (e) {
      error.value = "取消好友邀請失敗";
      console.error("取消好友邀請失敗", e);
    } finally {
      delete processing.value[friendshipId];
    }
  }

  // 重新刷新好友列表
  const refreshFriendData = () =>
    Promise.all([getFriendList(), getRequestList(), getSendRequestList()]);

  const reset = () => {
  friendList.value = [];
  requestList.value = [];
  sendRequestList.value = [];
  selectedFriendId.value = null;
  processing.value = {};
  error.value = null;
};

  return {
    friendList, requestList, sendRequestList,
    selectedFriendId, selectedFriend,
    loading, processing, error,

    selectFriend,
    getFriendList, getRequestList, getSendRequestList,
    acceptFriendRequest, rejectFriendRequest,
    sendFriendRequest, cancelFriendRequest,
    refreshFriendData,
    reset,
  };
});
