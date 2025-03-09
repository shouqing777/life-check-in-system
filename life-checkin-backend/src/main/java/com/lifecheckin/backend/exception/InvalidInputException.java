package com.lifecheckin.backend.exception;

/**
 * 無效輸入異常
 * 當用戶提交的數據不符合業務規則時拋出此異常
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}