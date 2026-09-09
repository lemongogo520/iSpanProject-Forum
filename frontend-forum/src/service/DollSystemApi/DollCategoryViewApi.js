import { onMounted, ref } from "vue";
import httpClient from "../../../service/httpClient";
import { listAllCategories } from "./DollAllCategoryApi";

export function useDollCategories() {
    
  const {categories,fetchCategories}=listAllCategories();
  // 控制彈窗顯示與隱藏
  const isModalVisible = ref(false);
  // 存放當前被點擊選中的分類資料
  const selectedCategory = ref(null);
  // 點擊列表項目時觸發
  const openEditModal = (category) => {
    selectedCategory.value = category;
    isModalVisible.value = true;
  };

  // 接收子組件傳回來的儲存動作
  const saveCategory = (updatedData) => {
    console.log("準備發送給後端的更新資料:", updatedData);

    httpClient({
      method: "patch",
      url: `/api/dollcategory/${updatedData.id}`,
      data: updatedData,
    })
      .then(() => {
        isModalVisible.value = false;
        fetchCategories(); // 重新拉取最新列表
      })
      .catch((err) => console.error(err));
  };

  return {
    categories,
    fetchCategories,
    isModalVisible,
    selectedCategory,
    openEditModal,
    saveCategory,
  };
}
