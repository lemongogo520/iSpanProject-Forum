<template>
  <div>
    <div class="text-subtitle-1 text-medium-emphasis mb-2">
      文章內容
    </div>

    <v-sheet border class="rounded-lg mb-6">
      <QuillEditor
        v-model:content="content"
        content-type="html"
        theme="snow"
        :options="editorOptions"
        placeholder="請輸入文章內容..."
      />
    </v-sheet>
  </div>
</template>

<script setup>
import { QuillEditor, Quill } from "@vueup/vue-quill";
import "@vueup/vue-quill/dist/vue-quill.snow.css";

import { uploadPostImage as uploadPostImageApi } from "@/api/postApi";
import { useNotification } from "@/composables/useNotification";

const content = defineModel({
  type: String,
  default: "",
});

const { showMessage } = useNotification();

// ======================
// Twitch Clip 網址轉換
// ======================
function convertTwitchClipUrl(value) {
  try {
    const url = new URL(value.trim());

    let clipMatch = null;

    if (url.hostname === "clips.twitch.tv") {
      clipMatch = url.pathname.match(
        /^\/([A-Za-z0-9_-]+)\/?$/,
      );
    }

    if (
      url.hostname === "twitch.tv" ||
      url.hostname === "www.twitch.tv"
    ) {
      clipMatch = url.pathname.match(
        /^\/[^/]+\/clip\/([A-Za-z0-9_-]+)\/?$/,
      );
    }

    if (!clipMatch) {
      return value;
    }

    const clipId = clipMatch[1];
    const parent = window.location.hostname;

    return (
      "https://clips.twitch.tv/embed" +
      `?clip=${encodeURIComponent(clipId)}` +
      `&parent=${encodeURIComponent(parent)}` +
      "&autoplay=false"
    );
  } catch {
    return value;
  }
}

// 擴充 Quill 原本的影片格式
const OriginalVideo = Quill.import("formats/video");

class ForumVideo extends OriginalVideo {
  static create(value) {
    return super.create(convertTwitchClipUrl(value));
  }
}

Quill.register(ForumVideo, true);

// ======================
// 圖片上傳
// ======================
function imageHandler() {
  const quill = this.quill;

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

    const maxSize = 8 * 1024 * 1024;

    if (file.size > maxSize) {
      showMessage("圖片不能超過 8 MB", "warning");
      return;
    }

    try {
      const response = await uploadPostImageApi(file);

      let imageUrl = response.data.url;

      if (!imageUrl) {
        throw new Error("後端沒有回傳圖片網址");
      }

      if (imageUrl.startsWith("/")) {
        const backendUrl =
          import.meta.env.VITE_AXIOS_HTTP_BASEURL;

        imageUrl = `${backendUrl}${imageUrl}`;
      }

      const range = quill.getSelection(true);

      quill.insertEmbed(
        range.index,
        "image",
        imageUrl,
        "user",
      );

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

  input.click();
}

// ======================
// Quill 工具列
// ======================
const editorOptions = {
  modules: {
    toolbar: {
      container: [
        [{ header: [1, 2, 3, false] }],
        ["bold", "italic", "underline", "strike"],
        [{ list: "ordered" }, { list: "bullet" }],
        [{ color: [] }, { background: [] }],
        [{ align: [] }],
        [
          "blockquote",
          "code-block",
          "link",
          "video",
          "image",
        ],
        ["clean"],
      ],
      handlers: {
        image: imageHandler,
      },
    },
  },
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

:deep(.ql-editor iframe.ql-video) {
  display: block !important;
  width: 100% !important;
  max-width: 800px !important;
  aspect-ratio: 16 / 9;
  height: auto !important;
  margin: 20px auto !important;
  border: 0;
  border-radius: 10px;
  background-color: #000;
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

/* 防止連結及影片輸入視窗跑出編輯器左側 */
:deep(.ql-snow .ql-tooltip) {
  left: 8px !important;
  right: auto !important;
  max-width: calc(100% - 16px);
}

:deep(.ql-snow .ql-tooltip input[type="text"]) {
  width: min(420px, calc(100vw - 180px));
  max-width: calc(100% - 70px);
  font-size: 14px;
}

@media (max-width: 600px) {
  :deep(.ql-editor iframe.ql-video) {
    width: 100% !important;
    aspect-ratio: 16 / 9;
    height: auto !important;
    margin: 12px auto !important;
    border-radius: 8px;
  }
}
</style>