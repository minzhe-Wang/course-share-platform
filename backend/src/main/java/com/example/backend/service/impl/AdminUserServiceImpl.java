package com.example.backend.service.impl;

import com.example.backend.dto.UserStatusDTO;
import com.example.backend.entity.SysUser;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.AdminUserService;
import com.example.backend.service.AuthService;
import com.example.backend.vo.AdminUserListItemVO;
import com.example.backend.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private static final Set<String> ROLES = Set.of("STUDENT", "REVIEWER", "ADMIN");

    private final UserMapper userMapper;
    private final AuthService authService;

    @Override
    public PageResultVO<AdminUserListItemVO> listUsers(Integer pageNum, Integer pageSize, String keyword,
                                                       String role, String authorization) {
        authService.getEnabledAdmin(authorization, "无权限管理用户");
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        int offset = (safePageNum - 1) * safePageSize;
        String normalizedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        String normalizedRole = normalizeNullableRole(role);

        Long total = userMapper.countUsers(normalizedKeyword, normalizedRole);
        List<AdminUserListItemVO> list = userMapper.findUsers(normalizedKeyword, normalizedRole, safePageSize, offset);
        return PageResultVO.<AdminUserListItemVO>builder()
                .total(total)
                .list(list)
                .build();
    }

    @Override
    public void updateUserStatus(Long id, UserStatusDTO userStatusDTO, String authorization) {
        SysUser admin = authService.getEnabledAdmin(authorization, "无权限管理用户");
        if (id == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!Integer.valueOf(0).equals(userStatusDTO.getStatus()) && !Integer.valueOf(1).equals(userStatusDTO.getStatus())) {
            throw new RuntimeException("用户状态不支持");
        }
        SysUser targetUser = userMapper.findById(id);
        if (targetUser == null) {
            throw new RuntimeException("用户不存在");
        }
        if (admin.getId().equals(id) && userStatusDTO.getStatus() == 0) {
            throw new RuntimeException("不能禁用当前管理员");
        }
        userMapper.updateStatus(id, userStatusDTO.getStatus());
    }

    private String normalizeNullableRole(String role) {
        if (!StringUtils.hasText(role)) {
            return null;
        }
        String normalizedRole = role.trim().toUpperCase();
        if (!ROLES.contains(normalizedRole)) {
            throw new RuntimeException("用户角色不支持");
        }
        return normalizedRole;
    }
}
