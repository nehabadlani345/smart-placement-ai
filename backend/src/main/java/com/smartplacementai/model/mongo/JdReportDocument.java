package com.smartplacementai.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "jd_reports")
public class JdReportDocument {

    @Id
    private String id;
    private Long userId;
    private String role;
    private int atsScore;
    private List<String> missingRequiredSkills;
    private LocalDateTime createdAt = LocalDateTime.now();

    public JdReportDocument() {}

    public JdReportDocument(Long userId, String role, int atsScore, List<String> missingRequiredSkills) {
        this.userId = userId;
        this.role = role;
        this.atsScore = atsScore;
        this.missingRequiredSkills = missingRequiredSkills;
    }

    public String getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getAtsScore() { return atsScore; }
    public void setAtsScore(int atsScore) { this.atsScore = atsScore; }
    public List<String> getMissingRequiredSkills() { return missingRequiredSkills; }
    public void setMissingRequiredSkills(List<String> v) { this.missingRequiredSkills = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}