import axios from 'axios';

// 從環境變量獲取 API 基礎 URL
const baseURL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

// 創建 axios 實例
const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 請求攔截器 - 添加認證 token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 響應攔截器 - 處理常見錯誤
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // 處理 401 未授權錯誤
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      // 如果在客戶端，重定向到登入頁面
      if (typeof window !== 'undefined') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;