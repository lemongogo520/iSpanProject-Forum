import httpClient from "@/services/httpClient";

export async function purchaseDollItem(itemId) {
  if (!Number.isInteger(Number(itemId)) || Number(itemId) <= 0) {
    throw new Error("商品編號不正確");
  }
  const response = await httpClient.post(`/api/dollshop/items/${encodeURIComponent(itemId)}/purchase`);
  const data = response.data;
  if (!data || data.status !== "COMPLETED" || Number(data.itemId) !== Number(itemId)) {
    throw new Error("無法確認購買結果，請重新查詢背包與點數，勿直接重送");
  }
  return data;
}
