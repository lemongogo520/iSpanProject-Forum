<template>
  <v-snackbar
    v-model="isSnackbarVisible"
    :timeout="1000"
    color="error"
    location="top"
  >
    {{ snackbarMessage }}
  </v-snackbar>
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

          <v-card-actions class="px-4 pb-4 justify-center gap-2">
            <v-btn
              variant="outlined"
              style="
                color: var(--color-text);
                border-color: var(--color-border);
              "
              @click="resetEquipItems"
              prepend-icon="mdi-refresh"
              >重置</v-btn
            >

            <v-btn
              variant="outlined"
              style="
                color: var(--color-text);
                border-color: var(--color-border);
              "
              @click="randomEquipItems"
              prepend-icon="mdi-refresh"
              >隨機</v-btn
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
import { ref, onMounted } from "vue";
import DollConvas from "@/components/doll/DollConvas.vue";
import DollItemSelector from "@/components/doll/DollItemSelector.vue";
import httpClient from "@/services/httpClient";
import { userAvatar } from "@/services/dollSystemApi/dollAvaratApi";
import { useUserStore } from "@/stores/user";

const baseUrl = "http://localhost:8080/sprites/";

const equippedItems = ref({});
const isSnackbarVisible = ref(false);
const snackbarMessage = ref(null);

const avatarStore = userAvatar();
const userStore = useUserStore();

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
        snackbarMessage.value = `部位 [${item.category.typeName}] 為必須裝備，無法脫下！`;
        isSnackbarVisible.value = true;
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
  const payload = Object.values(equippedItems.value).map((item) => item.id);

  httpClient({
    method: "put",
    url: "/api/dollitem/newequip",
    data: payload, // 👈 暫時改回傳送純 ID 陣列 (List<Integer>) 以配合後端
  })
    .then((response) => {
      if (response.status === 200) {
        // 清除這位使用者的 avatar 快取，讓 DollAvatar 下次渲染時拿到最新資料
        avatarStore.clearCache(userStore.uid);
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

function resetEquipItems() {
  loadEquippedItems();
}

function randomEquipItems() {
  httpClient({
    method: "get",

    url: "/api/dollitem/userowned/all",
  })
    .then((response) => {
      const allOwned = response.data;
      // 按 category.id 分組
      const grouped = {};
      allOwned.forEach((item) => {
        const cid = item.category.id;
        if (!grouped[cid]) grouped[cid] = [];
        grouped[cid].push(item);
      });

      // 每個圖層隨機抽一件
      const newEquipped = {};
      Object.entries(grouped).forEach(([cid, items]) => {
        newEquipped[cid] = items[Math.floor(Math.random() * items.length)];
      });

      equippedItems.value = newEquipped;
    })

    .catch((error) => console.log(error));
}

onMounted(() => {
  loadEquippedItems();
});
</script>
<style scoped>
.gap-2 {
  gap: 8px;
}
</style>
