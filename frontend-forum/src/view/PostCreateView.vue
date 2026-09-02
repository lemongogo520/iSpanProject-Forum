<!-- src/views/PostCreate.vue -->
<template>
  <v-container class="py-8" max-width="800">
    <!-- 返回看板 -->
    <v-btn variant="text" prepend-icon="mdi-arrow-left" class="mb-4" @click="goBack">
      返回看板
    </v-btn>

    <v-card class="pa-6 rounded-xl">
      <v-card-title class="text-h5 mb-4">發表新文章</v-card-title>
      
      <v-form ref="form" @submit.prevent="handleSubmit">
        <!-- 標題輸入 -->
        <v-text-field
          v-model="title"
          label="文章標題"
          variant="outlined"
          :rules="[v => !!v || '請輸入標題']"
          required
          class="mb-4"
        />

        <!-- 文字編輯器標題提示 -->
        <div class="text-subtitle-1 text-medium-emphasis mb-2">文章內容</div>

        <!-- 富文字編輯器區塊 -->
        <v-sheet border class="rounded-lg overflow-hidden mb-6">
          <QuillEditor
            v-model:content="content"
            contentType="html"
            theme="snow"
            :options="editorOptions"
            placeholder="請輸入文章內容..."
          />
        </v-sheet>

        <!-- 按鈕 -->
        <div class="d-flex justify-end">
          <v-btn variant="text" class="mr-2" @click="goBack">取消</v-btn>
          <v-btn color="primary" type="submit" :loading="submitting">
            發布文章
          </v-btn>
        </div>
      </v-form>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import httpClient from '@/api/httpClient'; // 這是你專案封裝好的 axios 實例

// 引入 Quill 編輯器與其核心樣式
import { QuillEditor } from '@vueup/vue-quill';
import '@vueup/vue-quill/dist/vue-quill.snow.css';

const route = useRoute();
const router = useRouter();

// 從網址參數取得 boardId 
const boardId = route.query.boardId;

const title = ref('');
const content = ref('');
const submitting = ref(false);

// 編輯器工具列功能配置
const editorOptions = {
  modules: {
    toolbar: [
      [{ 'header': [1, 2, 3, false] }],
      ['bold', 'italic', 'underline', 'strike'],
      [{ 'list': 'ordered'}, { 'list': 'bullet' }],
      [{ 'color': [] }, { 'background': [] }],
      ['blockquote', 'code-block'],
      ['clean']
    ]
  }
};

// 送出文章
const handleSubmit = async () => {
  if (!title.value || !content.value || content.value === '<p><br></p>') {
    alert('請填寫標題與內容');
    return;
  }

  submitting.value = true;

  // 💡 改成你指定的 axios 寫法
  httpClient({
    method: 'post', // 新增資料使用 post
    url: '/api/posts',
    data: {
      title: title.value,
      content: content.value, // 富文字編輯器產出的 HTML 字串
      board: {id: boardId ? Number(boardId) : null} // 確保傳給 Spring Boot 的是數字型態的 ID
    }
  })
  .then(response => {
 
    console.log('===== 新增文章成功 =====');
    console.log('DATA:', response.data);
    goBack(); // 成功後返回看板頁
  })
  .catch(error => {
      
    console.error('===== 新增文章失敗 =====');
    console.log(error);
    alert('發文失敗，請檢查網路連線或後端服務');
  })
  .finally(() => {
    submitting.value = false;
  });
};

// 返回看板
const goBack = () => {
  router.back();
};
</script>

<style scoped>
:deep(.ql-container) {
  min-height: 350px;
  font-size: 16px;
}
:deep(.ql-toolbar) {
  border-top: none !important;
  border-left: none !important;
  border-right: none !important;
  background-color: #f5f5f5;
}
:deep(.ql-container.ql-snow) {
  border: none !important;
}
</style>
