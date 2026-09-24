<template>
  <div>
    <v-btn
      prepend-icon="mdi-upload"
      class="upload-btn"
      :disabled="fileList.length >= props.maxFiles"
      @click="openFileSelector"
    >
      上傳檔案
    </v-btn>

    <input
      ref="fileInput"
      type="file"
      hidden
      :multiple="props.multiple"
      :accept="props.allowedExtensions.map(ext => `.${ext}`).join(',')"
      @change="selectFiles"
    >

    <div
      v-for="(item, index) in fileList"
      :key="item.fileId || item.previewUrl || index"
      class="mt-3"
    >
      <!-- 上傳中 -->
      <div v-if="item.uploading">
        <div class="d-flex justify-space-between align-center">
          <span>{{ item.name }}</span>

          <v-btn
            icon="mdi-close"
            size="small"
            variant="text"
            @click="removeFile(index)"
          />
        </div>

        <div class="d-flex justify-space-between">
          <span>{{ item.name }}</span>
          <span>{{ item.progress }}%</span>
        </div>

        <v-progress-linear
          :model-value="item.progress"
          class="mt-1"
        />
      </div>

      <!-- 已完成 -->
      <div
        v-else-if="item.uploaded"
        class="d-flex align-center"
      >
        <img
          v-if="['jpg', 'jpeg', 'png'].includes(item.extension)"
          :src="item.url || item.previewUrl"
          style="max-width: 50px; max-height: 50px; cursor: pointer; object-fit: contain;"
          @click="openFile(item)"
        >

        <a
          href="#"
          class="ml-2"
          @click.prevent="openFile(item)"
        >
          {{ item.name }}
        </a>

        <v-btn
          icon="mdi-close"
          size="small"
          variant="text"
          class="ml-2"
          @click="removeFile(index)"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onBeforeUnmount } from 'vue'

const props = defineProps({
  files: {
    type: Array,
    default: () => []
  },
  allowedExtensions: {
    type: Array,
    default: () => ['jpg', 'jpeg', 'png', 'pdf']
  },
  multiple: {
    type: Boolean,
    default: true
  },
  maxFiles: {
    type: Number,
    default: 1
  }
})

const emit = defineEmits([
  'update:files',
  'upload-complete',
  'remove'
])

const fileInput = ref(null)
const fileList = ref([])

const openFileSelector = () => {
  if (fileList.value.length >= props.maxFiles) return
  fileInput.value?.click()
}

const getExtension = (fileName) => {
  const index = fileName.lastIndexOf('.')
  return index === -1 ? '' : fileName.substring(index + 1).toLowerCase()
}

const selectFiles = (event) => {
  let selectedFiles = Array.from(event.target.files)

  const remainingCount = props.maxFiles - fileList.value.length

  if (remainingCount <= 0) {
    event.target.value = ''
    return
  }

  // 超過剩餘數量時，只取可以容納的數量
  selectedFiles = selectedFiles.slice(0, remainingCount)

  selectedFiles.forEach(file => {
    const extension = getExtension(file.name)

    if (!props.allowedExtensions.includes(extension)) {
      return
    }

    const fileItem = {
      fileId: null,
      file,
      name: file.name,
      extension,
      progress: 0,
      uploading: true,
      uploaded: false,
      previewUrl: URL.createObjectURL(file),
      url: null
    }

    fileList.value.push(fileItem)

    const reactiveFile = fileList.value[fileList.value.length - 1]

    simulateUpload(reactiveFile)
  })

  emitFiles()

  event.target.value = ''
}

const simulateUpload = (fileItem) => {
  const timer = setInterval(() => {
    fileItem.progress += 10

    if (fileItem.progress >= 100) {
      fileItem.progress = 100
      fileItem.uploading = false
      fileItem.uploaded = true

      clearInterval(timer)

      emit('upload-complete', {
        fileId: fileItem.fileId,
        file: fileItem.file,
        name: fileItem.name,
        extension: fileItem.extension,
        previewUrl: fileItem.previewUrl
      })

      emitFiles()
    }
  }, 100)
}

const removeFile = (index) => {
  const fileItem = fileList.value[index]

  if (!fileItem) return

  // 如果是新上傳檔案，釋放 Blob URL
  if (fileItem.previewUrl?.startsWith('blob:')) {
    URL.revokeObjectURL(fileItem.previewUrl)
  }

  fileList.value.splice(index, 1)

  // 通知父元件檔案被刪除
  emit('remove', fileItem)

  // 同步目前檔案清單
  emitFiles()
}

const emitFiles = () => {
  emit(
    'update:files',
    fileList.value.map(file => ({
      fileId: file.fileId,
      file: file.file,
      name: file.name,
      extension: file.extension,
      previewUrl: file.previewUrl,
      url: file.url
    }))
  )
}

const openFile = (fileItem) => {
  const url = fileItem.url || fileItem.previewUrl

  if (!url) return

  window.open(url, '_blank')
}

const loadExistingFiles = () => {
  fileList.value = props.files.map(file => ({
    fileId: file.fileId ?? null,
    file: file.file ?? null,
    name: file.name,
    extension: file.extension ?? getExtension(file.name),
    progress: 100,
    uploading: false,
    uploaded: true,
    previewUrl: file.previewUrl ?? null,
    url: file.url ?? (
      file.fileId
        ? `/api/file/files/${file.fileId}`
        : null
    )
  }))
}

watch(
  () => props.files,
  loadExistingFiles,
  {
    immediate: true
  }
)

onBeforeUnmount(() => {
  fileList.value.forEach(file => {
    if (file.previewUrl?.startsWith('blob:')) {
      URL.revokeObjectURL(file.previewUrl)
    }
  })
})
</script>

<style scoped>
.upload-btn {
  background-color: #2e7d32 !important;
  color: white !important;
}

.upload-btn:not(.v-btn--disabled) {
  background-color: #2e7d32 !important;
  color: white !important;
}
</style>