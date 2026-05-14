package com.example.backend.service;

import com.example.backend.entity.SysUser;

public interface AuthService {

    SysUser getEnabledUser(String authorization);

    SysUser getEnabledStudent(String authorization, String forbiddenMessage);

    SysUser getEnabledAdmin(String authorization, String forbiddenMessage);

    SysUser getEnabledReviewerOrAdmin(String authorization, String forbiddenMessage);
}
