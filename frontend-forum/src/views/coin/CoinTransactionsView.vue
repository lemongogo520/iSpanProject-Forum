<script setup>
import { ref, onMounted } from "vue";
import httpClient from "@/services/httpClient";

const records = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const needsLogin = ref(false);
const loaded = ref(false);

// 後端頁碼從 0 開始。
const page = ref(0);
const size = 10;
const totalPages = ref(0);
const totalElements = ref(0);

const transactionTypes = {
  INITIAL: { label: "初始點數", direction: 1 },
  TRANSFER_IN: { label: "轉帳收入", direction: 1 },
  TRANSFER_OUT: { label: "轉帳支出", direction: -1 },
  DAILY_TASK_REWARD: { label: "每日任務獎勵", direction: 1 },
  DAILY_REWARD: { label: "每日獎勵", direction: 1 },
  PURCHASE: { label: "商城購買", direction: -1 },
  REFUND: { label: "退款", direction: 1 },
  PURCHASE_REFUND: { label: "商城退款", direction: 1 },
  ADMIN_ADD: { label: "管理員加點（歷史紀錄）", direction: 1 },
  ADMIN_DEDUCT: { label: "管理員扣點（歷史紀錄）", direction: -1 },
};

function typeLabel(type) {
  return transactionTypes[type]?.label ?? type ?? "未知類型";
}

function integerValue(value) {
  if (typeof value === "number" && Number.isSafeInteger(value)) {
    return BigInt(value);
  }

  if (typeof value === "string" && /^-?\d+$/.test(value)) {
    return BigInt(value);
  }

  return null;
}

function formatPoints(value) {
  const number = integerValue(value);
  return number === null ? "—" : number.toLocaleString("zh-TW");
}

// 後端 amount 儲存正數，畫面依交易類型呈現增減。
function formatAmount(record) {
  const amount = integerValue(record.amount);

  if (amount === null) return "—";
  if (amount === 0n) return "0";

  const direction = transactionTypes[record.transactionType]?.direction;

  // 未知類型不猜測收入或支出。
  if (!direction) return amount.toLocaleString("zh-TW");

  const magnitude = amount < 0n ? -amount : amount;
  const sign = direction > 0 ? "+" : "-";

  return `${sign}${magnitude.toLocaleString("zh-TW")}`;
}

function amountClass(record) {
  const direction = transactionTypes[record.transactionType]?.direction;

  if (direction === 1) return "text-success";
  if (direction === -1) return "text-error";

  return "";
}

const dateFormatter = new Intl.DateTimeFormat("zh-TW", {
  timeZone: "Asia/Taipei",
  year: "numeric",
  month: "2-digit",
  day: "2-digit",
  hour: "2-digit",
  minute: "2-digit",
  second: "2-digit",
  hourCycle: "h23",
});

function formatTime(value) {
  if (!value) return "—";

  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? "—" : dateFormatter.format(date);
}

async function loadTransactions(targetPage = page.value) {
  if (loading.value) return;

  loading.value = true;
  errorMessage.value = "";
  needsLogin.value = false;
  loaded.value = false;
  records.value = [];

  try {
    const response = await httpClient.get("/api/coins/transactions", {
      params: {
        page: targetPage,
        size,
      },
    });

    const data = response.data;

    if (
      !data ||
      !Array.isArray(data.content) ||
      !Number.isInteger(data.page) ||
      data.page < 0 ||
      !Number.isInteger(data.totalPages) ||
      data.totalPages < 0 ||
      !Number.isSafeInteger(data.totalElements) ||
      data.totalElements < 0
    ) {
      throw new Error("交易紀錄回傳格式不正確，請確認 API 設定");
    }

    records.value = data.content;
    page.value = data.page;
    totalPages.value = data.totalPages;
    totalElements.value = data.totalElements;
    loaded.value = true;
  } catch (error) {
    const status = error.response?.status;

    if (status === 401) {
      needsLogin.value = true;
      errorMessage.value = "請先登入後查看點數交易紀錄";
    } else if (!error.response && error.request) {
      errorMessage.value = "目前無法連線至後端，請稍後重新整理";
    } else {
      errorMessage.value =
        error.response?.data?.message ||
        error.message ||
        "讀取交易紀錄失敗，請稍後再試";
    }
  } finally {
    loading.value = false;
  }
}

onMounted(() => loadTransactions(0));
</script>

<template>
  <v-container class="transactions-page py-8">
    <div class="d-flex align-center flex-wrap ga-3 mb-6">
      <div>
        <h1 class="text-h4 font-weight-bold">點數交易紀錄</h1>
        <p class="text-body-2 text-medium-emphasis mt-3">
          查看點數收入、支出與每筆交易後的餘額。
        </p>
      </div>

      <v-spacer />

      <v-btn
        color="primary"
        variant="outlined"
        :to="{ name: 'DailyTasks' }"
      >
        每日任務
      </v-btn>

      <v-btn
        color="primary"
        variant="tonal"
        :loading="loading"
        :disabled="loading"
        @click="loadTransactions(0)"
      >
        重新整理
      </v-btn>
    </div>

    <v-alert
      v-if="errorMessage"
      type="error"
      variant="tonal"
      class="mb-4"
      role="alert"
    >
      {{ errorMessage }}
    </v-alert>

    <v-btn
      v-if="needsLogin"
      color="primary"
      :to="{ name: 'Login' }"
      class="mb-4"
    >
      前往登入
    </v-btn>

    <v-progress-linear
      v-if="loading"
      indeterminate
      color="primary"
      aria-label="讀取交易紀錄中"
      class="mb-4"
    />

    <v-card v-if="loaded" variant="outlined" rounded="lg">
      <v-card-text class="d-flex align-center flex-wrap ga-2">
        <span class="font-weight-medium">
          共 {{ totalElements.toLocaleString("zh-TW") }} 筆交易
        </span>

        <v-spacer />

        <span class="text-body-2 text-medium-emphasis">
          時間以台灣時區顯示
        </span>
      </v-card-text>

      <v-divider />

      <div
        v-if="records.length === 0"
        class="pa-8 text-center text-medium-emphasis"
      >
        {{ totalElements === 0 ? "目前尚無點數交易紀錄" : "此頁沒有資料，請重新整理" }}
      </div>

      <v-table v-else class="transaction-table">
        <thead>
          <tr>
            <th scope="col">交易時間</th>
            <th scope="col">交易類型</th>
            <th scope="col" class="text-right">點數增減</th>
            <th scope="col" class="text-right">交易後餘額</th>
            <th scope="col">說明</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="record in records" :key="record.id">
            <td class="time-cell">{{ formatTime(record.createTime) }}</td>

            <td>{{ typeLabel(record.transactionType) }}</td>

            <td
              class="text-right font-weight-bold number-cell"
              :class="amountClass(record)"
            >
              {{ formatAmount(record) }}
            </td>

            <td class="text-right number-cell">
              {{ formatPoints(record.balanceAfter) }}
            </td>

            <td class="description-cell">
              {{ record.description || "—" }}
            </td>
          </tr>
        </tbody>
      </v-table>

      <template v-if="totalPages > 0">
        <v-divider />

        <div class="d-flex align-center justify-center flex-wrap ga-3 pa-4">
          <v-btn
            variant="text"
            :disabled="loading || page === 0"
            @click="loadTransactions(page - 1)"
          >
            上一頁
          </v-btn>

          <span class="text-body-2">
            第 {{ page + 1 }}／{{ totalPages }} 頁
          </span>

          <v-btn
            variant="text"
            :disabled="loading || page >= totalPages - 1"
            @click="loadTransactions(page + 1)"
          >
            下一頁
          </v-btn>
        </div>
      </template>
    </v-card>

    <p class="text-body-2 text-medium-emphasis mt-5">
      每日任務完成後需手動領獎，入帳後才會產生交易紀錄。
      「交易後餘額」為該筆交易當時的餘額。
    </p>
  </v-container>
</template>

<style scoped>
.transactions-page {
  max-width: 1100px;
}

.transaction-table :deep(table) {
  min-width: 760px;
}

.time-cell,
.number-cell {
  white-space: nowrap;
}

.number-cell {
  font-variant-numeric: tabular-nums;
}

.description-cell {
  min-width: 180px;
  max-width: 360px;
  overflow-wrap: anywhere;
}
</style>