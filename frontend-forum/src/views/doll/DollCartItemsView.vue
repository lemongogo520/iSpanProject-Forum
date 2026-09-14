<template>
  <v-container fluid class="fill-height bg-grey-darken-4 pa-6">
    <v-row class="fill-height">
      <!-- 左側：紙娃娃預覽區 (佔 4 欄) -->
      <v-col cols="12" md="4" class="d-flex flex-column">
        <v-card
          theme="dark"
          class="flex-grow-1 bg-grey-darken-3 d-flex flex-column"
          rounded="lg"
          elevation="6"
        >
          <v-card-title
            class="text-center font-weight-bold text-cyan-accent-3 pt-4"
          >
            外觀預覽
          </v-card-title>

          <v-sheet
            class="flex-grow-1 mx-4 mb-4 bg-grey-darken-4 rounded-lg d-flex align-center justify-center border"
            style="border-color: #333 !important"
          >
            <DollConvas :equippedItems="equippedItems" :baseUrl="baseUrl" />
          </v-sheet>

          <v-card-actions class="px-4 pb-4 justify-center gap-2">
            <v-btn
              variant="tonal"
              color="grey-lighten-1"
              prepend-icon="mdi-refresh"
              >重置</v-btn
            >
            <v-btn
              variant="tonal"
              color="cyan-accent-3"
              prepend-icon="mdi-dice-multiple"
              >隨機</v-btn
            >
            <v-btn
              variant="flat"
              color="cyan-accent-3"
              class="text-grey-darken-4 font-weight-bold"
              prepend-icon="mdi-content-save"
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
    equippedItems.value[item.category.id] = item;
    console.log("目前試穿狀態", equippedItems.value);
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
