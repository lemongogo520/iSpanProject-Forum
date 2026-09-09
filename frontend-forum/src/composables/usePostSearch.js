import { ref } from "vue";
import { searchPosts } from "@/services/postService";

export const usePostSearch = () => {
  // 每次呼叫都建立自己的搜尋狀態
  const searchKeyword = ref("");
  const activeKeyword = ref("");
  const searchResults = ref([]);
  const searchLoading = ref(false);
  const searchError = ref("");

  const searchPage = ref(1);
  const searchPageSize = 10;
  const searchTotalPages = ref(0);
  const searchTotalElements = ref(0);

  // 取得搜尋結果
  const fetchSearchResults = async () => {
    if (!activeKeyword.value || searchLoading.value) return;

    searchLoading.value = true;
    searchError.value = "";

    try {
      const response = await searchPosts(
        activeKeyword.value,
        searchPage.value - 1,
        searchPageSize,
      );

      searchResults.value = response.data.content;
      searchTotalPages.value = response.data.totalPages;
      searchTotalElements.value = response.data.totalElements;
    } catch (error) {
      console.error("搜尋文章失敗：", error);
      searchError.value = "搜尋失敗，請稍後再試";
      searchResults.value = [];
      searchTotalPages.value = 0;
      searchTotalElements.value = 0;
    } finally {
      searchLoading.value = false;
    }
  };

  // 清除搜尋
  const clearSearch = () => {
    if (searchLoading.value) return;

    searchKeyword.value = "";
    activeKeyword.value = "";
    searchResults.value = [];
    searchPage.value = 1;
    searchTotalPages.value = 0;
    searchTotalElements.value = 0;
    searchError.value = "";
  };

  // 按搜尋或 Enter
  const handleSearch = () => {
    if (searchLoading.value) return;

    const keyword = (searchKeyword.value ?? "").trim();

    if (!keyword) {
      clearSearch();
      return;
    }

    activeKeyword.value = keyword;
    searchPage.value = 1;
    fetchSearchResults();
  };

  // 提供給頁面使用
  return {
    searchKeyword,
    activeKeyword,
    searchResults,
    searchLoading,
    searchError,
    searchPage,
    searchTotalPages,
    searchTotalElements,
    fetchSearchResults,
    clearSearch,
    handleSearch,
  };
};