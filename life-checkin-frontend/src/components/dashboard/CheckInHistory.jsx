"use client";

import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getMyCheckIns } from "@/redux/slices/checkInSlice";
import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  CircularProgress,
  Box,
} from "@mui/material";

const CheckInHistory = () => {
  const dispatch = useDispatch();
  const { checkInHistory, loading, error } = useSelector(
    (state) => state.checkIn
  );

  useEffect(() => {
    dispatch(getMyCheckIns());
  }, [dispatch]);

  // 格式化日期時間
  const formatDateTime = (dateTimeStr) => {
    const date = new Date(dateTimeStr);
    return date.toLocaleString("zh-TW", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    });
  };

  if (loading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", p: 3 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Typography color="error" gutterBottom>
        無法加載打卡記錄：{error}
      </Typography>
    );
  }

  return (
    <Paper sx={{ width: "100%", overflow: "hidden" }}>
      <TableContainer sx={{ maxHeight: 440 }}>
        <Table stickyHeader aria-label="打卡記錄表">
          <TableHead>
            <TableRow>
              <TableCell>序號</TableCell>
              <TableCell>打卡時間</TableCell>
              <TableCell>狀態</TableCell>
              <TableCell>備註</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {checkInHistory.length > 0 ? (
              checkInHistory.map((record, index) => (
                <TableRow hover key={record.id}>
                  <TableCell>{index + 1}</TableCell>
                  <TableCell>{formatDateTime(record.checkinTime)}</TableCell>
                  <TableCell>{record.status}</TableCell>
                  <TableCell>{record.note || "-"}</TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={4} align="center">
                  暫無打卡記錄
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Paper>
  );
};

export default CheckInHistory;
