package com.smartplacementai.model.aggregation;

import java.util.List;

/**
 * API-facing Placement Readiness Report.
 * This is what UI / frontend / external consumers see.
 */
public class PlacementReadinessReport {

    private String resumeId;

    private double readinessScore;
    private String readinessLevel;

    private ScoreBreakdown breakdown;

    private List<String> weakestSkills;
    private List<String> strongestSkills;

    private int totalJobsAnalyzed;

    // ---------- getters & setters ----------

    public String getResumeId() {
        return resumeId;
    }

    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }

    public double getReadinessScore() {
        return readinessScore;
    }

    public void setReadinessScore(double readinessScore) {
        this.readinessScore = readinessScore;
    }

    public String getReadinessLevel() {
        return readinessLevel;
    }

    public void setReadinessLevel(String readinessLevel) {
        this.readinessLevel = readinessLevel;
    }

    public ScoreBreakdown getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(ScoreBreakdown breakdown) {
        this.breakdown = breakdown;
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

    // ---------- nested DTO ----------

    public static class ScoreBreakdown {

        private double resumeQuality;
        private double atsMatch;
        private double experienceConfidence;

        public double getResumeQuality() {
            return resumeQuality;
        }

        public void setResumeQuality(double resumeQuality) {
            this.resumeQuality = resumeQuality;
        }

        public double getAtsMatch() {
            return atsMatch;
        }

        public void setAtsMatch(double atsMatch) {
            this.atsMatch = atsMatch;
        }

        public double getExperienceConfidence() {
            return experienceConfidence;
        }

        public void setExperienceConfidence(double experienceConfidence) {
            this.experienceConfidence = experienceConfidence;
        }
    }
}
