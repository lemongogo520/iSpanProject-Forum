import { ref } from "vue";
import httpClient from "../../../service/httpClient";
import { listAllCategories } from "./DollAllCategoryApi";

export function DollFittingRoom(){

    const {categories,fetchCategories}=listAllCategories();
    //宣告變數
    const currentItems = ref([]);
    const activeTab = ref();
    const equippedItems = ref({});

    
}