package com.example.backend.controller;

import com.example.backend.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String test() {
        return "backend docker success";
    }

    @GetMapping("/api/error-test")
    public Result<String> errorTest() {
        throw new RuntimeException("这是一个测试异常");
    }
}