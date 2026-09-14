<template>
  <v-card class="pa-5 mt-6" elevation="1">
    <div class="text-h6 mb-4">
      發表留言
    </div>

    <v-form @submit.prevent="emit('submit')">
      <v-textarea
        :model-value="modelValue"
        label="留言內容"
        placeholder="輸入你的留言..."
        variant="outlined"
        rows="4"
        hide-details
        :disabled="submitting"
        @update:model-value="emit('update:modelValue', $event)"
      />

      <div class="d-flex justify-end mt-4">
        <v-btn
          type="submit"
          color="primary"
          :loading="submitting"
          :disabled="submitting"
        >
          發表留言
        </v-btn>
      </div>
    </v-form>
  </v-card>
</template>

<script setup>
defineProps({
  modelValue: {
    type: String,
    default: "",
  },

  submitting: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits([
  "update:modelValue",
  "submit",
]);
</script>