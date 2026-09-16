import httpClient from "@/services/httpClient";

function getData(response) {
  const data = response.data;

  // 避免 API 被導向 HTML 頁面後，誤以為請求成功。
  if (data === null || typeof data !== "object") {
    throw new Error("API 未回傳正確資料，請確認登入狀態與後端設定");
  }

  return data;
}

export async function getTodayTasks() {
  const response = await httpClient.get("/api/coins/tasks/today");
  const data = getData(response);

  if (!Array.isArray(data)) {
    throw new Error("每日任務資料格式不正確");
  }

  return data;
}

export async function checkInToday() {
  const response = await httpClient.post("/api/coins/tasks/check-in");
  const data = getData(response);

  if (!data.id || data.taskType !== "CHECK_IN") {
    throw new Error("簽到回傳資料格式不正確");
  }

  return data;
}

export async function claimTaskReward(taskId) {
  const response = await httpClient.post(
    `/api/coins/tasks/${encodeURIComponent(taskId)}/claim`
  );

  const data = getData(response);

  if (data.taskId !== taskId || data.status !== "CLAIMED") {
    throw new Error("無法確認領獎結果，請重新查詢任務");
  }

  return data;
}