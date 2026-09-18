package com.smartplacementai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ResumeStorageService {

    @Value("${app.storage.resume-dir:./storage/resumes}")
    private String storageDir;

    public String store(MultipartFile file, Long userId) throws IOException {
        Path baseDir = Paths.get(storageDir, String.valueOf(userId));
        Files.createDirectories(baseDir);

        String extension = getExtension(file.getOriginalFilename());
        String storageKey = userId + "/" + UUID.randomUUID() + extension;

        Path targetPath = Paths.get(storageDir, storageKey);
        file.transferTo(targetPath);

        return storageKey;
    }

    public InputStream retrieve(String storageKey) throws IOException {
        Path path = Paths.get(storageDir, storageKey);
        return Files.newInputStream(path);
    }

    public void delete(String storageKey) throws IOException {
        Path path = Paths.get(storageDir, storageKey);
        Files.deleteIfExists(path);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.'));
    }
}