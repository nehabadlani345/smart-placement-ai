package com.smartplacementai.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "ats_reports")
public class AtsReportDocument {

    @Id
    private String id;
    private Long userId;
    private int totalScore;
    private LocalDateTime createdAt = LocalDateTime.now();

    public AtsReportDocument() {}

    public AtsReportDocument(Long userId, int totalScore) {
        this.userId = userId;
        this.totalScore = totalScore;
    }

    public String getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}