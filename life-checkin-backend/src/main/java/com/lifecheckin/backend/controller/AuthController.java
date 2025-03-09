package com.lifecheckin.backend.controller;

import com.lifecheckin.backend.dto.LoginRequest;
import com.lifecheckin.backend.dto.LoginResponse;
import com.lifecheckin.backend.dto.RegisterRequest;
import com.lifecheckin.backend.model.User;
import com.lifecheckin.backend.security.JwtUtil;
import com.lifecheckin.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 認證控制器
 * 處理用戶註冊和登入相關的請求
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "API for user registration and login")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 用戶註冊
     * @param registerRequest 註冊請求
     * @return 註冊結果
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user with username, email and password")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "409", description = "Username or email already exists")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        logger.info("開始註冊新用戶：{}", registerRequest.getUsername());

        try {
            // 參數校驗
            if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
                logger.error("註冊失敗：用戶名為空");
                return ResponseEntity.badRequest().body("用戶名不能為空");
            }

            if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
                logger.error("註冊失敗：郵箱為空");
                return ResponseEntity.badRequest().body("郵箱不能為空");
            }

            if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
                logger.error("註冊失敗：密碼不符合要求");
                return ResponseEntity.badRequest().body("密碼長度至少為6位");
            }

            User user = userService.registerUser(
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword()
            );

            logger.info("用戶註冊成功：{}, 用戶ID: {}", user.getUsername(), user.getId());
            return ResponseEntity.status(201).body("註冊成功");
        } catch (Exception e) {
            logger.error("註冊過程中發生錯誤", e);
            return ResponseEntity.status(500).body("註冊失敗：" + e.getMessage());
        }
    }

    /**
     * 用戶登入
     * @param loginRequest 登入請求
     * @return 登入結果（含JWT令牌）
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid username or password")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        logger.info("開始嘗試用戶登入：{}", loginRequest.getUsername());

        try {
            // 參數校驗
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                logger.error("登入失敗：用戶名為空");
                return ResponseEntity.status(401).body("用戶名不能為空");
            }

            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                logger.error("登入失敗：密碼為空");
                return ResponseEntity.status(401).body("密碼不能為空");
            }

            logger.debug("開始進行認證，用戶名：{}", loginRequest.getUsername());

            // 使用Spring Security進行認證
            Authentication authentication = null;
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(),
                                loginRequest.getPassword()
                        )
                );
                logger.info("用戶認證成功：{}", loginRequest.getUsername());
            } catch (BadCredentialsException e) {
                logger.error("認證失敗：錯誤的憑證，用戶名或密碼錯誤，用戶名：{}", loginRequest.getUsername());
                return ResponseEntity.status(401).body("用戶名或密碼錯誤");
            } catch (AuthenticationException e) {
                logger.error("認證過程中發生其他錯誤：{}", e.getMessage(), e);
                return ResponseEntity.status(401).body("認證失敗：" + e.getMessage());
            }

            // 存儲認證信息
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 生成JWT令牌
            logger.debug("開始生成JWT令牌，用戶名：{}", loginRequest.getUsername());
            String token = jwtUtil.generateToken(loginRequest.getUsername());
            logger.info("JWT令牌生成成功，長度：{}", token.length());

            // 返回令牌
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (Exception e) {
            logger.error("登入過程中發生未預期錯誤", e);
            return ResponseEntity.status(500).body("登入失敗：" + e.getMessage());
        }
    }
}