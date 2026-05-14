package com.example.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMeVO {

    private Long userId;
    private String username;
    private String nickname;
    private String role;
    private String phone;
    private String email;
    private String avatar;
}
