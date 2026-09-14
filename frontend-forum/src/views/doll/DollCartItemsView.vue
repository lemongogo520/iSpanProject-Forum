<template>
  <v-container
    fluid
    class="fill-height pa-6"
    style="background: var(--color-background)"
  >
    <v-row class="fill-height">
      <!-- 左側：紙娃娃預覽區 (佔 4 欄) -->
      <v-col cols="12" md="4" class="d-flex flex-column">
        <v-card
          class="flex-grow-1 d-flex flex-column auth-card"
          rounded="lg"
          elevation="6"
        >
          <v-card-title class="text-center font-weight-bold pt-4 auth-title">
            外觀預覽
          </v-card-title>

          <v-sheet
            class="flex-grow-1 mx-4 mb-4 rounded-lg d-flex align-center justify-center border"
            style="
              background: var(--color-background-light);
              border-color: var(--color-border) !important;
            "
          >
            <DollConvas :equippedItems="equippedItems" :baseUrl="baseUrl" />
          </v-sheet>

          <v-card-actions class="px-4 pb-4 justify-center gap-2">
            <v-btn
              variant="outlined"
              style="
                color: var(--color-text);
                border-color: var(--color-border);
              "
              prepend-icon="mdi-refresh"
              >重置</v-btn
            >

            <v-btn
              variant="flat"
              class="auth-button font-weight-bold"
              prepend-icon="mdi-content-save"
              @click="saveEquippedItems"
              :loading="isSaving"
              >儲存套裝</v-btn
            >
          </v-card-actions>
        </v-card>
      </v-col>

      <!-- 右側：部位分類與裝備選擇 (佔 8 欄) -->
      <v-col cols="12" md="8" class="d-flex flex-column">
        <!-- 載入新拆分的元件，並監聽 equip-item 事件 -->
        <DollItemSelector @equip-item="equipItem" />
      </v-col>
    </v-row>
  </v-container>
</template>
<script setup>
import { ref, onMounted } from "vue";
import DollConvas from "@/components/doll/DollConvas.vue";
import DollItemSelector from "@/components/doll/DollItemSelector.vue";
import httpClient from "@/services/httpClient";

const baseUrl = "http://localhost:8080/sprites/";

const equippedItems = ref({});

// 取得使用者目前的穿戴紀錄
const loadEquippedItems = async () => {
  try {
    const response = await httpClient.get("/api/dollitem/equipped");
    const items = response.data;

    if (items && items.length > 0) {
      // 把從後端抓到的陣列資料，轉換為字典格式 (key: category.id) 塞進 equippedItems
      const newEquipped = {};
      items.forEach((item) => {
        if (item && item.category) {
          newEquipped[item.category.id] = item;
        }
      });
      equippedItems.value = newEquipped;
    } else {
      console.log("目前無穿著紀錄，顯示後端提供的預設裝備");
    }
  } catch (error) {
    console.error("無法取得裝備資料", error);
  }
};

const equipItem = (item) => {
  if (item?.category?.id) {
    const categoryId = item.category.id;
    // 如果目前已經穿著這件裝備，再次點擊就是嘗試「脫下」
    if (equippedItems.value[categoryId]?.id === item.id) {
      // 防呆：如果是必須穿戴的部位 (身體、頭)，禁止脫下成空白
      if (item.category.isRequired) {
        console.warn(`部位 [${item.category.typeName}] 為必須裝備，無法脫下！`);
        // 這裡未來可以加個 v-snackbar 提示使用者
        return;
      }

      delete equippedItems.value[categoryId];
      console.log(`已脫下裝備: ${item.name}`);
    } else {
      // 如果點擊的是「不同」的裝備，就是直接覆蓋掉舊的 (更換)
      equippedItems.value[categoryId] = item;
      console.log(`穿上裝備: ${item.name}`);
    }
  }
};

const isSaving = ref(false);

const saveEquippedItems = async () => {
  try {
    isSaving.value = true;

    // 將 { 1: {id: 5, ...}, 2: {id: 8, ...} } 轉成只有 ID 的陣列 [5, 8]
    const itemIds = Object.values(equippedItems.value).map((item) => item.id);

    // 發送 PUT 請求給後端
    const response = await httpClient.put("/api/dollitem/newequip", itemIds);

    if (response.status === 200) {
      alert("套裝儲存成功！");
    }
  } catch (error) {
    console.error("儲存套裝失敗", error);
    alert("儲存失敗，請確認連線或登入狀態");
  } finally {
    isSaving.value = false;
  }
};

onMounted(() => {
  loadEquippedItems();
});
</script>
<style scoped>
.gap-2 {
  gap: 8px;
}
</style>
