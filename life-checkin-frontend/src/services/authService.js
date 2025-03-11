import api from './api';

const authService = {
  // 用戶登入
  login: async (username, password) => {
    const response = await api.post('/auth/login', { username, password });
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
    }
    return response.data;
  },

  // 用戶註冊
  register: async (userData) => {
    return await api.post('/auth/register', userData);
  },

  // 登出
  logout: () => {
    localStorage.removeItem('token');
  },

  // 檢查是否已登入
  isAuthenticated: () => {
    return localStorage.getItem('token') !== null;
  }
};

export default authService;