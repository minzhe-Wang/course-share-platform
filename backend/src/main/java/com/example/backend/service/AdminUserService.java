package com.example.backend.service;

import com.example.backend.dto.UserStatusDTO;
import com.example.backend.vo.AdminUserListItemVO;
import com.example.backend.vo.PageResultVO;

public interface AdminUserService {

    PageResultVO<AdminUserListItemVO> listUsers(Integer pageNum, Integer pageSize, String keyword,
                                                String role, String authorization);

    void updateUserStatus(Long id, UserStatusDTO userStatusDTO, String authorization);
}
