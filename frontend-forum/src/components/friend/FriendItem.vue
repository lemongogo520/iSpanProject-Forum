<template>
  <v-list-item>
    <template #prepend >
      <DollAvatar :userId="friend.userId" :scale="scale" :size="size" />
    </template>
    <div class="ml-4">
      <v-list-item-title>{{ friend.displayName }}</v-list-item-title>
      <v-list-item-subtitle>@{{ friend.account }}</v-list-item-subtitle>
    </div>
  </v-list-item>
</template>
<script setup>
import DollAvatar from "@/components/doll/DollAvatar.vue";

defineProps({
  friend: {
    type: Object,
    required: true,
  },
  size: {
    type: Number,
    required: true
  },
  scale: {
    type: Number,
    required: true
  }
});
</script>
