"use client";

import { useEffect } from "react";
import { useSelector } from "react-redux";
import { useRouter } from "next/navigation";
import Container from "@mui/material/Container";
import Grid from "@mui/material/Grid2"; // 使用新的 Grid v2
import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import CheckInButton from "@/components/dashboard/CheckInButton";
import CheckInHistory from "@/components/dashboard/CheckInHistory";

export default function DashboardPage() {
  const router = useRouter();
  const { isAuthenticated } = useSelector((state) => state.auth);

  // 如果未登入，重定向到登入頁面
  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login");
    }
  }, [isAuthenticated, router]);

  // 未登入時不顯示儀表板內容
  if (!isAuthenticated) {
    return null;
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Grid container spacing={3}>
        {/* 打卡按鈕區域 */}
        <Grid xs={12}>
          {" "}
          {/* 注意：Grid v2 中不需要 item 屬性 */}
          <Paper
            sx={{
              p: 4,
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              justifyContent: "center",
              minHeight: 200,
            }}
          >
            <Typography variant="h5" color="textSecondary" gutterBottom>
              今日狀態打卡
            </Typography>
            <CheckInButton />
          </Paper>
        </Grid>

        {/* 打卡歷史記錄 */}
        <Grid xs={12}>
          {" "}
          {/* 注意：Grid v2 中不需要 item 屬性 */}
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6" gutterBottom component="div">
              打卡歷史記錄
            </Typography>
            <CheckInHistory />
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
}
