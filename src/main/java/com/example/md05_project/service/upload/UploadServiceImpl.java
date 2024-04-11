package com.example.md05_project.service.upload;

import com.example.md05_project.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class UploadServiceImpl implements UploadService{
    @Value("${server.port}")
    private String port;
    @Override
    public String uploadImage(MultipartFile file) throws CustomException {
        String fileName= file.getOriginalFilename();
        try {
            String pathUpload = "//Users//admin//Desktop//MODULE_05//MD05_PROJECT//src//main//resources//uploads//";
            FileCopyUtils.copy(file.getBytes(),new File(pathUpload +fileName));
            return "http://localhost:"+port+"/"+fileName;
        } catch (IOException e) {
            throw new CustomException("Error saving image file "+e.getMessage());
        }
    }
}
