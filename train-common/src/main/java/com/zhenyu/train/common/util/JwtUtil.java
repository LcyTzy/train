package com.zhenyu.train.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtil {

    private static final String SECRET = "zhenyu-train-12306-secret-key-2024-must-be-at-least-256-bits";
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成Token
     */
    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(KEY)
                .compact();
    }

    /**
     * 解析Token
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Token解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        return claims.get("userId", Long.class);
    }

    /**
     * 获取用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        return claims.getSubject();
    }

    /**
     * 验证Token是否过期
     */
    public static boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return true;
        }
        return claims.getExpiration().before(new Date());
    }

    /**
     * 验证Token是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims != null && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
