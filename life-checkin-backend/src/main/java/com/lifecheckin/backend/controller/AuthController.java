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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
        User user = userService.registerUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword()
        );
        return ResponseEntity.status(201).body("註冊成功");
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
        try {
            // 使用Spring Security進行認證
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 存儲認證信息
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 生成JWT令牌
            String token = jwtUtil.generateToken(loginRequest.getUsername());

            // 返回令牌
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}