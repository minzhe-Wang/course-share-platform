package com.example.backend.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResultVO<T> {

    private Long total;
    private List<T> list;
}
