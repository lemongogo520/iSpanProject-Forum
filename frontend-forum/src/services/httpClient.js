import router from '@/router';
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

const backendURL = import.meta.env.VITE_AXIOS_HTTP_BASEURL // locahost 或 ip 或 domain
// 提醒，環境變數一定要 VITE 開頭，否則讀不到，
// 另外 .env.local 要自己加到根目錄，這個檔案不會被 git 追蹤

// 跨域請求帶 cookie , 前端需要設定
const httpClient = axios.create({
    withCredentials: true,
    baseURL: backendURL
});

// httpClient.interceptors.response.use(
//     (response) => response,
//     (error) => {
//         const path = router.currentRoute.value.path;
//         if (
//             error.response?.status === 401 &&
//             path !== "/login" &&
//             path !== "/register"
//         ) {
//             useAuthStore().setLogout();
//             router.push("/login");
//         }
//         return Promise.reject(error);   // 繼續往外丟
//     }
// );

export default httpClient;
