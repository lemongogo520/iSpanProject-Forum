import { ref, computed } from "vue";

const items = ref([]);
const MAX = 99;

export const useNotificationCenter = () => {
  const unreadCount = computed(() => items.value.filter((n) => !n.read).length);

  const push = (notification) => {
    items.value.unshift({
      id: crypto.randomUUID?.() ?? String(Date.now() + Math.random()),
      read: false,
      time: new Date().toISOString(),
      ...notification,
    });
    if (items.value.length > MAX) items.value.length = MAX;
  };

  const markAllRead = () => items.value.forEach((n) => (n.read = true));
  const markRead = (id) => {
    const n = items.value.find((x) => x.id === id);
    if (n) n.read = true;
  };
  const clear = () => (items.value = []);

  return { items, unreadCount, push, markAllRead, markRead, clear };
};