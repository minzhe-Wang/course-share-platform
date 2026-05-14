package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.UserStatusDTO;
import com.example.backend.service.AdminUserService;
import com.example.backend.vo.AdminUserListItemVO;
import com.example.backend.vo.PageResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/api/admin/users")
    public Result<PageResultVO<AdminUserListItemVO>> listUsers(@RequestParam(required = false) Integer pageNum,
                                                               @RequestParam(required = false) Integer pageSize,
                                                               @RequestParam(required = false) String keyword,
                                                               @RequestParam(required = false) String role,
                                                               @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(adminUserService.listUsers(pageNum, pageSize, keyword, role, authorization));
    }

    @PutMapping("/api/admin/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id,
                                         @RequestBody @Valid UserStatusDTO userStatusDTO,
                                         @RequestHeader(value = "Authorization", required = false) String authorization) {
        adminUserService.updateUserStatus(id, userStatusDTO, authorization);
        return Result.success();
    }
}
