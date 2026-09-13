import httpClient from "@/services/httpClient";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useFriendStore = defineStore("friend", () => {

  const friendList = ref([]);
  const requestList = ref([]);
  const sendRequest = ref([]);

  // 取得好友列表
  const getFriendList = ()=>{
    return httpClient.get("/api/friendship/list")
      .then((response) => {
        friendList.value = response.data;
      })
  }

  // 好友邀請列表
  const getRequestList = ()=> {
    return httpClient.get('/api/friendship/request/received/list')
      .then(response => {
        requestList.value = response.data;
      })
  }

  // 寄出的好友邀請
  const getSendRequest = ()=>{
    return httpClient.get('/api/friendship/request/send/list')
    .then(response => {
        sendRequest.value = response.data
    })
  }

  // ===============好友操作=========================
  const sendFriendRequest = (targetUserId) => {
      httpClient({
        method: 'post',
        url: '/api/friendship/request',
        data: {
          targetUserId
        }
      })
  }

  const acceptFriendRequest = (friendshipId)=>{
    return httpClient.patch(`/api/friendship/request/${friendshipId}/accept`)
    .then(getFriendList())
  }

  const rejectFriendRequest = (friendshipId) => {
    return httpClient.delete(`/api/friendship/request/${friendshipId}`)
    .then(getFriendList())
  }







  // 重新刷新好友列表
  const refreshFriendData = ()=>{
    return Promise.all([
      getFriendList(),
      getRequestList(),
      getSendRequest()
    ])
  }


  return { 
    friendList,
    requestList,
    sendRequest,

    getFriendList,
    getRequestList,
    getSendRequest,

    sendFriendRequest,
    acceptFriendRequest,
    rejectFriendRequest,

    refreshFriendData
  };



});
