package com.smartplacementai.dto;

import java.util.List;

public class AtsReportDto {
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
    public void setSkillClarityScore(int skillClarityScore) { this.skillClarityScore = skillClarityScore; }
    public int getExperiencePresentationScore() { return experiencePresentationScore; }
    public void setExperiencePresentationScore(int v) { this.experiencePresentationScore = v; }
    public List<String> getAiStrengths() { return aiStrengths; }
    public void setAiStrengths(List<String> aiStrengths) { this.aiStrengths = aiStrengths; }
    public List<String> getAiImprovements() { return aiImprovements; }
    public void setAiImprovements(List<String> aiImprovements) { this.aiImprovements = aiImprovements; }
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
}