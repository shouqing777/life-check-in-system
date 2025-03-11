import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import checkInService from "../../services/checkInService";

// 執行打卡 Thunk
export const performCheckIn = createAsyncThunk(
  "checkIn/performCheckIn",
  async (_, { rejectWithValue }) => {
    try {
      const response = await checkInService.checkIn();
      return response.data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || "Check-in failed"
      );
    }
  }
);

// 獲取今日打卡狀態 Thunk
export const getTodayStatus = createAsyncThunk(
  "checkIn/getTodayStatus",
  async (_, { rejectWithValue }) => {
    try {
      const response = await checkInService.getTodayStatus();
      return response.data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || "Failed to get today status"
      );
    }
  }
);

// 獲取我的打卡記錄 Thunk
export const getMyCheckIns = createAsyncThunk(
  "checkIn/getMyCheckIns",
  async (_, { rejectWithValue }) => {
    try {
      const response = await checkInService.getMyCheckIns();
      return response.data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || "Failed to get check-in records"
      );
    }
  }
);

// 初始狀態
const initialState = {
  todayCheckedIn: false,
  checkInHistory: [],
  loading: false,
  error: null,
};

// 創建 slice
const checkInSlice = createSlice({
  name: "checkIn",
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // 執行打卡
      .addCase(performCheckIn.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(performCheckIn.fulfilled, (state) => {
        state.loading = false;
        state.todayCheckedIn = true;
      })
      .addCase(performCheckIn.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // 獲取今日狀態
      .addCase(getTodayStatus.pending, (state) => {
        state.loading = true;
      })
      .addCase(getTodayStatus.fulfilled, (state, action) => {
        state.loading = false;
        state.todayCheckedIn = action.payload;
      })
      .addCase(getTodayStatus.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // 獲取打卡歷史
      .addCase(getMyCheckIns.pending, (state) => {
        state.loading = true;
      })
      .addCase(getMyCheckIns.fulfilled, (state, action) => {
        state.loading = false;
        state.checkInHistory = action.payload;
      })
      .addCase(getMyCheckIns.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { clearError } = checkInSlice.actions;
export default checkInSlice.reducer;
