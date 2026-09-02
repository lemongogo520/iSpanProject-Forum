<template>
  <v-container class="py-8">

    <!-- 首頁標題 -->
    <div class="mb-8">
      <h1 class="text-h4 font-weight-bold">
        論壇首頁
      </h1>

      <p class="text-body-1 text-medium-emphasis mt-2">
        選擇討論區
      </p>
    </div>


    <!-- 載入中 -->
    <div v-if="loading" class="text-center py-10">
      <v-progress-circular
        indeterminate
        color="primary"
        size="50"
      />
    </div>


    <!-- 錯誤 -->
    <v-alert
      v-else-if="errorMessage"
      type="error"
      variant="tonal"
    >
      {{ errorMessage }}
    </v-alert>


    <!-- Category -->
    <div
      v-else
      v-for="category in sortedCategories"
      :key="category.id"
      class="mb-10"
    >

      <!-- Category 標題 -->
      <div class="d-flex align-center mb-4">

        <v-icon
          icon="mdi-folder"
          size="32"
          class="me-3"
        />

        <h2 class="text-h5 font-weight-bold">
          {{ category.name }}
        </h2>

      </div>


      <!-- Board -->
      <v-row>

        <v-col
          v-for="board in boards[category.id]"
          :key="board.id"
          cols="12"
          sm="6"
          md="4"
          lg="3"
        >

          <v-card
            :to="`/boards/${board.id}`"
            hover
            class="h-100 rounded-xl"
          >

            <v-card-item>

              <v-card-title>
                {{ board.name }}
              </v-card-title>

              <v-card-subtitle
                v-if="board.description"
              >
                {{ board.description }}
              </v-card-subtitle>

            </v-card-item>


            <v-card-actions>

              <v-btn
                color="primary"
                variant="text"
                append-icon="mdi-arrow-right"
              >
                進入討論
              </v-btn>

            </v-card-actions>

          </v-card>

        </v-col>

      </v-row>

    </div>

  </v-container>
</template>


<script setup>

import { ref, computed, onMounted } from 'vue'

import httpClient from '@/api/httpClient'// 引入你在 main.js 中設定的 axios 實例


// =========================
// Category
// =========================

const categories = ref([])


// =========================
// Board
// =========================
//
// 會長這樣：
//
// boards = {
//   1: [ Board1, Board2 ],
//   2: [ Board3, Board4 ],
//   3: [ Board5, Board6 ]
// }

const boards = ref({})


const loading = ref(false)

const errorMessage = ref('')


// =========================
// Category 排序
// =========================

const sortedCategories = computed(() => {

  return [...categories.value].sort(
    (a, b) => a.rank - b.rank
  )

})


// =========================
// 取得 Category
// =========================

const fetchCategories = async () => {

  const response = await httpClient.get(
    '/api/categories'
  )

  categories.value = response.data

}


// =========================
// 取得指定 Category 的 Board
// =========================

const fetchBoards = async (categoryId) => {

  const response = await httpClient.get(
    `/api/boards/category/${categoryId}`
  )

  boards.value[categoryId] = response.data

}


// =========================
// 載入首頁
// =========================

const loadHomePage = async () => {

  loading.value = true

  errorMessage.value = ''

  try {

    // ① 先取得 Category
    await fetchCategories()


    // ② 取得每個 Category 底下的 Board
    await Promise.all(

      categories.value.map(category =>
        fetchBoards(category.id)
      )

    )

  } catch (error) {

    console.error(
      '首頁資料取得失敗：',
      error
    )

    errorMessage.value =
      '首頁資料取得失敗，請確認後端是否正常啟動。'

  } finally {

    loading.value = false

  }

}


// =========================
// 頁面載入時執行
// =========================

onMounted(() => {

  loadHomePage()

})

</script>

