import { ref } from "vue";
import httpClient from "../httpClient";
import { listAllCategories } from "@/service/DollSystemApi/DollAllCategoryApi";

export function DollFittingRoom(){

    const {categories,fetchCategories}=listAllCategories();
    //宣告變數
    const currentItems = ref([]);
    const activeTab = ref();
    const equippedItems = ref({});

    
}