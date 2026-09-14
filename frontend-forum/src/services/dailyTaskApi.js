const API_BASE =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8085";

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    credentials: "include",
    headers: {
      Accept: "application/json",
      ...options.headers,
    },
  });

  const contentType = response.headers.get("content-type") || "";
  const data = contentType.includes("application/json")
    ? await response.json()
    : null;

  if (!response.ok) {
    const error = new Error(
      data?.message ||
        (response.status === 401
          ? "請先登入"
          : `請求失敗（${response.status}）`)
    );

    error.status = response.status;
    throw error;
  }

  if (data === null) {
    throw new Error("後端未回傳 JSON，請確認登入狀態與 API 位址");
  }

  return data;
}

export function getTodayTasks() {
  return request("/api/coins/tasks/today");
}

export function claimTaskReward(taskId) {
  return request(
    `/api/coins/tasks/${encodeURIComponent(taskId)}/claim`,
    { method: "POST" }
  );
}