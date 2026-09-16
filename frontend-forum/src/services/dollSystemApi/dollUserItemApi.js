import { ref } from "vue";
import httpClient from "../httpClient";
import { listAllCategories } from "./dollAllCategoryApi";

export function listUserItems() {
  //圖片基底網址
  const baseUrl = "http://localhost:8080/sprites/";
  //圖層Api解構
  const { categories, fetchCategories } = listAllCategories();
  //響應變數
  const dollItems = ref([]);
  const activeCategoryId = ref(null);

  //呼叫後端Api
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
    fetchCategories()
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
