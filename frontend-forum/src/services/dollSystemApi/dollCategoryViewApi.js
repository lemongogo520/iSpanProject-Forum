import { onMounted, ref } from "vue";
import httpClient from "../httpClient";
import { listAllCategories } from "./dollAllCategoryApi";

export function useDollCategories() {

  const { categories, fetchCategories } = listAllCategories();
  // 控制彈窗顯示與隱藏
  const isModalVisible = ref(false);
  // 存放當前被點擊選中的分類資料
  const selectedCategory = ref(null);
  // 點擊列表項目時觸發
  const openEditModal = (category) => {
    selectedCategory.value = category;
    isModalVisible.value = true;
  };

  // Snackbar 狀態
  const isSnackbarVisible = ref(false);
  const snackbarMessage = ref("");
  const snackbarColor = ref("success");

  function showSnackBar(message, type = "success") {
    snackbarMessage.value = message;
    snackbarColor.value = type;
    isSnackbarVisible.value = true;
  }


  // 接收子組件傳回來的儲存動作
  const saveCategory = (updatedData) => {
    httpClient({
      method: "patch",
      url: `/api/dollcategory/${updatedData.id}`,
      data: updatedData,
    })
      .then(() => {
        isModalVisible.value = false;
        fetchCategories(); // 重新拉取最新列表
        showSnackBar("圖層更新成功！", "success");
      })
      .catch((err) => {
        showSnackBar("更新失敗", "error");
      });
  };

  return {
    categories,
    fetchCategories,
    isModalVisible,
    selectedCategory,
    openEditModal,
    saveCategory,
    isSnackbarVisible,
    snackbarMessage,
    snackbarColor
  };
}
