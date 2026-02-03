package com.smartplacementai.model.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resumes_structured")
public class StructuredResumeDocument {

    @Id
    private String id;

    private String rawResumeId;
    private Long userId;

    private Map<String, List<String>> sections;

    private LocalDateTime structuredAt;

    public StructuredResumeDocument() {}

    // =====================
    // ✅ GETTERS
    // =====================

    public String getId() {
        return id;
    }

    public String getRawResumeId() {
        return rawResumeId;
    }

    public Long getUserId() {
        return userId;
    }

    public Map<String, List<String>> getSections() {
        return sections;
    }

    public LocalDateTime getStructuredAt() {
        return structuredAt;
    }

    // =====================
    // ✅ SETTERS (CRITICAL)
    // =====================

    public void setRawResumeId(String rawResumeId) {
        this.rawResumeId = rawResumeId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setSections(Map<String, List<String>> sections) {
        this.sections = sections;
    }

    public void setStructuredAt(LocalDateTime structuredAt) {
        this.structuredAt = structuredAt;
    }


    private Map<String, Double> confidence;

    public Map<String, Double> getConfidence() {
    return confidence;
}

public void setConfidence(Map<String, Double> confidence) {
    this.confidence = confidence;
}


}
