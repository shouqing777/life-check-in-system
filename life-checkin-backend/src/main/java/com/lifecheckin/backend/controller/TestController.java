package com.lifecheckin.backend.controller;

import com.lifecheckin.backend.model.User;
import com.lifecheckin.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping("/auth")
    public ResponseEntity<?> testAuth() {
        // 僅用於測試認證是否正常工作
        return ResponseEntity.ok("認證成功!");
    }

    @GetMapping("/create-test-user")
    public ResponseEntity<?> createTestUser() {
        try {
            // 創建一個簡單的測試用戶
            User user = new User();
            user.setUsername("test123");
            user.setEmail("test123@example.com");

            // 輸出明文密碼和加密密碼進行對比
            String rawPassword = "test123";
            String encodedPassword = encoder.encode(rawPassword);

            user.setPassword(encodedPassword);
            userService.createUser(user);

            return ResponseEntity.ok("測試用戶創建成功! 用戶名: test123, 密碼: test123, 加密密碼: " + encodedPassword);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("創建測試用戶失敗: " + e.getMessage());
        }
    }
}