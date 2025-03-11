"use client";

import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { performCheckIn, getTodayStatus } from "@/redux/slices/checkInSlice";
import { Button, CircularProgress, Snackbar, Alert } from "@mui/material";

const CheckInButton = () => {
  const dispatch = useDispatch();
  const { todayCheckedIn, loading, error } = useSelector(
    (state) => state.checkIn
  );
  const [showSuccess, setShowSuccess] = useState(false);

  // 在組件加載時獲取今日打卡狀態
  useState(() => {
    dispatch(getTodayStatus());
  }, [dispatch]);

  const handleCheckIn = async () => {
    const resultAction = await dispatch(performCheckIn());
    if (performCheckIn.fulfilled.match(resultAction)) {
      setShowSuccess(true);
    }
  };

  return (
    <>
      <Button
        variant="contained"
        color={todayCheckedIn ? "success" : "primary"}
        size="large"
        onClick={handleCheckIn}
        disabled={loading || todayCheckedIn}
        sx={{
          py: 2,
          px: 4,
          fontSize: "1.2rem",
          borderRadius: "50px",
        }}
      >
        {loading ? (
          <CircularProgress size={24} color="inherit" />
        ) : todayCheckedIn ? (
          "今日已打卡"
        ) : (
          "立即打卡"
        )}
      </Button>

      {/* 成功提示 */}
      <Snackbar
        open={showSuccess}
        autoHideDuration={6000}
        onClose={() => setShowSuccess(false)}
      >
        <Alert severity="success" sx={{ width: "100%" }}>
          打卡成功！
        </Alert>
      </Snackbar>

      {/* 錯誤提示 */}
      <Snackbar
        open={!!error}
        autoHideDuration={6000}
        onClose={() => dispatch({ type: "checkIn/clearError" })}
      >
        <Alert severity="error" sx={{ width: "100%" }}>
          {error}
        </Alert>
      </Snackbar>
    </>
  );
};

export default CheckInButton;
