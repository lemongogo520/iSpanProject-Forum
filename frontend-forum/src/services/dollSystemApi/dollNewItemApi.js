import { onMounted, ref } from "vue";
import httpClient from "../httpClient";

export function addNewDollItem() {
  //上傳表單相關變數
  const categories = ref([]);
  const isSubmit = ref(false);
  const formData = ref({
    categoryId: null,
    name: "",
    price: 0,
  });
  const selectedFile = ref(null);

  //初始化
  onMounted(async () => {
    try {
      const res = await httpClient.get("/api/dollcategory/all");
      categories.value = res.data;
    } catch (error) {
      console.error("載入圖層分類失敗", error);
    }
  });

  //送出表單邏輯
  const submitItem = async () => {
    //驗證
    if (
      !formData.value.categoryId ||
      !formData.value.name ||
      !selectedFile.value
    ) {
      alert("請填寫完整資訊後再上傳");
      return;
    }

    isSubmit.value = true;
    const payload = new FormData();

    // 封裝 JSON 資料為 Blob
    const itemData = {
      categoryId: formData.value.categoryId,
      name: formData.value.name,
      price: formData.value.price,
      isActive: true,
    };

    payload.append(
      "itemData",
      new Blob([JSON.stringify(itemData)], { type: "application/json" }),
    );

    //處理圖片檔案
    const fileUpload = Array.isArray(selectedFile.value)
      ? selectedFile.value[0]
      : selectedFile.value;
    payload.append("file", fileUpload);

    //發送API
    try {
      const response = await httpClient({
        method: "post",
        url: "/api/dollitems/add",
        data: payload,
      });

      alert("部件新增成功!");

      formData.value.name = "";
      formData.value.price = 0;
      selectedFile.value = null;
    } catch (error) {
      alert("新增失敗，請檢察檔案格式!");
    } finally {
      isSubmit.value = false;
    }
  };

  return {
    categories,
    isSubmit,
    formData,
    selectedFile,
    submitItem,
  };
}
