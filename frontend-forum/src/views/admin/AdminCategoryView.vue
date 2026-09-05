<template>
  <v-container class="py-5">
    <h2 class="mb-4">📦 分類管理</h2>

    <!-- 表單區塊 (同時支援 新增 與 編輯) -->
    <v-card class="pa-4 mb-5" elevation="2">
      <div class="d-flex justify-space-between align-center mb-2">
        <v-card-title class="px-0">
          {{ isEditMode ? `編輯分類 (ID: ${form.id})` : '新增分類' }}
        </v-card-title>
        <!-- 編輯模式才顯示取消按鈕 -->
        <v-btn v-if="isEditMode" size="small" color="grey" variant="outlined" @click="resetForm">
          取消編輯
        </v-btn>
      </div>

      <v-card-text class="px-0">
        <v-text-field
          v-model="form.name"
          label="分類名稱"
          variant="outlined"
          density="comfortable"
          clearable
        />
        <v-text-field
          v-model.number="form.rank"
          label="排序 (Rank)"
          type="number"
          variant="outlined"
          density="comfortable"
        />
        
        <!-- 按鈕文字與顏色會隨模式自動切換 -->
        <v-btn 
          :color="isEditMode ? 'warning' : 'success'" 
          @click="handleSubmit" 
          block
        >
          {{ isEditMode ? '確認修改' : '送出新增' }}
        </v-btn>
      </v-card-text>
    </v-card>

    <!-- 列表區塊 -->
    <v-card class="pa-4" elevation="2">
      <div class="d-flex justify-space-between align-center mb-3">
        <v-card-title class="px-0">現有分類列表</v-card-title>
        <v-btn size="small" variant="outlined" @click="fetchCategories">重新整理</v-btn>
      </div>
      
      <v-list v-if="categories.length > 0">
        <v-list-item v-for="cat in categories" :key="cat.id" border class="mb-2 rounded">
          <v-list-item-title><b>ID:</b> {{ cat.id }} | <b>名稱:</b> {{ cat.name }}</v-list-item-title>
          <v-list-item-subtitle>排序: {{ cat.rank }}</v-list-item-subtitle>
          
          <!-- 每筆資料右側加上「編輯」按鈕 -->
          <template v-slot:append>
            <v-btn size="small" color="error" variant="flat" @click="handleDelete(cat)">
              刪除
            </v-btn>
            <v-btn size="small" color="primary" variant="flat" @click="startEdit(cat)">
              編輯
            </v-btn>
          </template>
        </v-list-item>
      </v-list>
      <p v-else class="text-grey text-center py-3">目前沒有資料</p>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import httpClient from '@/api/httpClient' // 引入你在 main.js 中設定的 axios 實例

// 1. 響應式資料與狀態宣告
const categories = ref([])
const form = ref({
  id: null,
  name: '',
  rank: 0
})
const isEditMode = ref(false)

const API_URL = '/api/categories'

// 2. 取得全部資料 (GET)
const fetchCategories = () => {
  httpClient({
    method: 'get',
    url: API_URL
  })
    .then(response => {
      categories.value = response.data
    })
    .catch(error => {
      console.error('連線錯誤:', error)
    })
}

// 3. 點擊編輯按鈕
const startEdit = (cat) => {
  form.value = {
    id: cat.id,
    name: cat.name,
    rank: cat.rank
  }
  isEditMode.value = true
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 4. 取消編輯
const resetForm = () => {
  form.value = {
    id: null,
    name: '',
    rank: 0
  }
  isEditMode.value = false
}

// 5. 送出表單 (統一處理 新增 POST 或 更新 PUT)
const handleSubmit = () => {
  if (!form.value.name) {
    alert('請填寫分類名稱！')
    return
  }

  if (isEditMode.value) {
    // 編輯模式 -> 執行 PUT
    httpClient({
      method: 'put',
      url: `${API_URL}/${form.value.id}`,
      data: {
        name: form.value.name,
        rank: form.value.rank
      }
    })
      .then(response => {
        console.log(response)
        alert('修改成功！')
        resetForm()
        fetchCategories()
      })
      .catch(error => {
        console.log(error)
        alert('修改失敗')
      })
  } else {
    // 新增模式 -> 執行 POST
    httpClient({
      method: 'post',
      url: API_URL,
      data: {
        name: form.value.name,
        rank: form.value.rank
      }
    })
      .then(response => {
        console.log(response)
        alert('新增成功！')
        resetForm()
        fetchCategories()
      })
      .catch(error => {
        console.log(error)
        alert('新增失敗')
      })
  }
}

// 6. 畫面載入時抓取資料
onMounted(() => {
  fetchCategories()
})

const handleDelete = (cat) => {
  // 彈出確認視窗，避免誤按
  if (!confirm(`確定要刪除分類「${cat.name}」嗎？`)) {
    return
  }

  httpClient({
    method: 'delete',
    url: `${API_URL}/delete/${cat.id}` // 帶上要刪除的 ID
  })
    .then(response => {
      console.log(response)
      alert('刪除成功！')
      
      // 如果被刪除的項目正好是正在編輯的項目，就重設表單
      if (form.value.id === cat.id) {
        resetForm()
      }
      
      fetchCategories() // 重新整理列表
    })
    .catch(error => {
      console.error(error)
      alert('刪除失敗')
    })
}
</script>