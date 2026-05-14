package com.example.backend.service.impl;

import com.example.backend.entity.SysUser;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.AuthService;
import com.example.backend.utils.MockTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int NORMAL_STATUS = 1;
    private static final String ROLE_STUDENT = "STUDENT";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final Set<String> REVIEWER_OR_ADMIN = Set.of("REVIEWER", "ADMIN");

    private final UserMapper userMapper;

    @Override
    public SysUser getEnabledUser(String authorization) {
        Long userId = MockTokenUtils.parseUserId(authorization);
        SysUser user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != NORMAL_STATUS) {
            throw new RuntimeException("用户已被禁用");
        }
        return user;
    }

    @Override
    public SysUser getEnabledStudent(String authorization, String forbiddenMessage) {
        SysUser user = getEnabledUser(authorization);
        if (!ROLE_STUDENT.equals(user.getRole())) {
            throw new RuntimeException(forbiddenMessage);
        }
        return user;
    }

    @Override
    public SysUser getEnabledAdmin(String authorization, String forbiddenMessage) {
        SysUser user = getEnabledUser(authorization);
        if (!ROLE_ADMIN.equals(user.getRole())) {
            throw new RuntimeException(forbiddenMessage);
        }
        return user;
    }

    @Override
    public SysUser getEnabledReviewerOrAdmin(String authorization, String forbiddenMessage) {
        SysUser user = getEnabledUser(authorization);
        if (!REVIEWER_OR_ADMIN.contains(user.getRole())) {
            throw new RuntimeException(forbiddenMessage);
        }
        return user;
    }
}
