import httpClient from "@/services/httpClient";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useFriendStore = defineStore("friend", () => {
  const friendList = ref([]);
  const requestList = ref([]);
  const sendRequest = ref([]);
  const loading = ref(false);
  const processing = ref({});
  const sending = ref(false);

  // 取得好友列表
  const getFriendList = async () => {
    loading.value = true;
    await httpClient
      .get("/api/friendship/list")
      .then((response) => {
        friendList.value = response.data ?? [];
      })
      .catch((error) => console.log("取得好友列表失敗", error))
      .finally((loading.value = false));
  };

  // 收到的好友邀請列表
  const getRequestList = async () => {
    await httpClient
      .get("/api/friendship/request/received/list")
      .then((response) => {
        requestList.value = response.data ?? [];
      })
      .catch((error) => console.log("取得好友邀請列表失敗", error));
  };
  // 接受好友邀請
  const acceptFriendRequest = (friendshipId) => {
    processing.value[friendshipId] = "accepting";
    httpClient
      .patch(`/api/friendship/request/${friendshipId}/accept`)
      .then(() => {
        processing.value[friendshipId] = "accepted";
        requestList.value = requestList.value.filter(
          (request) => request.id !== friendshipId,
        );
        getFriendList();
      })
      .catch((error) => console.log("接受好友邀請失敗", error));
  };
  // 拒絕好友邀請
  const rejectFriendRequest = (friendshipId) => {
    processing.value[friendshipId] = "rejecting";
    httpClient
      .delete(`/api/friendship/request/${friendshipId}`)
      .then(() => {
        processing.value[friendshipId] = "rejected";
        requestList.value = requestList.value.filter(
          (request) => request.id !== friendshipId,
        );
        getFriendList();
      })
      .catch((error) => console.log("拒絕好友邀請失敗", error));
  };

  // 寄出的好友邀請列表
  const getSendRequestList = async () => {
    await httpClient
      .get("/api/friendship/request/send/list")
      .then((response) => (sendRequest.value = response.data));
  };

  // 寄出好友邀請
  const sendFriendRequest = (targetUserId) => {
    httpClient({
      method: "post",
      url: "/api/friendship/request",
      data: {
        targetUserId,
      },
    });
  };

  // 重新刷新好友列表
  const refreshFriendData = () => {
    return Promise.all([
      getFriendList(),
      getRequestList(),
      getSendRequestList(),
    ]);
  };

  return {
    friendList,
    requestList,
    sendRequest,
    loading,
    processing,

    getFriendList,
    getRequestList,
    acceptFriendRequest,
    rejectFriendRequest,

    getSendRequestList,
    sendFriendRequest,

    refreshFriendData,
  };
});
