package com.smartplacementai.dto;

import java.time.LocalDateTime;

public class ResumeSummaryDto {
    private String id;
    private String originalFileName;
    private Integer version;
    private boolean active;
    private Long fileSizeBytes;
    private LocalDateTime uploadedAt;

    public ResumeSummaryDto(String id, String originalFileName, Integer version,
                             boolean active, Long fileSizeBytes, LocalDateTime uploadedAt) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.version = version;
        this.active = active;
        this.fileSizeBytes = fileSizeBytes;
        this.uploadedAt = uploadedAt;
    }

    public String getId() { return id; }
    public String getOriginalFileName() { return originalFileName; }
    public Integer getVersion() { return version; }
    public boolean isActive() { return active; }
    public Long getFileSizeBytes() { return fileSizeBytes; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
}