package com.lifecheckin.backend.exception;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 標準錯誤響應類
 * 用於在API出錯時返回統一格式的錯誤信息
 */
@Data
public class ErrorResponse {
    private int status;          // HTTP狀態碼
    private String message;      // 錯誤信息
    private LocalDateTime timestamp;  // 錯誤發生時間
    private Object details;      // 錯誤詳情（可選）

    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
}