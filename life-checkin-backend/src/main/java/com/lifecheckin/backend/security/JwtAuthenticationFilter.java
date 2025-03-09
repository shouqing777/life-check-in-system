package com.lifecheckin.backend.security;

import com.lifecheckin.backend.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

/**
 * JWT認證過濾器
 * 從HTTP請求中提取JWT令牌並進行認證
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 從標頭取得 Token
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                // 沒有令牌，繼續過濾鏈
                filterChain.doFilter(request, response);
                return;
            }

            // 提取令牌
            String token = authHeader.substring(7);

            // 解析並驗證令牌
            String username = jwtUtil.validateTokenAndGetUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 使用UserDetailsService加載用戶數據
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 驗證令牌是否對該用戶有效
                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // 設置認證
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("User authenticated: {}", username);
                }
            }
        } catch (Exception e) {
            logger.error("JWT Authentication failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}