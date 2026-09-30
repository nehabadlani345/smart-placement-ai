package com.smartplacementai.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

@Component
public class FileValidationUtil {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".docx");
    private static final long MAX_SIZE_BYTES = 10L * 1024 * 1024;

    private static final byte[] PDF_MAGIC = {0x25, 0x50, 0x44, 0x46};   // "%PDF"
    private static final byte[] ZIP_MAGIC = {0x50, 0x4B, 0x03, 0x04};   // DOCX is a zip container

    public void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("File exceeds 10MB limit");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || ALLOWED_EXTENSIONS.stream().noneMatch(ext -> filename.toLowerCase().endsWith(ext))) {
            throw new IllegalArgumentException("Only PDF and DOCX files are allowed");
        }

        try {
            validateMagicBytes(file);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read file to verify its type", e);
        }
    }

    private void validateMagicBytes(MultipartFile file) throws IOException {
        byte[] header = new byte[4];
        try (var in = file.getInputStream()) {
            int read = in.readNBytes(header, 0, 4);
            if (read < 4) {
                throw new IllegalArgumentException("File is too small or corrupted");
            }
        }
        boolean isPdf = Arrays.equals(header, PDF_MAGIC);
        boolean isDocx = Arrays.equals(header, ZIP_MAGIC);
        if (!isPdf && !isDocx) {
            throw new IllegalArgumentException(
                    "File content doesn't match a PDF or DOCX file - the extension may be spoofed");
        }
    }
}
