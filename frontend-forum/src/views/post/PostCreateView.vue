<!-- src/views/PostCreate.vue -->
<template>
  <v-container class="py-8" max-width="800">
    <!-- 返回看板 -->
    <v-btn
      variant="text"
      prepend-icon="mdi-arrow-left"
      class="mb-4"
      @click="goBack"
    >
      返回看板
    </v-btn>

    <!-- 編輯模式先取得文章，資料回來後再建立 Quill -->
    <div v-if="loading" class="text-center py-12">
      <v-progress-circular indeterminate color="primary" size="48" />
      <div class="text-body-2 text-medium-emphasis mt-3">文章載入中...</div>
    </div>

    <v-card v-else class="pa-6 rounded-xl">
      <v-card-title class="text-h5 mb-4">
        {{ isEditing ? "編輯文章" : "發表新文章" }}
      </v-card-title>

      <v-form ref="form" @submit.prevent="handleSubmit">
        <!-- 文章類型：新增這段 -->
        <v-select
          v-model="postType"
          :items="postTypeOptions"
          label="文章類型"
          variant="outlined"
          class="mb-4"
        />
        <!-- 標題輸入 -->
        <v-text-field
          v-model="title"
          label="文章標題"
          variant="outlined"
          :rules="[(v) => !!v || '請輸入標題']"
          required
          class="mb-4"
        />

        <!-- 文字編輯器標題提示 -->
        <div class="text-subtitle-1 text-medium-emphasis mb-2">文章內容</div>

        <!-- 富文字編輯器區塊 -->
        <v-sheet border class="rounded-lg overflow-hidden mb-6">
          <QuillEditor
            v-model:content="content"
            content-type="html"
            theme="snow"
            :options="editorOptions"
            placeholder="請輸入文章內容..."
          />
        </v-sheet>

        <!-- 按鈕 -->
        <div class="d-flex justify-end">
          <v-btn variant="text" class="mr-2" @click="goBack">取消</v-btn>
          <v-btn color="primary" type="submit" :loading="submitting">
            {{ isEditing ? "儲存修改" : "發布文章" }}
          </v-btn>
        </div>
      </v-form>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

import {
  createPost as createPostApi,
  updatePost as updatePostApi,
  getPost as getPostApi,
  uploadPostImage as uploadPostImageApi,
} from "@/api/postApi";

import { QuillEditor } from "@vueup/vue-quill";
import "@vueup/vue-quill/dist/vue-quill.snow.css";

import { useNotification } from "@/composables/useNotification";
const { showMessage } = useNotification();

const route = useRoute();
const router = useRouter();

// ======================
// 判斷新增或編輯
// ======================
const editingPostId = route.params.postId;

const isEditing = computed(() => {
  return Boolean(editingPostId);
});

// 新增文章時從 query 取得 boardId
// 編輯文章時會由文章 API 補上原本的 boardId
const boardId = ref(route.query.boardId ? Number(route.query.boardId) : null);

// ======================
// 表單資料
// ======================
const title = ref("");
const content = ref("");
const submitting = ref(false);
const loading = ref(false);

const postType = ref("討論");
const postTypeOptions = ["討論", "攻略", "心得", "問題"];

// ======================
// Quill 工具列
// ======================
// Quill 圖片上傳
function imageHandler() {
  // this.quill 是目前這一個 Quill 編輯器
  const quill = this.quill;

  // 建立一個不顯示在畫面上的選檔欄位
  const input = document.createElement("input");

  input.setAttribute("type", "file");
  input.setAttribute(
    "accept",
    "image/jpeg,image/png,image/gif,image/webp",
  );

  input.onchange = async () => {
    const file = input.files?.[0];

    if (!file) {
      return;
    }

    // 前端先限制 8 MB
    const maxSize = 8 * 1024 * 1024;

    if (file.size > maxSize) {
      showMessage("圖片不能超過 8 MB", "warning");
      return;
    }

    try {
      // 呼叫 POST /api/posts/images
      const response = await uploadPostImageApi(file);

      let imageUrl = response.data.url;

      if (!imageUrl) {
        throw new Error("後端沒有回傳圖片網址");
      }

      // 後端目前回傳 /uploads/img/xxx.jpg，
      // 加上後端網址，避免瀏覽器跑去 localhost:5173 找圖片
      if (imageUrl.startsWith("/")) {
        const backendUrl =
          import.meta.env.VITE_AXIOS_HTTP_BASEURL;

        imageUrl = `${backendUrl}${imageUrl}`;
      }

      // 取得目前游標位置
      const range = quill.getSelection(true);

      // 在游標位置插入圖片
      quill.insertEmbed(
        range.index,
        "image",
        imageUrl,
        "user",
      );

      // 將游標移到圖片後面
      quill.setSelection(range.index + 1);

      showMessage("圖片上傳成功", "success");
    } catch (error) {
      console.error("圖片上傳失敗：", error);

      showMessage(
        error.response?.data ?? "圖片上傳失敗",
        "error",
      );
    }
  };

  // 打開作業系統的圖片選擇視窗
  input.click();
}
const editorOptions = {
  modules: {
    toolbar: {
      container: [
        [{ header: [1, 2, 3, false] }],
        ["bold", "italic", "underline", "strike"],
        [{ list: "ordered" }, { list: "bullet" }],
        [{ color: [] }, { background: [] }],
        [{ align: [] }],
        ["blockquote", "code-block", "video", "image"],
        ["clean"],
      ],
      handlers: {
        image: imageHandler,
      },
    },
  },
};

// ======================
// 取得要編輯的文章
// ======================
const fetchEditingPost = () => {
  if (!isEditing.value) {
    return;
  }

  loading.value = true;

  getPostApi(editingPostId)
    .then((response) => {
      const post = response.data;

      title.value = post.title ?? "";
      content.value = post.content ?? "";

      // 依照後端實際回傳的 Board 欄位調整
      // 建議 PostDTO 直接回傳 boardId；同時保留舊結構作為備援
      boardId.value = post.boardId ?? post.board?.id ?? boardId.value;
    })
    .catch((error) => {
      console.error("取得文章失敗：", error);
      showMessage(error.response?.data ?? "取得文章失敗", "error");
    })
    .finally(() => {
      loading.value = false;
    });
};

// ======================
// 新增或修改文章
// ======================
const handleSubmit = () => {
  if (submitting.value) return;
  const cleanTitle = title.value.trim();

  if (!cleanTitle || !content.value || content.value === "<p><br></p>") {
    showMessage("請填寫標題與內容", "error");
    return;
  }

  if (!boardId.value) {
    showMessage("找不到文章所屬看板", "error");
    return;
  }

  submitting.value = true;

  // 準備文章資料
  const postData = {
    title: cleanTitle,
    content: content.value,
    postType: postType.value,
    board: {
      id: boardId.value,
    },
  };

  // 編輯使用 PUT，新增使用 POST
  const request = isEditing.value
    ? updatePostApi(editingPostId, postData)
    : createPostApi(postData);

  request
    .then(async () => {
      const successMessage = isEditing.value ? "文章修改成功" : "文章發布成功";

      const destination = isEditing.value
        ? {
            name: "PostDetail",
            params: { postId: editingPostId },
          }
        : {
            name: "board",
            params: { boardId: boardId.value },
          };

      // 文章已儲存；導頁失敗要另外處理
      try {
        const failure = await router.push(destination);

        if (failure) {
          showMessage("文章已儲存，但頁面未切換，請勿重複送出", "warning");
          return;
        }
      } catch (error) {
        console.error("儲存後跳轉失敗：", error);
        showMessage("文章已儲存，但跳轉失敗，請返回看板查看", "warning");
        return;
      }

      // 導頁完成後，才顯示成功通知
      showMessage(successMessage, "success");
    })
    .catch((error) => {
      console.error("文章儲存失敗：", error);

      showMessage(
        error.response?.data ??
          (isEditing.value ? "修改文章失敗" : "新增文章失敗"),
        "error",
      );
    })
    .finally(() => {
      submitting.value = false;
    });
};

// ======================
// 返回
// ======================
const goBack = () => {
  router.back();
};

// 編輯模式進入頁面時，取得原文章
onMounted(() => {
  fetchEditingPost();
});
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
:deep(.ql-editor iframe.ql-video) {
  display: block !important;
  width: 100% !important;
  max-width: 800px !important;
  height: min(56.25vw, 450px) !important;
  margin: 16px auto !important;
  border: 0;
}
@media (max-width: 600px) {
  :deep(.ql-editor iframe.ql-video) {
    width: 100% !important;
    height: calc((100vw - 80px) * 0.5625) !important;
    margin: 12px auto !important;
  }
}
:deep(.ql-editor img) {
  display: inline-block;
  width: auto;
  max-width: 100%;
  height: auto;
  margin: 0;
  vertical-align: middle;
}

:deep(.ql-editor .ql-align-center img) {
  margin-left: auto;
  margin-right: auto;
}

:deep(.ql-editor .ql-align-right img) {
  margin-left: auto;
  margin-right: 0;
}
</style>
