
package com.smartplacementai.model.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resumes")
public class ResumeDocument {

    @Id
    private String id; // MongoDB ObjectId (String)

    private Long userId; // Reference to PostgreSQL User

    private String originalFileName;

    private String rawText; // extracted resume text
    private String storageKey;
    private Long fileSizeBytes;
    private Integer version;
    private boolean active = true;
    private List<String> skills;

    private Map<String, Object> sections;
    // education, experience, projects (flexible – future use)

    private LocalDateTime uploadedAt;

    public ResumeDocument() {}

    public ResumeDocument(Long userId, String originalFileName) {
        this.userId = userId;
        this.originalFileName = originalFileName;
        this.uploadedAt = LocalDateTime.now();
    }

    // =====================
    // ✅ GETTERS (REQUIRED FOR STEP 5)
    // =====================

    public String getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRawText() {
        return rawText;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    // =====================
    // ✅ SETTERS
    // =====================

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    
    
    public String getStorageKey() { return storageKey; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }

    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
