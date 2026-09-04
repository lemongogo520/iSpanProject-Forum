import axios from 'axios';

const backendURL = import.meta.env.VITE_AXIOS_HTTP_BASEURL // locahost 或 ip 或 domain
// 提醒，環境變數一定要 VITE 開頭，否則讀不到，
// 另外 .env.local 要自己加到根目錄，這個檔案不會被 git 追蹤

// 跨域請求帶 cookie , 前端需要設定
const httpClient = axios.create({
    withCredentials: true,
    baseURL: backendURL
});

export default httpClient;