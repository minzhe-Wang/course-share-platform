package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.service.FileService;
import com.example.backend.vo.FileUploadVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public Result<FileUploadVO> upload(@RequestParam("file") MultipartFile file,
                                       @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(fileService.upload(file, authorization));
    }

    @GetMapping("/download")
    public void download(@RequestParam("fileKey") String fileKey, HttpServletResponse response) {
        fileService.writeObject(fileKey, response);
    }
}
