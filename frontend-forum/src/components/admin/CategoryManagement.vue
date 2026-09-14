<template>
  <!-- 新增／編輯類別表單 -->
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

  <!-- 現有類別列表 -->
  <v-card class="admin-card pa-4" elevation="0">
    <div class="d-flex justify-space-between align-center mb-3">
      <v-card-title class="px-0">現有類別</v-card-title>

      <v-btn
        size="small"
        variant="outlined"
        prepend-icon="mdi-refresh"
        @click="fetchCategories"
      >
        重新整理
      </v-btn>
    </div>

    <!-- 載入進度條 -->
    <v-progress-linear
      v-if="categoryLoading"
      indeterminate
      color="primary"
      class="mb-4"
    />

    <v-list v-if="categories.length > 0">
      <v-list-item
        v-for="category in categories"
        :key="category.id"
        border
        class="mb-2 rounded"
      >
        <v-list-item-title>{{ category.name }}</v-list-item-title>

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
              @click="requestDeleteCategory(category)"
            >
              刪除
            </v-btn>
          </div>
        </template>
      </v-list-item>
    </v-list>

    <!-- 沒有類別且載入完成時顯示 -->
    <div
      v-else-if="!categoryLoading"
      class="text-medium-emphasis text-center py-5"
    >
      目前沒有類別
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
// Emits
// ======================

// categories-updated：將最新類別清單傳給父元件
// categories-changed：通知父元件重新整理討論版
const emit = defineEmits(["categories-updated", "categories-changed"]);

// ======================
// 通知功能
// ======================

const { showMessage } = useNotification();

// ======================
// 類別狀態
// ======================

// 類別清單
const categories = ref([]);

// 類別資料載入狀態
const categoryLoading = ref(false);

// 是否處於類別編輯模式
const isCategoryEditMode = ref(false);

// ======================
// 類別表單
// ======================

// 新增或修改類別時使用的表單資料
const categoryForm = ref({
  id: null,
  name: "",
  rank: 0,
});

// ======================
// 刪除視窗狀態
// ======================

// 是否顯示刪除確認視窗
const deleteDialog = ref(false);

// 準備刪除的類別
const deleteTarget = ref(null);

// 刪除請求載入狀態
const deleteLoading = ref(false);

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
// 取得所有類別
// ======================

const fetchCategories = () => {
  categoryLoading.value = true;

  return httpClient({
    method: "get",
    url: "/api/categories",
  })
    .then((response) => {
      categories.value = response.data;

      // 將最新類別清單傳給父元件
      emit("categories-updated", response.data);
    })
    .catch((error) => {
      showMessage(getErrorMessage(error, "取得類別失敗"), "error");
    })
    .finally(() => {
      categoryLoading.value = false;
    });
};

// ======================
// 開始編輯類別
// ======================

const startEditCategory = (category) => {
  categoryForm.value = {
    id: category.id,
    name: category.name ?? "",
    rank: category.rank ?? 0,
  };

  isCategoryEditMode.value = true;

  // 將畫面移動到上方的編輯表單
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

const handleCategorySubmit = () => {
  const name = categoryForm.value.name.trim();

  // 驗證類別名稱是否有填寫
  if (!name) {
    showMessage("請填寫類別名稱", "warning");
    return;
  }

  const editing = isCategoryEditMode.value;

  return httpClient({
    method: editing ? "put" : "post",
    url: editing
      ? `/api/categories/${categoryForm.value.id}`
      : "/api/categories",
    data: {
      name,
      rank: categoryForm.value.rank ?? 0,
    },
  })
    .then(() => {
      showMessage(
        editing ? "類別修改成功" : "類別新增成功",
        "success",
      );

      resetCategoryForm();
      return fetchCategories();
    })
    .then(() => {
      // 通知父元件重新整理討論版
      emit("categories-changed");
    })
    .catch((error) => {
      showMessage(getErrorMessage(error, "儲存類別失敗"), "error");
    });
};

// ======================
// 開啟刪除確認視窗
// ======================

const requestDeleteCategory = (category) => {
  deleteTarget.value = category;
  deleteDialog.value = true;
};

// ======================
// 關閉刪除確認視窗
// ======================

const closeDeleteDialog = () => {
  // 刪除請求進行中時禁止關閉視窗
  if (deleteLoading.value) {
    return;
  }

  deleteDialog.value = false;
  deleteTarget.value = null;
};

// ======================
// 確認刪除類別
// ======================

const confirmDelete = () => {
  const category = deleteTarget.value;

  if (!category) {
    return;
  }

  deleteLoading.value = true;

  return httpClient({
    method: "delete",
    url: `/api/categories/delete/${category.id}`,
  })
    .then(() => {
      // 如果正在編輯被刪除的類別，就清空表單
      if (categoryForm.value.id === category.id) {
        resetCategoryForm();
      }

      showMessage("類別刪除成功", "success");
      deleteDialog.value = false;
      deleteTarget.value = null;

      return fetchCategories();
    })
    .then(() => {
      // 通知父元件重新整理討論版
      emit("categories-changed");
    })
    .catch((error) => {
      showMessage(
        getErrorMessage(
          error,
          "刪除類別失敗，請確認類別下是否仍有討論版",
        ),
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

const deleteDialogMessage = computed(() => {
  if (!deleteTarget.value) {
    return "";
  }

  return `確定要刪除類別「${deleteTarget.value.name}」嗎？`;
});

// ======================
// 元件生命週期
// ======================

// 元件載入完成後取得類別清單
onMounted(fetchCategories);
</script>

<style scoped>
.admin-card {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
}
</style>