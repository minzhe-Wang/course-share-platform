package com.example.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginVO {

    private Long userId;
    private String username;
    private String nickname;
    private String role;
    private String token;
}