package com.lifecheckin.backend.dto;

import lombok.Data;

/**
 * 註冊請求DTO
 * 用於接收用戶註冊請求的數據
 */
@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}