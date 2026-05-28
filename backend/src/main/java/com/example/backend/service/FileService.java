package com.example.backend.service;

import com.example.backend.vo.FileUploadVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileUploadVO upload(MultipartFile file, String authorization);

    String getDownloadUrl(String fileKey);

    void writeObject(String fileKey, HttpServletResponse response);
}
