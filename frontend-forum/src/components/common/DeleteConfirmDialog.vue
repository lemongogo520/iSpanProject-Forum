<template>
  <v-dialog
    :model-value="modelValue"
    max-width="420"
    persistent
  >
    <v-card>
      <v-card-title class="text-h6">
        {{ title }}
      </v-card-title>

      <v-card-text>
        {{ message }}
      </v-card-text>

      <v-card-actions>
        <v-spacer />

        <v-btn
          variant="text"
          :disabled="loading"
          @click="emit('cancel')"
        >
          取消
        </v-btn>

        <v-btn
          color="error"
          variant="flat"
          :loading="loading"
          :disabled="loading"
          @click="emit('confirm')"
        >
          確定刪除
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },

  title: {
    type: String,
    default: "確認刪除",
  },

  message: {
    type: String,
    required: true,
  },

  loading: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits([
  "cancel",
  "confirm",
]);
</script>