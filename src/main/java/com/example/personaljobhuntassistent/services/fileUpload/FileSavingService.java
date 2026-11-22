package com.example.personaljobhuntassistent.services.fileUpload;


import org.springframework.web.multipart.MultipartFile;

public interface FileSavingService
{
    String uploadFile(MultipartFile file);
}
