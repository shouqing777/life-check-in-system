package com.lifecheckin.backend.service;

import com.lifecheckin.backend.model.User;
import com.lifecheckin.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserService userService; // 改用 UserService 代替直接使用 UserRepository

    @Override
    public UserDetails loadUserByUsername(String username) {
        // 輸入驗證
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Attempted to load user with null or empty username");
            throw new UsernameNotFoundException("Username cannot be empty");
        }

        logger.info("Loading user details for username: {}", username);

        // 從 UserService 獲取使用者
        Optional<User> userOpt = userService.findByUsername(username);
        User user = userOpt.orElseThrow(() -> {
            logger.warn("User not found with username: {}", username);
            return new UsernameNotFoundException("User not found with username: " + username);
        });

        // 處理角色
        List<String> roles = parseRoles(user.getRoles());

        // 構建 UserDetails
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(roles.toArray(new String[0]))
                .build();

        logger.info("User details loaded successfully for username: {}", username);
        return userDetails;
    }

    // 輔助方法：解析角色字串
    private List<String> parseRoles(String roles) {
        if (roles == null || roles.trim().isEmpty()) {
            logger.warn("Roles are empty for user, assigning default role: ROLE_USER");
            return Collections.singletonList("ROLE_USER"); // 預設角色
        }
        return Arrays.stream(roles.split(","))
                .map(String::trim) // 移除多餘空格
                .filter(role -> !role.isEmpty()) // 過濾空角色
                .collect(Collectors.toList());
    }
}