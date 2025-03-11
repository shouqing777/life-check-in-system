import Link from "next/link";
import { Button, Container, Typography, Box } from "@mui/material";

export default function Home() {
  return (
    <Container>
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "calc(100vh - 64px)", // 減去 AppBar 高度
          py: 4,
          textAlign: "center",
        }}
      >
        <Typography variant="h2" component="h1" gutterBottom>
          生命狀態打卡系統
        </Typography>
        <Typography variant="h5" color="textSecondary" paragraph>
          簡單高效的打卡系統，記錄每一天的生活狀態
        </Typography>
        <Box sx={{ mt: 4 }}>
          <Button
            variant="contained"
            color="primary"
            size="large"
            component={Link}
            href="/login"
            sx={{ mx: 1 }}
          >
            立即登入
          </Button>
          <Button
            variant="outlined"
            color="primary"
            size="large"
            component={Link}
            href="/register"
            sx={{ mx: 1 }}
          >
            註冊帳號
          </Button>
        </Box>
      </Box>
    </Container>
  );
}
