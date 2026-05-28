package com.example.backend.utils;

import com.example.backend.exception.BusinessException;
import org.springframework.util.StringUtils;

public final class MockTokenUtils {

    private static final String TOKEN_PREFIX = "mock-token-";
    private static final String BEARER_PREFIX = "Bearer ";

    private MockTokenUtils() {
    }

    public static String buildToken(Long userId) {
        return TOKEN_PREFIX + userId;
    }

    public static Long parseUserId(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            throw new BusinessException(401, "\u672a\u767b\u5f55");
        }
        if (!authorization.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(401, "\u767b\u5f55\u51ed\u8bc1\u65e0\u6548");
        }
        String token = authorization.substring(BEARER_PREFIX.length());
        if (!token.startsWith(TOKEN_PREFIX)) {
            throw new BusinessException(401, "\u767b\u5f55\u51ed\u8bc1\u65e0\u6548");
        }
        try {
            return Long.parseLong(token.substring(TOKEN_PREFIX.length()));
        } catch (NumberFormatException e) {
            throw new BusinessException(401, "\u767b\u5f55\u51ed\u8bc1\u65e0\u6548");
        }
    }
}
