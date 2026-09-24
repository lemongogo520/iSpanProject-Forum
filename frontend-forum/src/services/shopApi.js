import httpClient from "@/services/httpClient";

const BACKEND_URL =
  import.meta.env.VITE_BACKEND_URL || "http://localhost:8080";

// 檢查 API 回傳資料
function getData(response) {
  const data = response.data;

  if (data === null || typeof data !== "object") {
    throw new Error("API 回傳格式不正確，請確認登入狀態與後端設定");
  }

  return data;
}

// 取得商品分類
export async function getCategories() {
  const response = await httpClient.get("/api/dollshop/categories");
  const data = getData(response);

  if (!Array.isArray(data)) {
    throw new Error("商品分類資料格式不正確");
  }

  return data;
}

// 取得分頁商品，頁碼從 1 開始
export async function getProducts(categoryId, pageNumber = 1) {
  const response = await httpClient.get("/api/dollshop/items", {
    params: {
      categoryId,
      pageNumber,
    },
  });

  const data = getData(response);

  if (!Array.isArray(data.content) || !Number.isInteger(data.totalPages)) {
    throw new Error("商品分頁資料格式不正確");
  }

  return data;
}

// 購買商品，會員身分由後端 Session 取得
export async function purchaseProduct(itemId) {
  const response = await httpClient.post(
    `/api/dollshop/items/${encodeURIComponent(itemId)}/purchase`,
  );

  const data = getData(response);

  if (data.status !== "COMPLETED" || data.itemId !== itemId) {
    throw new Error(
      "無法確認購買結果，請重新整理商品與餘額，勿立即重複購買",
    );
  }

  return data;
}

// 處理商品圖片網址
export function resolveProductImage(filePath) {
  if (!filePath) return null;

  if (/^https?:\/\//i.test(filePath)) {
    return filePath;
  }

  const backendUrl = BACKEND_URL.replace(/\/+$/, "");
  const path = filePath.replace(/^\/+/, "");
  const imagePath = path.startsWith("sprites/")
    ? path
    : `sprites/${path}`;

  return `${backendUrl}/${imagePath}`;
}