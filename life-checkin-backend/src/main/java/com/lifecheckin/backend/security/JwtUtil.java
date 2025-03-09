package com.lifecheckin.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具類
 * 處理JWT令牌的生成、解析和驗證
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration:86400000}") // 默認1天
    private Long expiration;

    // 根據密鑰字串生成簽名密鑰
    private Key getSigningKey() {
        byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 從令牌中提取用戶名
     * @param token JWT令牌
     * @return 用戶名
     */
    public String extractUsername(String token) {
        logger.debug("從令牌中提取用戶名");
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 從令牌中提取過期時間
     * @param token JWT令牌
     * @return 過期時間
     */
    public Date extractExpiration(String token) {
        logger.debug("從令牌中提取過期時間");
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 提取特定聲明
     * @param token JWT令牌
     * @param claimsResolver 聲明解析函數
     * @return 聲明值
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 提取所有聲明
     * @param token JWT令牌
     * @return 所有聲明
     */
    private Claims extractAllClaims(String token) {
        try {
            logger.debug("開始提取令牌中的所有聲明");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            logger.debug("令牌聲明提取成功");
            return claims;
        } catch (Exception e) {
            logger.error("提取令牌聲明時發生錯誤: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 檢查令牌是否過期
     * @param token JWT令牌
     * @return 是否過期
     */
    private Boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            boolean isExpired = expiration.before(new Date());
            logger.debug("檢查令牌是否過期: {}", isExpired);
            return isExpired;
        } catch (Exception e) {
            logger.error("檢查令牌過期時發生錯誤: {}", e.getMessage(), e);
            return true; // 默認視為過期
        }
    }

    /**
     * 生成令牌
     * @param username 用戶名
     * @return JWT令牌
     */
    public String generateToken(String username) {
        logger.info("開始為用戶生成令牌: {}", username);
        Map<String, Object> claims = new HashMap<>();
        String token = createToken(claims, username);
        logger.debug("令牌生成成功，長度: {}", token.length());
        return token;
    }

    /**
     * 創建令牌
     * @param claims 額外聲明
     * @param subject 主題（用戶名）
     * @return JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject) {
        try {
            Date now = new Date(System.currentTimeMillis());
            Date expiryDate = new Date(System.currentTimeMillis() + expiration);

            logger.debug("創建令牌，主題: {}, 過期時間: {}", subject, expiryDate);

            if (secretString == null || secretString.isEmpty()) {
                logger.error("JWT密鑰為空，請檢查配置!");
                throw new IllegalStateException("JWT secret key is empty or null");
            }

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            logger.error("創建令牌時發生錯誤: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 驗證令牌並提取用戶名
     * @param token JWT令牌
     * @return 用戶名
     */
    public String validateTokenAndGetUsername(String token) {
        logger.debug("開始驗證令牌並提取用戶名");
        if (token == null || token.isEmpty()) {
            logger.error("令牌為空");
            return null;
        }

        try {
            if (isTokenExpired(token)) {
                logger.warn("令牌已過期");
                return null;
            }
            String username = extractUsername(token);
            logger.debug("令牌驗證成功，用戶名: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("令牌驗證失敗: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 驗證令牌
     * @param token JWT令牌
     * @param username 需要驗證的用戶名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        logger.debug("驗證令牌是否對指定用戶有效: {}", username);
        final String extractedUsername = extractUsername(token);
        boolean isValid = (extractedUsername.equals(username) && !isTokenExpired(token));
        logger.debug("令牌驗證結果: {}", isValid);
        return isValid;
    }
}