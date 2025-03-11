import api from './api';

const checkInService = {
  // 執行打卡
  checkIn: async () => {
    return await api.post('/checkins');
  },
  
  // 獲取今日打卡狀態
  getTodayStatus: async () => {
    return await api.get('/checkins/today');
  },
  
  // 獲取用戶的所有打卡記錄
  getMyCheckIns: async () => {
    return await api.get('/checkins/my');
  }
};

export default checkInService;