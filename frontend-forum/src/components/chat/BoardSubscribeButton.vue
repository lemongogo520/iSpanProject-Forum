<template>
    <v-btn v-if="iconOnly" :icon="subscribed ? 'mdi-bell-off' : 'mdi-bell-ring'" size="x-small" variant="text"
        :loading="busy" @click.stop="toggle" />
    <v-btn v-else :prepend-icon="subscribed ? 'mdi-bell-off' : 'mdi-bell-ring'" :loading="busy" @click.stop="toggle">
        {{ subscribed ? "已訂閱" : "訂閱" }}
    </v-btn>
</template>

<script setup>
import { computed, ref } from "vue";
import { useChatStore } from "@/stores/conversation";

import { useNotification } from "@/composables/useNotification";
const { showMessage } = useNotification();

const props = defineProps({
    boardId: { type: Number, required: true },
    iconOnly: { type: Boolean, default: false },
});

const chatStore = useChatStore();
const busy = ref(false);

const subscribed = computed(() => chatStore.subscribedBoardIds.has(props.boardId));

const toggle = async () => {
    if (busy.value) return;
    busy.value = true;
    try {
        if (subscribed.value) await chatStore.unsubscribeBoard(props.boardId);
        else await chatStore.subscribeBoard(props.boardId);
    } catch (e) {
        console.error("訂閱操作失敗", e);
        showMessage(e.response?.data?.message ?? "送出失敗", "error");
    } finally {
        busy.value = false;
    }
};
</script>
