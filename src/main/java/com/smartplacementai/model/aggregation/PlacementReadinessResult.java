package com.smartplacementai.model.aggregation;

import java.util.List;

/**
 * INTERNAL aggregation result.
 * This represents the computed placement readiness state
 * before explanation / UI formatting.
 */
public class PlacementReadinessResult {

    private String resumeId;

    // ---------- CORE SCORES ----------
    private double overallScore;
    private double resumeQualityScore;
    private double averageAtsScore;
    private double experienceConfidenceScore;

    // ---------- QUALITATIVE INFO ----------
    private String readinessLevel;

    private List<String> weakestSkills;
    private List<String> strongestSkills;

    private int totalJobsAnalyzed;

    // ---------- 🔥 NEW INTELLIGENCE LAYER ----------
    private List<String> insights;
    private List<ImprovementSuggestion> improvements;
    private String confidenceLevel;

    // ---------- GETTERS & SETTERS ----------

    public String getResumeId() {
        return resumeId;
    }

    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }

    public double getResumeQualityScore() {
        return resumeQualityScore;
    }

    public void setResumeQualityScore(double resumeQualityScore) {
        this.resumeQualityScore = resumeQualityScore;
    }

    public double getAverageAtsScore() {
        return averageAtsScore;
    }

    public void setAverageAtsScore(double averageAtsScore) {
        this.averageAtsScore = averageAtsScore;
    }

    public double getExperienceConfidenceScore() {
        return experienceConfidenceScore;
    }

    public void setExperienceConfidenceScore(double experienceConfidenceScore) {
        this.experienceConfidenceScore = experienceConfidenceScore;
    }

    public String getReadinessLevel() {
        return readinessLevel;
    }

    public void setReadinessLevel(String readinessLevel) {
        this.readinessLevel = readinessLevel;
    }

    public List<String> getWeakestSkills() {
        return weakestSkills;
    }

    public void setWeakestSkills(List<String> weakestSkills) {
        this.weakestSkills = weakestSkills;
    }

    public List<String> getStrongestSkills() {
        return strongestSkills;
    }

    public void setStrongestSkills(List<String> strongestSkills) {
        this.strongestSkills = strongestSkills;
    }

    public int getTotalJobsAnalyzed() {
        return totalJobsAnalyzed;
    }

    public void setTotalJobsAnalyzed(int totalJobsAnalyzed) {
        this.totalJobsAnalyzed = totalJobsAnalyzed;
    }

    // ---------- NEW GETTERS & SETTERS ----------

    public List<String> getInsights() {
        return insights;
    }

    public void setInsights(List<String> insights) {
        this.insights = insights;
    }

    public List<ImprovementSuggestion> getImprovements() {
        return improvements;
    }

    public void setImprovements(List<ImprovementSuggestion> improvements) {
        this.improvements = improvements;
    }

    public String getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(String confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }
}