/*一次購買多件裝備api 使用方式

1. 在 script setup 的最上方引入
import { addItems } from "@/services/dollSystemApi/dollUserBuyBatchApi";

2. 準備要傳給後端的裝備 ID 陣列

function handleBuyItems() {
  const cartItems = [1, 3, 5]; 
  
3. 直接呼叫，並接上 .then() 與 .catch()
  addItems(cartItems)
    .then((message) => {
      alert(message); 
    })
    .catch((error) => {
      alert("購買失敗，請稍後再試");
    });
}
*/

import httpClient from "../httpClient";

//一次購買多件裝備api
export function addItems(itemIds) {
  return httpClient({
    method: "post",
    url: "/api/dollitem/userbuy/batch",
    data: itemIds,
  })
    .then((response) => {
      console.log("後端回傳:", response.data);
      return response.data;
    })

    .catch((error) => {
      console.log(error);
      throw error;
    });
}

//單次購買裝備api
export function buySingleItems(itemId) {
  return httpClient({
    method: "post",
    url: `/api/dollitem/userbuy/${itemId}`,
  })
    .then((response) => {
      console.log("後端回傳:", response.data);
      return response.data;
    })

    .catch((error) => {
      console.log(error);
      throw error;
    });
}
