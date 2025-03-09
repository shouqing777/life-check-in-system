package com.lifecheckin.backend.service;

import com.lifecheckin.backend.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        logger.debug("⭐ loadUserByUsername 方法開始執行，username: {}", username);

        // 輸入驗證
        if (username == null || username.trim().isEmpty()) {
            logger.error("❌ 使用者名稱為空或 null: {}", username);
            throw new UsernameNotFoundException("Username cannot be empty");
        }

        logger.info("🔍 開始從 UserService 獲取使用者，username: {}", username);

        try {
            // 從 UserService 獲取使用者
            Optional<User> userOpt = userService.findByUsername(username);

            if (!userOpt.isPresent()) {
                logger.error("❌ 使用者不存在，username: {}", username);
                throw new UsernameNotFoundException("User not found with username: " + username);
            }

            User user = userOpt.get();
            logger.debug("✅ 已成功獲取使用者，user ID: {}", user.getId());

            // 處理角色
            List<String> roles = parseRoles(user.getRoles());
            logger.debug("📋 使用者角色列表: {}", roles);

            // 構建 UserDetails - 使用 authorities() 而不是 roles()
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities(roles.stream()
                            .map(role -> new SimpleGrantedAuthority(role))
                            .collect(Collectors.toList()))
                    .build();

            logger.info("✅ UserDetails 構建成功，username: {}", username);
            return userDetails;
        } catch (UsernameNotFoundException e) {
            logger.error("❌ 使用者不存在異常，username: {}", username, e);
            throw e;
        } catch (Exception e) {
            logger.error("❌ 載入使用者過程中發生未預期錯誤，username: {}", username, e);
            throw new RuntimeException("Error loading user: " + e.getMessage(), e);
        }
    }

    // 輔助方法：解析角色字串
    private List<String> parseRoles(String roles) {
        logger.debug("⭐ 開始解析角色，roles: {}", roles);

        if (roles == null || roles.trim().isEmpty()) {
            logger.warn("⚠️ 角色為空，將分配預設角色: ROLE_USER");
            return Collections.singletonList("ROLE_USER"); // 預設角色
        }

        List<String> parsedRoles = Arrays.stream(roles.split(","))
                .map(String::trim) // 移除多餘空格
                .filter(role -> !role.isEmpty()) // 過濾空角色
                .collect(Collectors.toList());

        logger.debug("✅ 角色解析完成，結果: {}", parsedRoles);
        return parsedRoles;
    }
}