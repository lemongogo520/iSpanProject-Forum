import httpClient from "../httpClient";
import { listAllCategories } from "./dollAllCategoryApi";
import { ref } from "vue";

// 圖片基底網址 (常數，移到最外面讓大家共用)
const baseUrl = "http://localhost:8080/sprites/";

//使用者衣櫃Api
export function listUserItems() {
  //圖層Api解構
  const { categories, fetchCategories } = listAllCategories();
  //響應變數
  const dollItems = ref([]);
  const activeCategoryId = ref(null);

  //呼叫後端Api(使用者衣櫃)
  function itemsByCategory(categoryId) {
    httpClient({
      method: "get",

      url: `/api/dollitem/userowned/${categoryId}`,
    })
      .then((response) => {
        //篩選出isOwned為true的資料，因為後端會回傳所有資料
        dollItems.value = response.data.filter((item) => item.isOwned === true);
        console.log("裝備資料載入成功", dollItems.value);
      })

      .catch((error) => console.log(error));
  }

  async function loadCategories() {
    fetchCategories();
    try {
      await httpClient.get("/api/dollcategory/all");
      //自動載入第一個分類的部件，避免畫面一開始空白
      if (categories.value.length > 0) {
        const firstCategoryId = categories.value[0].id;
        activeCategoryId.value = firstCategoryId; // 讓 Tabs 高亮第一個按鈕
        itemsByCategory(firstCategoryId); //觸發抓取該圖層的圖片
      }
    } catch (error) {
      return console.error("分類讀取失敗", error);
    }
  }
  return {
    baseUrl,
    itemsByCategory,
    dollItems,
    categories,
    activeCategoryId,
    loadCategories,
  };
}

//商城Api
export function listShopItems() {
  //圖層Api解構
  const { categories, fetchCategories } = listAllCategories();
  //響應變數
  const shopItems = ref([]);
  const activeCategoryId = ref(null);

  //呼叫後端Api(商城)
  function unOwnedItemsByCategory(categoryId) {
    httpClient({
      method: "get",

      url: `/api/dollitem/userowned/${categoryId}`,
    })
      .then((response) => {
        //篩選出 isOwned 為 false 的資料，因為後端會回傳所有資料
        shopItems.value = response.data.filter(
          (item) => item.isOwned === false,
        );
        console.log("商城裝備資料載入成功", shopItems.value);
      })

      .catch((error) => console.log(error));
  }

  async function loadCategories() {
    fetchCategories();
    try {
      await httpClient.get("/api/dollcategory/all");
      //自動載入第一個分類的部件，避免畫面一開始空白
      if (categories.value.length > 0) {
        const firstCategoryId = categories.value[0].id;
        activeCategoryId.value = firstCategoryId; // 讓 Tabs 高亮第一個按鈕
        unOwnedItemsByCategory(firstCategoryId); //觸發抓取該圖層的圖片
      }
    } catch (error) {
      return console.error("分類讀取失敗", error);
    }
  }

  return {
    baseUrl,
    unOwnedItemsByCategory,
    shopItems,
    categories,
    activeCategoryId,
    loadCategories,
  };
}
