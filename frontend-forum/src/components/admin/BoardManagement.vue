<template>
  <!-- 新增／編輯討論版表單 -->
  <v-card class="admin-card pa-4 mb-5" elevation="0">
    <div class="d-flex justify-space-between align-center mb-2">
      <v-card-title class="px-0">
        {{
          isBoardEditMode ? `編輯討論版（ID：${boardForm.id}）` : "新增討論版"
        }}
      </v-card-title>

      <v-btn
        v-if="isBoardEditMode"
        size="small"
        color="grey"
        variant="outlined"
        @click="resetBoardForm"
      >
        取消編輯
      </v-btn>
    </div>

    <v-card-text class="px-0">
      <v-select
        v-model="boardForm.categoryId"
        :items="categories"
        item-title="name"
        item-value="id"
        label="所屬類別"
        variant="outlined"
        density="comfortable"
        :disabled="isBoardEditMode"
      />

      <v-text-field
        v-model="boardForm.name"
        label="討論版名稱"
        variant="outlined"
        density="comfortable"
        clearable
      />

      <v-textarea
        v-model="boardForm.description"
        label="討論版說明"
        variant="outlined"
        rows="3"
        counter="500"
      />

      <v-text-field
        v-model.number="boardForm.rank"
        label="排序"
        type="number"
        variant="outlined"
        density="comfortable"
      />

      <v-btn
        :color="isBoardEditMode ? 'warning' : 'success'"
        prepend-icon="mdi-content-save"
        block
        @click="handleBoardSubmit"
      >
        {{ isBoardEditMode ? "確認修改" : "新增討論版" }}
      </v-btn>
    </v-card-text>
  </v-card>

  <!-- 現有討論版列表 -->
  <v-card class="admin-card pa-4" elevation="0">
    <div class="d-flex justify-space-between align-center mb-3">
      <v-card-title class="px-0">現有討論版</v-card-title>

      <v-btn
        size="small"
        variant="outlined"
        prepend-icon="mdi-refresh"
        @click="fetchBoards"
      >
        重新整理
      </v-btn>
    </div>

    <!-- 載入進度條 -->
    <v-progress-linear
      v-if="boardLoading"
      indeterminate
      color="primary"
      class="mb-4"
    />

    <v-list v-if="boards.length > 0">
      <v-list-item
        v-for="board in boards"
        :key="board.id"
        border
        class="mb-2 rounded"
      >
        <v-list-item-title>{{ board.name }}</v-list-item-title>

        <v-list-item-subtitle>
          類別：{{ board.category?.name ?? "未分類" }} ｜排序：{{ board.rank }}
        </v-list-item-subtitle>

        <div
          v-if="board.description"
          class="text-body-2 text-medium-emphasis mt-1"
        >
          {{ board.description }}
        </div>

        <template #append>
          <div class="d-flex ga-2">
            <v-btn
              size="small"
              color="primary"
              variant="tonal"
              @click="startEditBoard(board)"
            >
              編輯
            </v-btn>

            <v-btn
              size="small"
              color="error"
              variant="tonal"
              @click="requestDeleteBoard(board)"
            >
              刪除
            </v-btn>
          </div>
        </template>
      </v-list-item>
    </v-list>

    <!-- 沒有討論版且載入完成時顯示 -->
    <div
      v-else-if="!boardLoading"
      class="text-medium-emphasis text-center py-5"
    >
      目前沒有討論版
    </div>
  </v-card>

  <!-- 刪除確認視窗 -->
  <DeleteConfirmDialog
    :model-value="deleteDialog"
    :message="deleteDialogMessage"
    :loading="deleteLoading"
    @cancel="closeDeleteDialog"
    @confirm="confirmDelete"
  />
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import httpClient from "@/services/httpClient";
import { useNotification } from "@/composables/useNotification";
import DeleteConfirmDialog from "@/components/common/DeleteConfirmDialog.vue";

// ======================
// Props 與 Emits
// ======================

// 接收父元件傳入的類別清單
defineProps({
  categories: {
    type: Array,
    default: () => [],
  },
});

// 通知父元件討論版清單已更新
const emit = defineEmits(["boards-updated"]);

// ======================
// 通知功能
// ======================

const { showMessage } = useNotification();

// ======================
// 討論版狀態
// ======================

// 討論版清單
const boards = ref([]);

// 討論版清單載入狀態
const boardLoading = ref(false);

// 是否為編輯模式
const isBoardEditMode = ref(false);

// ======================
// 刪除視窗狀態
// ======================

// 是否顯示刪除確認視窗
const deleteDialog = ref(false);

// 準備刪除的討論版
const deleteTarget = ref(null);

// 刪除請求載入狀態
const deleteLoading = ref(false);

// ======================
// 討論版表單
// ======================

const boardForm = ref({
  id: null,
  categoryId: null,
  name: "",
  description: "",
  rank: 0,
});

// ======================
// 錯誤訊息處理
// ======================

// 取得後端錯誤訊息，沒有訊息時使用預設內容
const getErrorMessage = (error, fallback) => {
  const data = error.response?.data;

  if (typeof data === "string") {
    return data;
  }

  return data?.message ?? fallback;
};

// ======================
// 查詢討論版
// ======================

// 取得全部討論版
const fetchBoards = () => {
  boardLoading.value = true;

  return httpClient({
    method: "get",
    url: "/api/boards",
  })
    .then((response) => {
      boards.value = response.data;
      emit("boards-updated", response.data);
    })
    .catch((error) => {
      showMessage(getErrorMessage(error, "取得討論版失敗"), "error");
    })
    .finally(() => {
      boardLoading.value = false;
    });
};

// ======================
// 編輯討論版
// ======================

// 將選取的討論版資料放入表單
const startEditBoard = (board) => {
  boardForm.value = {
    id: board.id,
    categoryId: board.category?.id ?? null,
    name: board.name ?? "",
    description: board.description ?? "",
    rank: board.rank ?? 0,
  };

  isBoardEditMode.value = true;
  window.scrollTo({ top: 0, behavior: "smooth" });
};

// 清空表單並離開編輯模式
const resetBoardForm = () => {
  boardForm.value = {
    id: null,
    categoryId: null,
    name: "",
    description: "",
    rank: 0,
  };

  isBoardEditMode.value = false;
};

// ======================
// 新增討論版
// ======================

// 呼叫新增討論版 API
const createBoard = () => {
  return httpClient({
    method: "post",
    url: "/api/board",
    data: {
      category: {
        id: boardForm.value.categoryId,
      },
      name: boardForm.value.name.trim(),
      description: boardForm.value.description.trim(),
      rank: boardForm.value.rank ?? 0,
    },
  });
};

// ======================
// 修改討論版
// ======================

// 呼叫修改討論版 API
const updateBoard = () => {
  return httpClient({
    method: "put",
    url: `/api/boards/${boardForm.value.id}`,
    data: {
      name: boardForm.value.name.trim(),
      description: boardForm.value.description.trim(),
      rank: boardForm.value.rank ?? 0,
    },
  });
};

// ======================
// 表單送出
// ======================

// 驗證表單並執行新增或修改
const handleBoardSubmit = () => {
  if (!isBoardEditMode.value && !boardForm.value.categoryId) {
    showMessage("請選擇所屬類別", "warning");
    return;
  }

  if (!boardForm.value.name.trim()) {
    showMessage("請輸入討論版名稱", "warning");
    return;
  }

  const editing = isBoardEditMode.value;
  const request = editing ? updateBoard() : createBoard();

  return request
    .then(() => {
      showMessage(editing ? "討論版修改成功" : "討論版新增成功", "success");

      resetBoardForm();
      return fetchBoards();
    })
    .catch((error) => {
      showMessage(getErrorMessage(error, "儲存討論版失敗"), "error");
    });
};

// ======================
// 刪除討論版
// ======================

// 開啟刪除確認視窗
const requestDeleteBoard = (board) => {
  deleteTarget.value = board;
  deleteDialog.value = true;
};

// 關閉刪除確認視窗
const closeDeleteDialog = () => {
  if (deleteLoading.value) {
    return;
  }

  deleteDialog.value = false;
  deleteTarget.value = null;
};

// 確認刪除討論版
const confirmDelete = () => {
  const board = deleteTarget.value;

  if (!board) {
    return;
  }

  deleteLoading.value = true;

  return httpClient({
    method: "delete",
    url: `/api/boards/delete/${board.id}`,
  })
    .then(() => {
      // 如果正在編輯被刪除的討論版，就清空表單
      if (boardForm.value.id === board.id) {
        resetBoardForm();
      }

      showMessage("討論版刪除成功", "success");
      deleteDialog.value = false;
      deleteTarget.value = null;

      return fetchBoards();
    })
    .catch((error) => {
      showMessage(
        getErrorMessage(error, "刪除討論版失敗，請確認討論版內是否仍有文章"),
        "error",
      );
    })
    .finally(() => {
      deleteLoading.value = false;
    });
};

// ======================
// 刪除確認訊息
// ======================

// 根據刪除目標產生確認訊息
const deleteDialogMessage = computed(() => {
  if (!deleteTarget.value) {
    return "";
  }

  return `確定要刪除討論版「${deleteTarget.value.name}」嗎？`;
});

// ======================
// 對外提供函式
// ======================

// 讓父元件可以呼叫 fetchBoards
defineExpose({ fetchBoards });

// ======================
// 元件生命週期
// ======================

// 元件載入後取得討論版清單
onMounted(fetchBoards);
</script>

<style scoped>
.admin-card {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
}
</style>
