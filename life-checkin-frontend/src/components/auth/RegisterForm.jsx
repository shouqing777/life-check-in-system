"use client";

import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { register } from "@/redux/slices/authSlice";
import { useRouter } from "next/navigation";
import {
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  CircularProgress,
} from "@mui/material";

const RegisterForm = () => {
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [passwordError, setPasswordError] = useState("");
  const dispatch = useDispatch();
  const router = useRouter();
  const { loading, error } = useSelector((state) => state.auth);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });

    // 檢查密碼是否匹配
    if (e.target.name === "confirmPassword" || e.target.name === "password") {
      if (
        formData.password !== e.target.value &&
        e.target.name === "confirmPassword"
      ) {
        setPasswordError("密碼不匹配");
      } else if (
        e.target.name === "password" &&
        formData.confirmPassword &&
        formData.confirmPassword !== e.target.value
      ) {
        setPasswordError("密碼不匹配");
      } else {
        setPasswordError("");
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.password !== formData.confirmPassword) {
      setPasswordError("密碼不匹配");
      return;
    }

    const { confirmPassword, ...registerData } = formData;
    const resultAction = await dispatch(register(registerData));

    if (register.fulfilled.match(resultAction)) {
      router.push("/login");
    }
  };

  return (
    <Box sx={{ maxWidth: 400, mx: "auto", p: 2 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        註冊
      </Typography>

      {error && <Alert severity="error">{error}</Alert>}

      <form onSubmit={handleSubmit}>
        <TextField
          margin="normal"
          required
          fullWidth
          id="username"
          label="用戶名"
          name="username"
          autoComplete="username"
          autoFocus
          value={formData.username}
          onChange={handleChange}
        />
        <TextField
          margin="normal"
          required
          fullWidth
          id="email"
          label="電子郵件"
          name="email"
          autoComplete="email"
          type="email"
          value={formData.email}
          onChange={handleChange}
        />
        <TextField
          margin="normal"
          required
          fullWidth
          name="password"
          label="密碼"
          type="password"
          id="password"
          autoComplete="new-password"
          value={formData.password}
          onChange={handleChange}
        />
        <TextField
          margin="normal"
          required
          fullWidth
          name="confirmPassword"
          label="確認密碼"
          type="password"
          id="confirmPassword"
          error={!!passwordError}
          helperText={passwordError}
          value={formData.confirmPassword}
          onChange={handleChange}
        />
        <Button
          type="submit"
          fullWidth
          variant="contained"
          sx={{ mt: 3, mb: 2 }}
          disabled={loading || !!passwordError}
        >
          {loading ? <CircularProgress size={24} /> : "註冊"}
        </Button>
      </form>

      <Typography variant="body2" textAlign="center">
        已有帳號？{" "}
        <Button
          variant="text"
          onClick={() => router.push("/login")}
          sx={{ textTransform: "none" }}
        >
          登入
        </Button>
      </Typography>
    </Box>
  );
};

export default RegisterForm;
