package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminTagVO {

    private Long id;
    private String name;
    private String type;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
