<template>
  <div class="layout">
    <Sidebar />
    <div class="content">
      <div class="header">
        <h1>服裝素材管理</h1>
      </div>

      <!-- 分頁標籤 (利用 router to 跳轉) -->
      <v-tabs color="primary" class="tabs mb-6" model-value="adminitems">
        <v-tab to="/config/doll" value="categories">圖層管理</v-tab>
        <v-tab to="/config/doll/newitem" value="newitem">新增素材</v-tab>
        <v-tab to="/config/doll/adminitems" value="adminitems"
          >商品上下架</v-tab
        >
      </v-tabs>

      <!-- 管理卡片 -->
      <v-card class="bg-white" elevation="2" rounded="lg">
        <v-card-title class="d-flex align-center pa-4 border-b">
          <v-icon start icon="mdi-store-cog-outline" color="primary"></v-icon>
          商品上下架管理
          <v-spacer></v-spacer>

          <!-- 分類選擇下拉選單 -->
          <v-select
            v-model="selectedCategory"
            :items="categories"
            item-title="typeName"
            item-value="id"
            label="選擇圖層分類"
            variant="outlined"
            density="compact"
            hide-details
            class="max-w-xs mr-4"
            style="max-width: 200px"
            color="primary"
          ></v-select>

          <!-- 搜尋列 -->
          <v-text-field
            v-model="search"
            prepend-inner-icon="mdi-magnify"
            label="搜尋商品名稱"
            variant="outlined"
            density="compact"
            hide-details
            class="max-w-xs"
            style="max-width: 250px"
            color="primary"
            :disabled="!selectedCategory"
          ></v-text-field>
        </v-card-title>

        <v-table hover v-if="selectedCategory">
          <thead>
            <tr>
              <th class="text-left" width="100">商品預覽</th>
              <th class="text-left">商品名稱</th>
              <th class="text-left">圖層分類</th>
              <th class="text-left">價格</th>
              <th class="text-center">狀態</th>
              <th class="text-center">上/下架操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredItems" :key="item.id">
              <td>
                <v-avatar
                  rounded
                  size="64"
                  color="grey-lighten-4"
                  class="my-2 border"
                  style="overflow: hidden"
                >
                  <DollConvas
                    v-if="item.filePath"
                    :equippedItems="{ current: item }"
                    :scale="1"
                  />
                  <v-icon v-else color="grey-lighten-1"
                    >mdi-image-outline</v-icon
                  >
                </v-avatar>
              </td>
              <td class="font-weight-medium" style="width: 250px">
                <!-- 編輯模式 -->
                <div v-if="editItemId === item.id" class="d-flex align-center">
                  <v-text-field
                    v-model="editItemName"
                    density="compact"
                    variant="outlined"
                    hide-details
                    class="mr-2"
                    style="min-width: 120px"
                    @keyup.enter="saveName(item)"
                    @keyup.esc="cancelEdit"
                  ></v-text-field>
                  <v-btn
                    icon="mdi-check"
                    color="success"
                    size="x-small"
                    variant="text"
                    @click="saveName(item)"
                  ></v-btn>
                  <v-btn
                    icon="mdi-close"
                    color="error"
                    size="x-small"
                    variant="text"
                    @click="cancelEdit"
                  ></v-btn>
                </div>
                <!-- 閱讀模式 -->
                <div v-else class="d-flex align-center">
                  <span>{{ item.name }}</span>
                  <v-btn
                    icon="mdi-pencil"
                    size="x-small"
                    variant="text"
                    color="grey"
                    class="ml-2"
                    @click="startEdit(item)"
                  ></v-btn>
                </div>
              </td>
              <td>
                <v-chip size="small" variant="tonal" color="primary">
                  {{ categoryNameMap[item.category.id] || "未分類" }}
                </v-chip>
              </td>
              <td>
                <div class="d-flex align-center">
                  <v-icon size="small" color="amber-darken-2" class="mr-1"
                    >mdi-star-four-points</v-icon
                  >
                  <span class="font-weight-bold text-grey-darken-2">{{
                    item.price
                  }}</span>
                </div>
              </td>
              <td class="text-center">
                <v-chip
                  size="small"
                  :color="item.isActive ? 'success' : 'grey'"
                  variant="flat"
                  class="font-weight-bold text-white"
                >
                  {{ item.isActive ? "販售中" : "已下架" }}
                </v-chip>
              </td>
              <td class="text-center">
                <v-switch
                  v-model="item.isActive"
                  @change="handleStatusToggle(item)"
                  color="success"
                  hide-details
                  density="compact"
                  inset
                  class="d-inline-flex"
                ></v-switch>
              </td>
            </tr>
          </tbody>
        </v-table>

        <!-- 當沒有選擇分類時顯示 -->
        <div v-else class="pa-10 text-center text-grey-darken-1">
          <v-icon size="64" color="grey-lighten-2" class="mb-4"
            >mdi-layers-search-outline</v-icon
          >
          <div class="text-h6 font-weight-bold mb-2">請先選擇一個圖層分類</div>
          <div>選擇分類後即可管理該分類下的商品上下架狀態</div>
        </div>

        <!-- 當沒有搜尋結果時顯示 -->
        <div
          v-if="selectedCategory && filteredItems.length === 0"
          class="pa-5 text-center text-grey"
        >
          找不到符合條件的商品
        </div>
      </v-card>

      <!-- 測試用按鈕區塊 -->
      <div class="mt-4 text-right">
        <v-btn
          color="warning"
          prepend-icon="mdi-auto-fix"
          @click="testUnlockAll"
          :loading="isUnlocking"
        >
          測試：全圖鑑一鍵解鎖 (給予所有 DollItem)
        </v-btn>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import Sidebar from "@/components/config/ConfigSidebar.vue";
import DollConvas from "@/components/doll/DollConvas.vue";
import { useAdminDollItems } from "@/services/dollSystemApi/dollAdminItemApi";

const {
  categories,
  fetchCategories,
  adminItems,
  fetchAdminItems,
  updateItemStatus,
  updateItemName,
  unlockAllItems,
} = useAdminDollItems();

// 搜尋關鍵字與選擇的分類
const search = ref("");
const selectedCategory = ref(null);
const editItemId = ref(null);
const editItemName = ref("");
const isUnlocking = ref(false);

// 測試用：無差別全圖鑑解鎖
const testUnlockAll = () => {
  isUnlocking.value = true;
  unlockAllItems()
    .then((res) => {
      alert(`全圖鑑解鎖成功！已從所有分類中抓取並新增了 ${res.count} 件裝備！`);
    })
    .catch((err) => {
      alert(err === "資料庫中沒有任何裝備" ? err : "解鎖失敗，請查看 Console。");
    })
    .finally(() => {
      isUnlocking.value = false;
    });
};

// 初始化抓取所有圖層分類
onMounted(() => {
  fetchCategories();
});

// 當分類改變時，呼叫後端 API 抓取該分類的所有商品
watch(selectedCategory, (newVal) => {
  if (newVal) {
    fetchAdminItems(newVal);
  } else {
    adminItems.value = [];
  }
});

// 快速查找分類名稱用的 Map
const categoryNameMap = computed(() => {
  const map = {};
  categories.value.forEach((c) => {
    map[c.id] = c.typeName;
  });
  return map;
});

// 簡易過濾邏輯：只對已抓取的 adminItems 進行關鍵字搜尋
const filteredItems = computed(() => {
  if (!search.value) return adminItems.value;

  return adminItems.value.filter((item) => item.name.includes(search.value));
});

// 處理編輯商品名稱
const startEdit = (item) => {
  editItemId.value = item.id;
  editItemName.value = item.name;
};

const cancelEdit = () => {
  editItemId.value = null;
  editItemName.value = "";
};

const saveName = (item) => {
  // 防呆：名稱不能空白
  if (!editItemName.value.trim()) {
    alert("商品名稱不能為空！");
    return;
  }

  // 呼叫 API 更新名稱
  updateItemName(item.id, editItemName.value)
    .then(() => {
      // API 成功後，更新前端的顯示資料
      item.name = editItemName.value;
      // 關閉編輯模式
      editItemId.value = null;
      editItemName.value = "";
    })
    .catch((error) => {
      alert("更新名稱失敗，請檢查連線狀態");
    });
};

// 處理狀態切換
const handleStatusToggle = (item) => {
  updateItemStatus(item.id, item.isActive)
    .then(() => {
      // 成功更新，畫面上的 switch 已經自動切換了
    })
    .catch((error) => {
      // 若後端更新失敗，把開關狀態切回來
      item.isActive = !item.isActive;
    });
};
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.content {
  flex-grow: 1;
  padding: 40px;
  background-color: #ffffff;
  max-width: 1000px;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.header h1 {
  margin: 0;
}

.tabs {
  border-bottom: 1px solid #ddd;
}
</style>
