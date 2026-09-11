<template>
  <v-container class="py-6">
    <!-- 頁面標題 -->
    <div class="mb-6">
      <h2 class="mb-1">系統管理</h2>

      <div class="text-body-2 text-medium-emphasis">管理論壇類別與討論版</div>
    </div>

    <!-- 頁籤 -->
    <v-tabs v-model="activeTab" color="primary" class="mb-5">
      <v-tab value="categories">
        <v-icon icon="mdi-shape-outline" class="me-2" />

        類別管理
      </v-tab>

      <v-tab value="boards">
        <v-icon icon="mdi-forum-outline" class="me-2" />

        討論版管理
      </v-tab>
    </v-tabs>
    <v-window v-model="activeTab">
      <!-- ====================== -->
      <!-- 類別管理 -->
      <!-- ====================== -->

      <v-window-item value="categories">
        <!-- 類別新增／編輯表單 -->
        <v-card class="admin-card pa-4 mb-5" elevation="0">
          <div class="d-flex justify-space-between align-center mb-2">
            <v-card-title class="px-0">
              {{
                isCategoryEditMode
                  ? `編輯類別（ID：${categoryForm.id}）`
                  : "新增類別"
              }}
            </v-card-title>

            <v-btn
              v-if="isCategoryEditMode"
              size="small"
              color="grey"
              variant="outlined"
              @click="resetCategoryForm"
            >
              取消編輯
            </v-btn>
          </div>

          <v-card-text class="px-0">
            <v-text-field
              v-model="categoryForm.name"
              label="類別名稱"
              variant="outlined"
              density="comfortable"
              clearable
            />

            <v-text-field
              v-model.number="categoryForm.rank"
              label="排序"
              type="number"
              variant="outlined"
              density="comfortable"
            />

            <v-btn
              :color="isCategoryEditMode ? 'warning' : 'success'"
              prepend-icon="mdi-content-save"
              block
              @click="handleCategorySubmit"
            >
              {{ isCategoryEditMode ? "確認修改" : "新增類別" }}
            </v-btn>
          </v-card-text>
        </v-card>

        <!-- 類別列表 -->
        <v-card class="admin-card pa-4" elevation="0">
          <div class="d-flex justify-space-between align-center mb-3">
            <v-card-title class="px-0"> 現有類別 </v-card-title>

            <v-btn
              size="small"
              variant="outlined"
              prepend-icon="mdi-refresh"
              @click="fetchCategories"
            >
              重新整理
            </v-btn>
          </div>

          <v-progress-linear
            v-if="categoryLoading"
            indeterminate
            color="primary"
            class="mb-4"
          />

          <v-list v-if="categories.length > 0">
            <v-list-item
              v-for="(category, index) in categories"
              :key="category.id"
              border
              class="mb-2 rounded"
            >
              <v-list-item-title>
                {{ category.name }}
              </v-list-item-title>

              <v-list-item-subtitle>
                ID：{{ category.id }} ｜排序：{{ category.rank }}
              </v-list-item-subtitle>

              <template #append>
                <div class="d-flex ga-2">
                  <v-btn
                    size="small"
                    color="primary"
                    variant="tonal"
                    @click="startEditCategory(category)"
                  >
                    編輯
                  </v-btn>

                  <v-btn
                    size="small"
                    color="error"
                    variant="tonal"
                    @click="requestDelete('category', category)"
                  >
                    刪除
                  </v-btn>
                </div>
              </template>
            </v-list-item>
          </v-list>

          <div
            v-else-if="!categoryLoading"
            class="text-medium-emphasis text-center py-5"
          >
            目前沒有類別
          </div>
        </v-card>
      </v-window-item>

      <!-- ====================== -->
      <!-- 討論版管理 -->
      <!-- ====================== -->

      <v-window-item value="boards">
        <!-- 討論版新增／編輯表單 -->
        <v-card class="admin-card pa-4 mb-5" elevation="0">
          <div class="d-flex justify-space-between align-center mb-2">
            <v-card-title class="px-0">
              {{
                isBoardEditMode
                  ? `編輯討論版（ID：${boardForm.id}）`
                  : "新增討論版"
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

        <!-- 討論版列表 -->
        <v-card class="admin-card pa-4" elevation="0">
          <div class="d-flex justify-space-between align-center mb-3">
            <v-card-title class="px-0"> 現有討論版 </v-card-title>

            <v-btn
              size="small"
              variant="outlined"
              prepend-icon="mdi-refresh"
              @click="fetchBoards"
            >
              重新整理
            </v-btn>
          </div>

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
              <v-list-item-title>
                {{ board.name }}
              </v-list-item-title>

              <v-list-item-subtitle>
                類別：{{ board.category?.name ?? "未分類" }} ｜排序：{{
                  board.rank
                }}
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
                    @click="requestDelete('board', board)"
                  >
                    刪除
                  </v-btn>
                </div>
              </template>
            </v-list-item>
          </v-list>

          <div
            v-else-if="!boardLoading"
            class="text-medium-emphasis text-center py-5"
          >
            目前沒有討論版
          </div>
        </v-card>
      </v-window-item>
    </v-window>
  </v-container>
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
// 頁籤
// ======================

const activeTab = ref("categories");

const { showMessage } = useNotification();
// ======================
// 類別狀態
// ======================
const categories = ref([]);
const categoryLoading = ref(false);
const isCategoryEditMode = ref(false);
// ======================
// 刪除狀態
// ======================
const deleteDialog = ref(false);
const deleteTarget = ref(null);
const deleteLoading = ref(false);

const categoryForm = ref({
  id: null,
  name: "",
  rank: 0,
});

// ======================
// 討論版狀態
// ======================

const boards = ref([]);
const boardLoading = ref(false);
const isBoardEditMode = ref(false);

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

const getErrorMessage = (error, fallback) => {
  const data = error.response?.data;

  if (typeof data === "string") {
    return data;
  }

  return data?.message ?? fallback;
};

// ======================
// 取得所有類別
// ======================

const fetchCategories = async () => {
  categoryLoading.value = true;

  try {
    const response = await httpClient.get("/api/categories");

    categories.value = response.data;
  } catch (error) {
    console.error("取得類別失敗：", error);

    showMessage(
      getErrorMessage(error, "刪除類別失敗，請確認類別下是否仍有討論版"),
      "error",
    );
  } finally {
    categoryLoading.value = false;
  }
};

// ======================
// 編輯類別
// ======================

const startEditCategory = (category) => {
  categoryForm.value = {
    id: category.id,
    name: category.name ?? "",
    rank: category.rank ?? 0,
  };

  isCategoryEditMode.value = true;

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
};

// ======================
// 重設類別表單
// ======================

const resetCategoryForm = () => {
  categoryForm.value = {
    id: null,
    name: "",
    rank: 0,
  };

  isCategoryEditMode.value = false;
};

// ======================
// 新增或修改類別
// ======================

const handleCategorySubmit = async () => {
  const name = categoryForm.value.name.trim();

  if (!name) {
    showMessage("請填寫類別名稱", "warning");
    return;
  }

  const data = {
    name,
    rank: categoryForm.value.rank ?? 0,
  };

  try {
    if (isCategoryEditMode.value) {
      await httpClient.put(`/api/categories/${categoryForm.value.id}`, data);

      showMessage("類別修改成功", "success");
    } else {
      await httpClient.post("/api/categories", data);

      showMessage("類別新增成功", "success");
    }

    resetCategoryForm();
    await fetchCategories();
    await fetchBoards();
  } catch (error) {
    console.error("儲存類別失敗：", error);

    showMessage(getErrorMessage(error, "儲存類別失敗"), "error");
  }
};

// ======================
// 分類、看板刪除確認
// ======================
const requestDelete = (type, item) => {
  deleteTarget.value = {
    type,
    item,
  };

  deleteDialog.value = true;
};

const closeDeleteDialog = () => {
  if (deleteLoading.value) {
    return;
  }

  deleteDialog.value = false;
  deleteTarget.value = null;
};

const confirmDelete = async () => {
  const target = deleteTarget.value;

  if (!target) {
    return;
  }

  deleteLoading.value = true;

  try {
    if (target.type === "category") {
      await httpClient.delete(`/api/categories/delete/${target.item.id}`);

      if (categoryForm.value.id === target.item.id) {
        resetCategoryForm();
      }

      showMessage("類別刪除成功", "success");

      await fetchCategories();
      await fetchBoards();
    } else {
      await httpClient.delete(`/api/boards/delete/${target.item.id}`);

      if (boardForm.value.id === target.item.id) {
        resetBoardForm();
      }

      showMessage("討論版刪除成功", "success");

      await fetchBoards();
    }

    deleteDialog.value = false;
    deleteTarget.value = null;
  } catch (error) {
    console.error("刪除失敗：", error);

    const fallback =
      target.type === "category"
        ? "刪除類別失敗，請確認類別下是否仍有討論版"
        : "刪除討論版失敗，請確認討論版內是否仍有文章";

    showMessage(getErrorMessage(error, fallback), "error");
  } finally {
    deleteLoading.value = false;
  }
};

const deleteDialogMessage = computed(() => {
  const target = deleteTarget.value;

  if (!target) {
    return "";
  }

  if (target.type === "category") {
    return `確定要刪除類別「${target.item.name}」嗎？`;
  }

  return `確定要刪除討論版「${target.item.name}」嗎？`;
});
// ======================
// 取得所有討論版
// ======================
const fetchBoards = async () => {
  boardLoading.value = true;

  try {
    const response = await httpClient.get("/api/boards");

    boards.value = response.data;
  } catch (error) {
    console.error("取得討論版失敗：", error);

    alert(getErrorMessage(error, "取得討論版失敗"));
  } finally {
    boardLoading.value = false;
  }
};

// ======================
// 編輯討論版
// ======================

const startEditBoard = (board) => {
  boardForm.value = {
    id: board.id,
    categoryId: board.category?.id ?? null,
    name: board.name ?? "",
    description: board.description ?? "",
    rank: board.rank ?? 0,
  };

  isBoardEditMode.value = true;

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
};

// ======================
// 重設討論版表單
// ======================

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

const createBoard = async () => {
  await httpClient.post("/api/board", {
    category: {
      id: boardForm.value.categoryId,
    },
    name: boardForm.value.name.trim(),
    description: boardForm.value.description.trim(),
    rank: boardForm.value.rank ?? 0,
  });
};

// ======================
// 修改討論版
// ======================

const updateBoard = async () => {
  await httpClient.put(`/api/boards/${boardForm.value.id}`, {
    name: boardForm.value.name.trim(),
    description: boardForm.value.description.trim(),
    rank: boardForm.value.rank ?? 0,
  });
};

// ======================
// 新增或修改討論版
// ======================

const handleBoardSubmit = async () => {
  if (!isBoardEditMode.value && !boardForm.value.categoryId) {
    showMessage("請選擇所屬類別", "warning");
    return;
  }

  if (!boardForm.value.name.trim()) {
    showMessage("請輸入討論版名稱", "warning");
    return;
  }

  try {
    if (isBoardEditMode.value) {
      await updateBoard();
      alert("討論版修改成功");
    } else {
      await createBoard();
      alert("討論版新增成功");
    }

    resetBoardForm();
    await fetchBoards();
  } catch (error) {
    console.error("儲存討論版失敗：", error);

    alert(getErrorMessage(error, "儲存討論版失敗"));
  }
};
// ======================
// 初始載入
// ======================

onMounted(async () => {
  await Promise.all([fetchCategories(), fetchBoards()]);
});
</script>

<style scoped>
.admin-card {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
}
</style>
