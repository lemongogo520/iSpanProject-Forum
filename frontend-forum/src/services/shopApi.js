const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ||
  'http://localhost:8085/api'

const BACKEND_URL =
  import.meta.env.VITE_BACKEND_URL ||
  'http://localhost:8085'


async function request(url, options = {}) {

  const response = await fetch(url, {
    credentials: "include",
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
  });

  if (!response.ok) {

    let message = '系統發生錯誤'

    try {
      const data = await response.json()

      message =
        data.message ||
        data.error ||
        JSON.stringify(data)

    } catch {
      const text = await response.text()

      if (text) {
        message = text
      }
    }

    throw new Error(message)
  }

  if (response.status === 204) {
    return null
  }

  return response.json()
}


/**
 * 查詢商城商品
 *
 * GET /api/shop/products
 *
 * 或：
 * GET /api/shop/products?type=CLOTHES
 */
export async function getProducts(type = null) {

  let url = `${API_BASE_URL}/shop/products`

  if (type) {

    const params = new URLSearchParams()

    params.append('type', type)

    url += `?${params.toString()}`
  }

  return request(url)
}


/**
 * 購買商品：會員身分由後端登入狀態取得
 */
export async function purchaseProduct(productId) {
  const url = `${API_BASE_URL}/shop/products/${productId}/purchase`;

  return request(url, {
    method: "POST",
    credentials: "include",
  });
}

/**
 * 處理商品圖片網址
 */
export function resolveProductImage(imageUrl) {

  if (!imageUrl) {
    return null
  }

  if (
    imageUrl.startsWith('http://') ||
    imageUrl.startsWith('https://')
  ) {
    return imageUrl
  }

  return `${BACKEND_URL}${imageUrl}`
}