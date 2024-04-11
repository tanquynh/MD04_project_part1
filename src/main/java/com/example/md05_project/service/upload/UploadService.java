package com.example.md05_project.service.upload;

import com.example.md05_project.exception.CustomException;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    String uploadImage(MultipartFile file) throws CustomException;
}
