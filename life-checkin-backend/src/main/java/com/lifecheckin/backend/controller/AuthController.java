package com.lifecheckin.backend.controller;

import com.lifecheckin.backend.dto.LoginRequest;
import com.lifecheckin.backend.dto.LoginResponse;
import com.lifecheckin.backend.model.User;
import com.lifecheckin.backend.security.JwtUtil;
import com.lifecheckin.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "API for user registration and login")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user with username and password")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or username already exists")
    public ResponseEntity<?> register(@RequestBody LoginRequest registerRequest) {
        try {
            User user = userService.registerUser(registerRequest.getUsername(), registerRequest.getPassword());
            return ResponseEntity.status(201).body("註冊成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid username or password")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userService.findByUsername(loginRequest.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername());
                return ResponseEntity.ok(new LoginResponse(token));
            }
        }
        return ResponseEntity.status(401).body("Invalid username or password");
    }
}