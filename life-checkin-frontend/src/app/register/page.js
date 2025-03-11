import RegisterForm from "@/components/auth/RegisterForm";
import { Container, Box, Typography } from "@mui/material";

export default function RegisterPage() {
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
        <RegisterForm />
      </Box>
    </Container>
  );
}
