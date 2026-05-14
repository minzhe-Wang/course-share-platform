package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserListItemVO {

    private Long id;
    private String username;
    private String nickname;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
}
