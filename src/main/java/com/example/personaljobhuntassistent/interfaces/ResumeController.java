package com.example.personaljobhuntassistent.interfaces;


import com.example.personaljobhuntassistent.services.fileUpload.FileSavingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resume")
@Slf4j
public class ResumeController
{
    @Autowired
    private FileSavingService fileSavingService;

    @PostMapping()
    private ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file)
    {
        log.info("file name to be uploaded {}",file.getName());
        fileSavingService.uploadFile(file);
        return ResponseEntity.accepted()
                .body("Resume uploaded successfully");
    }
}
