import { ref } from "vue";
import httpClient from "../../../service/httpClient";

export function listAllCategories() {
  const categories = ref([]);

  function fetchCategories() {
    httpClient({
      method: "get",

      url: "/api/dollcategory/all",
    })
      .then((response) => {
        categories.value = response.data;
        console.log(response);
      })

      .catch((error) => console.error("取得圖層失敗", error));
  }
  return {categories,fetchCategories};
}
