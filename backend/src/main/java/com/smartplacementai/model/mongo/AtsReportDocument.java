package com.smartplacementai.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "ats_reports")
public class AtsReportDocument {

    @Id
    private String id;
    private Long userId;
    private String resumeId;
    private int totalScore;
    private int formatScore;
    private int sectionScore;
    private int keywordScore;
    private int skillClarityScore;
    private int experiencePresentationScore;
    private List<String> aiStrengths;
    private List<String> aiImprovements;
    private String aiSummary;
    private LocalDateTime createdAt = LocalDateTime.now();

    public AtsReportDocument() {}

    public String getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getResumeId() { return resumeId; }
    public void setResumeId(String resumeId) { this.resumeId = resumeId; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public int getFormatScore() { return formatScore; }
    public void setFormatScore(int formatScore) { this.formatScore = formatScore; }
    public int getSectionScore() { return sectionScore; }
    public void setSectionScore(int sectionScore) { this.sectionScore = sectionScore; }
    public int getKeywordScore() { return keywordScore; }
    public void setKeywordScore(int keywordScore) { this.keywordScore = keywordScore; }
    public int getSkillClarityScore() { return skillClarityScore; }
    public void setSkillClarityScore(int v) { this.skillClarityScore = v; }
    public int getExperiencePresentationScore() { return experiencePresentationScore; }
    public void setExperiencePresentationScore(int v) { this.experiencePresentationScore = v; }
    public List<String> getAiStrengths() { return aiStrengths; }
    public void setAiStrengths(List<String> aiStrengths) { this.aiStrengths = aiStrengths; }
    public List<String> getAiImprovements() { return aiImprovements; }
    public void setAiImprovements(List<String> aiImprovements) { this.aiImprovements = aiImprovements; }
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}