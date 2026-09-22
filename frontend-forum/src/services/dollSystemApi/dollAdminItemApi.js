import { ref } from "vue";
import httpClient from "../httpClient";
import { listAllCategories } from "./dollAllCategoryApi";

// 圖片基底網址 (常數)
const baseUrl = "http://localhost:8080/sprites/";

export function useAdminDollItems() {
  const { categories, fetchCategories } = listAllCategories();
  const adminItems = ref([]);

  function fetchAdminItems(categoryId) {
    return httpClient({
      method: "get",
      url: `/api/dollitems/admincheck/${categoryId}`,
    })
      .then((response) => {
        adminItems.value = response.data;
      })
      .catch((error) => {
        console.error("讀取商品失敗", error);
      });
  }

  // 更改商品上下架狀態

  function updateItemStatus(itemId, isActive) {
    return httpClient({
      method: "patch",
      url: `/api/dollitems/status/${itemId}?isActive=${isActive}`,
    })
      .then((response) => {
        console.log(`商品 ${itemId} 狀態更新為: ${isActive ? "上架" : "下架"}`);
      })
      .catch((error) => {
        console.error("更新商品狀態失敗", error);
        return Promise.reject(error); // 將錯誤往外拋，讓外面的畫面知道更新失敗了
      });
  }

  function updateItemName(itemId, newName) {
    return httpClient({
      method: "patch",
      url: `/api/dollitems/changename/${itemId}`,
      params: {
        newName: newName,
      },
    })
      .then((response) => {
        console.log(`商品 ${itemId} 名稱更新為: ${newName}`);
      })
      .catch((error) => {
        console.error("更新商品名稱失敗", error);
        return Promise.reject(error); // 將錯誤往外拋，讓外面的畫面知道更新失敗了
      });
  }

  // 測試用：無差別抓取全圖層的所有裝備並一鍵新增
  async function unlockAllItems() {
    try {
      // 1. 取得所有分類
      const catRes = await httpClient.get("/api/dollcategory/all");
      const allCategories = catRes.data;
      
      let allIds = [];
      // 2. 針對每一個分類，去抓出底下的所有商品 ID
      for (const cat of allCategories) {
        const itemRes = await httpClient.get(`/api/dollitems/admincheck/${cat.id}`);
        const items = itemRes.data;
        allIds = allIds.concat(items.map(item => item.id));
      }
      
      if (allIds.length === 0) return Promise.reject("資料庫中沒有任何裝備");

      // 3. 一次性打給 batch API 全部新增
      const response = await httpClient.post("/api/dollitem/userbuy/batch", allIds);
      return { count: allIds.length, message: response.data };
    } catch (error) {
      console.error("全圖鑑解鎖失敗", error);
      return Promise.reject(error);
    }
  }

  return {
    baseUrl,
    categories,
    fetchCategories,
    adminItems,
    fetchAdminItems,
    updateItemStatus,
    updateItemName,
    unlockAllItems,
  };
}
