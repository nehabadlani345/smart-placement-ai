package com.smartplacementai.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "resumes")
public class ResumeDocument {

    @Id
    private String id; // MongoDB ObjectId (String)

    private Long userId; // Reference to PostgreSQL User

    private String originalFileName;

    private String rawText; // extracted text (future)

    private List<String> skills;

    private Map<String, Object> sections; 
    // education, experience, projects (flexible)

    private LocalDateTime uploadedAt;

    public ResumeDocument() {}

    public ResumeDocument(Long userId, String originalFileName) {
        this.userId = userId;
        this.originalFileName = originalFileName;
        this.uploadedAt = LocalDateTime.now();
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }
}
