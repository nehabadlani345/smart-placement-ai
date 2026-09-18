package com.smartplacementai.dto;

import java.util.List;
import java.util.Map;

public class JdCompatibilityDto {
    private String jobId;
    private int atsScore;
    private List<String> matchedRequiredSkills;
    private List<String> missingRequiredSkills;
    private List<String> matchedPreferredSkills;
    private List<String> missingPreferredSkills;
    private Map<String, Integer> scoreBreakdown;
    private String aiExplanation;
    private List<String> aiRecommendedActions;

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public int getAtsScore() { return atsScore; }
    public void setAtsScore(int atsScore) { this.atsScore = atsScore; }
    public List<String> getMatchedRequiredSkills() { return matchedRequiredSkills; }
    public void setMatchedRequiredSkills(List<String> v) { this.matchedRequiredSkills = v; }
    public List<String> getMissingRequiredSkills() { return missingRequiredSkills; }
    public void setMissingRequiredSkills(List<String> v) { this.missingRequiredSkills = v; }
    public List<String> getMatchedPreferredSkills() { return matchedPreferredSkills; }
    public void setMatchedPreferredSkills(List<String> v) { this.matchedPreferredSkills = v; }
    public List<String> getMissingPreferredSkills() { return missingPreferredSkills; }
    public void setMissingPreferredSkills(List<String> v) { this.missingPreferredSkills = v; }
    public Map<String, Integer> getScoreBreakdown() { return scoreBreakdown; }
    public void setScoreBreakdown(Map<String, Integer> scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; }
    public String getAiExplanation() { return aiExplanation; }
    public void setAiExplanation(String aiExplanation) { this.aiExplanation = aiExplanation; }
    public List<String> getAiRecommendedActions() { return aiRecommendedActions; }
    public void setAiRecommendedActions(List<String> v) { this.aiRecommendedActions = v; }
}