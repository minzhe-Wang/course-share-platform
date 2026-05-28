package com.example.backend.service.impl;

import com.example.backend.entity.SysUser;
import com.example.backend.exception.BusinessException;
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
            throw new BusinessException(401, "\u7528\u6237\u4e0d\u5b58\u5728");
        }
        if (user.getStatus() == null || user.getStatus() != NORMAL_STATUS) {
            throw new BusinessException(403, "\u7528\u6237\u5df2\u88ab\u7981\u7528");
        }
        return user;
    }

    @Override
    public SysUser getEnabledStudent(String authorization, String forbiddenMessage) {
        SysUser user = getEnabledUser(authorization);
        if (!ROLE_STUDENT.equals(user.getRole())) {
            throw new BusinessException(403, forbiddenMessage);
        }
        return user;
    }

    @Override
    public SysUser getEnabledAdmin(String authorization, String forbiddenMessage) {
        SysUser user = getEnabledUser(authorization);
        if (!ROLE_ADMIN.equals(user.getRole())) {
            throw new BusinessException(403, forbiddenMessage);
        }
        return user;
    }

    @Override
    public SysUser getEnabledReviewerOrAdmin(String authorization, String forbiddenMessage) {
        SysUser user = getEnabledUser(authorization);
        if (!REVIEWER_OR_ADMIN.contains(user.getRole())) {
            throw new BusinessException(403, forbiddenMessage);
        }
        return user;
    }
}
