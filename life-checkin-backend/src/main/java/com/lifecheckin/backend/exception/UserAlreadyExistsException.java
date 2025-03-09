package com.lifecheckin.backend.exception;

/**
 * 用戶已存在異常
 * 當嘗試創建已存在的用戶時拋出此異常
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}