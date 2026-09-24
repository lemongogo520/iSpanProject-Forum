import { reactive } from "vue";

// 放在函式外，讓不同頁面共用同一份狀態
const snackbar = reactive({
  show: false,
  message: "",
  color: "success",
  timeout:"3000",
});

const showMessage = (message, color = "success") => {
  snackbar.message = message;
  snackbar.color = color;
  snackbar.show = true;
  snackbar.timeout = 3000;
};

export const useNotification = () => {
  return { snackbar, showMessage};
};