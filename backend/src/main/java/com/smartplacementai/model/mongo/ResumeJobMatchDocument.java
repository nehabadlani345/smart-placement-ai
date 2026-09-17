package com.smartplacementai.model.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resume_job_matches")
public class ResumeJobMatchDocument {

    @Id
    private String id;

    private String resumeId;
    private String jobId;

    private int atsScore;

    private List<String> matchedRequiredSkills;
    private List<String> missingRequiredSkills;

    private List<String> matchedPreferredSkills;
    private List<String> missingPreferredSkills;

    private Map<String, Integer> scoreBreakdown;

    private LocalDateTime createdAt;

    public ResumeJobMatchDocument() {}

    // ---------- Getters ----------

    public String getId() {
        return id;
    }

    public String getResumeId() {
        return resumeId;
    }

    public String getJobId() {
        return jobId;
    }

    public int getAtsScore() {
        return atsScore;
    }

    public List<String> getMatchedRequiredSkills() {
        return matchedRequiredSkills;
    }

    public List<String> getMissingRequiredSkills() {
        return missingRequiredSkills;
    }

    public List<String> getMatchedPreferredSkills() {
        return matchedPreferredSkills;
    }

    public List<String> getMissingPreferredSkills() {
        return missingPreferredSkills;
    }

    public Map<String, Integer> getScoreBreakdown() {
        return scoreBreakdown;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ---------- Setters ----------

    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setAtsScore(int atsScore) {
        this.atsScore = atsScore;
    }

    public void setMatchedRequiredSkills(List<String> matchedRequiredSkills) {
        this.matchedRequiredSkills = matchedRequiredSkills;
    }

    public void setMissingRequiredSkills(List<String> missingRequiredSkills) {
        this.missingRequiredSkills = missingRequiredSkills;
    }

    public void setMatchedPreferredSkills(List<String> matchedPreferredSkills) {
        this.matchedPreferredSkills = matchedPreferredSkills;
    }

    public void setMissingPreferredSkills(List<String> missingPreferredSkills) {
        this.missingPreferredSkills = missingPreferredSkills;
    }

    public void setScoreBreakdown(Map<String, Integer> scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
