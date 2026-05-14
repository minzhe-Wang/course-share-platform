package com.example.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    private Long id;
    private String name;
    private String type;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
