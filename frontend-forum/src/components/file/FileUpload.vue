<template>
  <div>
    <v-btn
      prepend-icon="mdi-upload"
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
      :key="index"
      class="mt-3"
    >
      <div v-if="item.uploading">
        <div class="d-flex justify-space-between">
          <span>{{ item.name }}</span>
          <span>{{ item.progress }}%</span>
        </div>

        <v-progress-linear
          :model-value="item.progress"
          class="mt-1"
        />
      </div>

      <div v-else-if="item.uploaded"
        class="d-flex align-center">
        <img
        v-if="['jpg', 'jpeg', 'png'].includes(item.extension)"
        :src="item.url"
        style="max-width: 50px; cursor: pointer;"
        @click="openFile(item)"
        />

        <a href="#"
          @click.prevent="openFile(item)">
          {{ item.name }}
        </a>
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
  }
})

const emit = defineEmits(['upload-complete'])

const fileInput = ref(null)
const fileList = ref([])

const openFileSelector = () => {
  fileInput.value.click()
}

const getExtension = (fileName) => {
  const index = fileName.lastIndexOf('.')
  return index === -1 ? '' : fileName.substring(index + 1).toLowerCase()
}

const selectFiles = (event) => {
  const selectedFiles = Array.from(event.target.files)

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
        previewUrl: URL.createObjectURL(file)
    }

    fileList.value.push(fileItem)

    const reactiveFile = fileList.value[fileList.value.length - 1]
    simulateUpload(reactiveFile)
  })

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
    }
  }, 100)
}

const openFile = (fileItem) => {
  if (!fileItem.previewUrl) return

  window.open(fileItem.previewUrl, '_blank')
}

const loadExistingFiles = () => {
  fileList.value = props.files.map(file => ({
    ...file,
    uploading: false,
    uploaded: true,
    progress: 100
  }))
}

watch(
  () => props.files,
  loadExistingFiles,
  { immediate: true }
)

onBeforeUnmount(() => {
  fileList.value.forEach(file => {
    if (file.previewUrl?.startsWith('blob:')) {
      URL.revokeObjectURL(file.previewUrl)
    }
  })
})
</script>

