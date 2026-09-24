<template>
  <v-container
    fluid
    class="fill-height pa-6"
    style="background: var(--color-background)"
  >
    <v-row class="fill-height">
      <!-- 左側：紙娃娃預覽區 (佔 4 欄) -->
      <v-col cols="12" md="3" class="d-flex flex-column">
        <v-card
          class="flex-grow-1 d-flex flex-column auth-card"
          rounded="lg"
          elevation="6"
        >
          <v-card-title class="text-center font-weight-bold pt-4 auth-title">
            我的衣櫃
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

          <!-- ================= 新增：色相偏移拖拉條 ================= -->
          <div
            v-if="dyeableItems.length > 0"
            class="px-4 w-100 overflow-y-auto"
            style="max-height: 200px"
          >
            <div
              v-for="item in dyeableItems"
              :key="item.category.id"
              class="mb-3"
            >
              <div class="d-flex align-center justify-space-between mb-1">
                <span class="text-caption font-weight-bold text-grey-darken-2">
                  {{ item.category.typeName }} 顏色 (色相)
                </span>
                <v-btn
                  size="x-small"
                  variant="text"
                  color="grey"
                  @click="resetColor(item.category.id)"
                  >還原</v-btn
                >
              </div>

              <!-- 新增：指定顏色覆蓋 (Tint) -->
              <div class="d-flex align-center justify-space-between mb-1 mt-2">
                <span class="text-caption font-weight-bold text-grey-darken-2">
                  覆蓋顏色 (支援黑灰白)
                </span>
              </div>
              <div class="d-flex align-center gap-2 mb-2">
                <!-- 黑色按鈕：對應 Integer 的 0 -->
                <v-btn
                  size="small"
                  color="black"
                  @click="updateColor(item.category.id, 0)"
                  >黑色</v-btn
                >
                <!-- 紅色按鈕：對應 Integer 的 16711680 (0xFF0000) -->
                <v-btn
                  size="small"
                  color="red"
                  @click="updateColor(item.category.id, 16711680)"
                  >紅色</v-btn
                >

                <!-- 完整的色碼選擇器 (Vuetify 版) -->
                <v-menu :close-on-content-click="false" location="bottom">
                  <template v-slot:activator="{ props }">
                    <v-btn
                      size="small"
                      icon="mdi-palette"
                      variant="tonal"
                      color="primary"
                      v-bind="props"
                      title="選擇更多顏色"
                    ></v-btn>
                  </template>
                  <v-card>
                    <v-color-picker
                      :model-value="intToHex(item.color)"
                      @update:model-value="
                        (val) => updateColor(item.category.id, hexToInt(val))
                      "
                      hide-inputs
                      show-swatches
                      elevation="0"
                    ></v-color-picker>
                  </v-card>
                </v-menu>
              </div>
            </div>
          </div>
          <!-- ========================================================== -->

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
      <v-col cols="12" md="9" class="d-flex flex-column">
        <!-- 載入新拆分的元件，並監聽 equip-item 事件 -->
        <DollItemSelector @equip-item="equipItem" />
      </v-col>
    </v-row>
  </v-container>
</template>
<script setup>
import { ref, onMounted, computed } from "vue";
import DollConvas from "@/components/doll/DollConvas.vue";
import DollItemSelector from "@/components/doll/DollItemSelector.vue";
import httpClient from "@/services/httpClient";

const baseUrl = "http://localhost:8080/sprites/";

const equippedItems = ref({});

function loadEquippedItems() {
  httpClient({
    method: "get",

    url: "/api/dollitem/equipped",
  })
    .then((response) => {
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
    })
    .catch((error) => {
      console.error("無法取得裝備資料", error);
    });
}

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

function saveEquippedItems() {
  isSaving.value = true;
  const payload = Object.values(equippedItems.value).map((item) => ({
    itemId: item.id,
    color: item.color || null,
  }));

  httpClient({
    method: "put",
    url: "/api/dollitem/newequip",
    data: payload, // 👈 傳送符合後端 DTO 的物件陣列
  })
    .then((response) => {
      if (response.status === 200) {
        alert("套裝儲存成功！");
      }
    })
    .catch((error) => {
      console.error("儲存套裝失敗", error);
      alert("儲存失敗，請確認連線或登入狀態");
    })
    .finally(() => {
      isSaving.value = false;
    });
}

// ================= 色相偏移相關邏輯 =================
const allowedDyeCategories = ["頭髮", "上衣", "下裝"];

const dyeableItems = computed(() => {
  return Object.values(equippedItems.value).filter((item) => {
    if (!item || !item.category) return false;
    return allowedDyeCategories.includes(item.category.typeName);
  });
});

// 色碼轉換工具：將 10 進位整數轉成 #RRGGBB 供 input type="color" 使用
function intToHex(intValue) {
  if (intValue === undefined || intValue === null) return "#ffffff";
  return "#" + intValue.toString(16).padStart(6, "0");
}

// 色碼轉換工具：將 #RRGGBB 轉成 10 進位整數供後端與 PixiJS 使用
function hexToInt(hexString) {
  if (!hexString) return null;
  return parseInt(hexString.replace("#", ""), 16);
}

function updateColor(categoryId, colorInt) {
  if (equippedItems.value[categoryId]) {
    equippedItems.value[categoryId].color = colorInt;
    equippedItems.value = { ...equippedItems.value };
  }
}

function resetColor(categoryId) {
  if (equippedItems.value[categoryId]) {
    equippedItems.value[categoryId].color = null;
    equippedItems.value = { ...equippedItems.value };
  }
}
// =================================================

onMounted(() => {
  loadEquippedItems();
});
</script>
<style scoped>
/* 讓 Slider 的軌道變成彩虹色，對應 0~360 度的色相 */
.rainbow-slider :deep(.v-slider-track__background) {
  background: linear-gradient(
    to right,
    #f00 0%,
    #ff0 17%,
    #0f0 33%,
    #0ff 50%,
    #00f 67%,
    #f0f 83%,
    #f00 100%
  ) !important;
  opacity: 1 !important;
}
.rainbow-slider :deep(.v-slider-track__fill) {
  background: transparent !important; /* 隱藏左側的填滿顏色 */
}
.gap-2 {
  gap: 8px;
}
</style>
