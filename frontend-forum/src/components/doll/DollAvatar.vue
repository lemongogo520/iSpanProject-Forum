<!-- 使用方式 -->
<!-- 在script import DollAvatar from "@/components/doll/DollAvatar.vue"; -->
<!-- 在template中要顯示的地方用
 <DollAvatar :userId="confirmedUserId" :scale="1.5" :size="100" /> 
 scale是人物大小 size是外框大小
 -->

<template>
  <v-sheet
    class="d-inline-flex align-center justify-center pa-1 rounded-circle border"
    :style="{
      background: 'var(--color-background-light)',
      borderColor: 'var(--color-border) !important',
      width: `${size}px`,
      height: `${size}px`,
      overflow: 'hidden',
    }"
  >
    <!-- 載入中畫面 -->
    <template v-if="isLoading">
      <v-progress-circular
        indeterminate
        color="primary"
        size="24"
      ></v-progress-circular>
    </template>

    <!-- 載入失敗或無資料時的預設圖示 -->
    <template v-else-if="hasError">
      <v-icon icon="mdi-account" color="grey" :size="size * 0.6"></v-icon>
    </template>

    <!-- 成功載入紙娃娃 -->
    <template v-else>
      <DollConvas
        :equippedItems="avatarItems"
        :scale="scale"
        :avatarMode="true"
      />
    </template>
  </v-sheet>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import DollConvas from "@/components/doll/DollConvas.vue";
import { userAvatar } from "@/services/dollSystemApi/dollAvaratApi";

const props = defineProps({
  // 必須傳入玩家的 UUID
  userId: {
    type: String,
    required: true,
  },
  // 外框圓形的直徑大小 (預設 68px)
  size: {
    type: Number,
    default: 68,
  },
  // 內部紙娃娃的縮放比例 (預設 1.8)
  scale: {
    type: Number,
    default: 1.8,
  },
});

const avatarStore = userAvatar();

const avatarItems = ref({}); // 儲存目前穿在身上的裝備物件
const isLoading = ref(true); // 是否正在載入中
const hasError = ref(false); // 是否載入失敗

function fetchAvatar(id) {
  if (!id) return; // 如果沒有傳入 id，直接 return
  isLoading.value = true; // 設定為載入中
  hasError.value = false; // 重置錯誤狀態
  avatarStore
    .fetchAvatar(id) //呼叫pinia拿資料

    .then((data) => {
      avatarItems.value = data;
      console.log(data);
    })
    .catch((error) => {
      console.error("無法取得大頭貼", error);
      hasError.value = true;
    })
    .finally(() => {
      isLoading.value = false;
    });
}

// 元件掛載時抓取資料
onMounted(() => {
  fetchAvatar(props.userId);
});

// 當 userId 改變時重新抓取 (例如在論壇文章中，同一個元件換了人)
watch(
  () => props.userId,
  (newId) => {
    fetchAvatar(newId);
  },
);
</script>
