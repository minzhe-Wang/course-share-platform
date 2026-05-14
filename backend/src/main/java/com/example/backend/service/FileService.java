package com.example.backend.service;

import com.example.backend.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileUploadVO upload(MultipartFile file, String authorization);
}
