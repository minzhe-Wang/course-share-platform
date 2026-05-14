package com.example.backend.utils;

import org.springframework.util.StringUtils;

public class MockTokenUtils {

    private static final String TOKEN_PREFIX = "mock-token-";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    private MockTokenUtils() {
    }

    public static Long parseUserId(String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(AUTHORIZATION_PREFIX)) {
            throw new RuntimeException("未登录");
        }

        String token = authorization.substring(AUTHORIZATION_PREFIX.length()).trim();
        if (!token.startsWith(TOKEN_PREFIX)) {
            throw new RuntimeException("登录凭证无效");
        }

        try {
            return Long.valueOf(token.substring(TOKEN_PREFIX.length()));
        } catch (NumberFormatException e) {
            throw new RuntimeException("登录凭证无效");
        }
    }

    public static String buildToken(Long userId) {
        return TOKEN_PREFIX + userId;
    }
}
