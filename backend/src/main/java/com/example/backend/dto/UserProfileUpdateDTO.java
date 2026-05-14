package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileUpdateDTO {

    @Size(max = 30, message = "昵称长度不能超过30位")
    private String nickname;

    @Size(max = 20, message = "手机号长度不能超过20位")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100位")
    private String email;

    @Size(max = 255, message = "头像地址长度不能超过255位")
    private String avatar;
}
