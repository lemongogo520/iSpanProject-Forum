import { defineStore } from "pinia";
import { ref } from "vue";
import httpClient from "../httpClient";

export const userAvatar = defineStore("useAvatar", () => {
  const cache = ref({});

  const pendingRequest = ref({});

  function fetchAvatar(userId) {
    if (!userId) return Promise.reject("沒有userId無法取得大頭貼");
    //如果快取有這位使用者，直接回傳資料
    if (cache.value[userId]) {
      return Promise.resolve(cache.value[userId]);
    }

    //如果還在請求中，就回傳剛剛正在請求
    if (pendingRequest.value[userId]) {
      return pendingRequest.value[userId];
    }

    //新使用者，發起API請求
    const request = httpClient({
      method: "get",
      url: `/api/dollitem/equipped/${userId}`,
    })
      .then((response) => {
        const items = response.data;
        const newEquipped = {};

        //轉字典格式供畫布渲染使用
        if (items && items.length > 0) {
          items.forEach((item) => {
            if (item && item.category) {
              newEquipped[item.category.id] = item;
            }
          });
        }
        //將結果存入快取中
        cache.value[userId] = newEquipped;
        return newEquipped;
      })
      .catch((error) => {
        console.error(`無法取得玩家 ${userId} 的大頭貼`, error);
        throw error;
      })
      .finally(() => {
        //不管成功失敗，都要清理請求狀態
        delete pendingRequest.value[userId];
      });

    //把正在請求的promise記住
    pendingRequest.value[userId] = request;
    return request;
  }
  // 儲存套裝後呼叫，清除指定使用者的快取，讓下次渲染重新打 API
  function clearCache(userId) {
    delete cache.value[userId];
  }
  return { cache, fetchAvatar, clearCache };
});
