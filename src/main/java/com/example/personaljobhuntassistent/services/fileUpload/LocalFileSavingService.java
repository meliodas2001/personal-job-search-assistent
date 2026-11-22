
package com.example.personaljobhuntassistent.services.fileUpload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileSavingService implements FileSavingService {

    @Value("${com.file.local.save.path}")
    private String filePath;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("pdf", "doc", "docx");

    @Override
    public String uploadFile(MultipartFile file) {

        // 1. Validate file
        validateFile(file);

        try {
            // 2. Prepare upload directory
            Path uploadPath = Paths.get(filePath)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(uploadPath);

            // 3. Extract and sanitize filename
            String originalFilename = file.getOriginalFilename();

            assert originalFilename != null;
            String filename = Paths.get(originalFilename)
                    .getFileName()
                    .toString();

            // 5. Resolve destination path
            Path destination = uploadPath.resolve(filename).normalize();

            if (!destination.startsWith(uploadPath)) {
                throw new IllegalArgumentException("Invalid file path");
            }

            // 6. Save file
            Files.copy(file.getInputStream(), destination);

            log.info("File uploaded successfully: {}", filename);

            return filename;

        } catch (IOException e) {
            log.error("Error while saving uploaded file", e);
            throw new RuntimeException("Failed to save file", e);
        }
    }

    private void validateFile(MultipartFile file) {

        if (file == null) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size cannot exceed 5 MB");
        }

        // Validate filename
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Invalid filename");
        }

        // Validate extension
        String extension = StringUtils.getFilenameExtension(originalFilename);

        if (extension == null ||
                !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Only PDF, DOC and DOCX files are allowed"
            );
        }

        // Basic MIME type validation
        String contentType = file.getContentType();

        if (contentType == null ||
                !Set.of(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                ).contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file content type");
        }
    }
}