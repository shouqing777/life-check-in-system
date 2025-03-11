import LoginForm from "@/components/auth/LoginForm";
import { Container, Box, Typography } from "@mui/material";

export default function LoginPage() {
  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "calc(100vh - 64px)",
          py: 4,
        }}
      >
        <LoginForm />
      </Box>
    </Container>
  );
}
