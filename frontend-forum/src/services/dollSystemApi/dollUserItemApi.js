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

/**
 * 商城 Api — listShopItems()
 *
 * ─── 使用方式 ───────────────────────────────────────────────
 * 在 Vue 元件的 setup() 中解構取出需要的變數和方法：
 *
 *   const { loadCategories, unOwnedItemsByCategory,
 *           pageContent, currentPageNum, totalPages,
 *           categories, activeCategoryId, baseUrl } = listShopItems();
 *
 *   // 1. 頁面掛載時呼叫，自動抓取圖層列表，並預設載入第一個圖層的商品
 *   onMounted(() => loadCategories());
 *
 *   // 2. 使用者點選其他圖層 Tab 時，手動切換圖層
 *   function onTabChange(categoryId) {
 *     activeCategoryId.value = categoryId;
 *     unOwnedItemsByCategory(categoryId);
 *   }
 *
 *   // 3. 切換分頁時 (如果後端需要 pageNumber 請自行加入參數)
 *   //    目前 API 預設從第 1 頁開始；換頁功能由接手的開發者實作
 *
 * ─── 可以確認的內容 ───────────────────────────────────────────
 * returns {Object} 解構後可使用以下屬性：
 *
 *   baseUrl            {string}  圖片基底網址，拼接後才是完整圖片路徑
 *                                範例：baseUrl + item.filePath
 *
 *   categories         {Ref<Array>} 所有裝備圖層列表（用來渲染 Tab 按鈕）
 *                                每筆格式：{ id, code, typeName, zIndex, isRequired }
 *
 *   activeCategoryId   {Ref<number>} 目前選中的圖層 id（用來高亮 Tab）
 *
 *   pageContent        {Ref<Array>} 目前頁的商品列表（綁定到畫面上渲染商品卡）
 *                                每筆格式：{ id, name, filePath, price, isActive, isOwned, category }
 *                                ⚠️ isOwned: true 表示使用者已擁有，false 表示尚未擁有
 *
 *   currentPageNum     {Ref<number>} 目前頁碼（從 1 開始）
 *
 *   totalPages         {Ref<number>} 總頁數（用來控制分頁按鈕是否還有下一頁）
 *
 *   loadCategories     {Function} 初始化函式，需在 onMounted 呼叫
 *
 *   unOwnedItemsByCategory  {Function(categoryId)} 切換圖層時呼叫，重新抓取該圖層的商品
 */
export function listShopItems() {
  //圖層Api解構
  const { categories, fetchCategories } = listAllCategories();
  //響應變數
  const activeCategoryId = ref(null);
  const pageContent = ref([]);
  const currentPageNum = ref(1);
  const totalPages = ref(1);

  //呼叫後端Api(商城)
  function unOwnedItemsByCategory(categoryId) {
    httpClient({
      method: "get",

      url: `/api/dollitems/dollshop/${categoryId}`,
    })
      .then((response) => {
        // 後端回傳 Page 物件，實際裝備資料放在 content 內
        pageContent.value = response.data.content;
        currentPageNum.value = response.data.number + 1; // Spring 從 0 算，+1 換算成人類頁碼
        totalPages.value = response.data.totalPages;
        console.log("商城裝備資料載入成功", pageContent.value);
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
    pageContent,
    currentPageNum,
    totalPages,
    categories,
    activeCategoryId,
    loadCategories,
  };
}
