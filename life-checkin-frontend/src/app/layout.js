"use client";

import { Inter } from "next/font/google";
import "./globals.css";
import { Provider } from "react-redux";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";
import store from "@/redux/store";
import Navbar from "@/components/ui/Navbar";

// 創建一個 React Query 客戶端實例
const queryClient = new QueryClient();

// 設置 MUI 主題
const theme = createTheme({
  palette: {
    primary: {
      main: "#1976d2",
    },
    secondary: {
      main: "#f50057",
    },
  },
});

// 設置字體
const inter = Inter({ subsets: ["latin"] });

export default function RootLayout({ children }) {
  return (
    <html lang="zh-TW">
      <body className={inter.className}>
        {/* Redux Provider */}
        <Provider store={store}>
          {/* React Query Provider */}
          <QueryClientProvider client={queryClient}>
            {/* MUI Theme Provider */}
            <ThemeProvider theme={theme}>
              {/* CssBaseline 重置 CSS */}
              <CssBaseline />
              {/* 導航欄 */}
              <Navbar />
              {/* 頁面內容 */}
              <main>{children}</main>
            </ThemeProvider>
          </QueryClientProvider>
        </Provider>
      </body>
    </html>
  );
}
