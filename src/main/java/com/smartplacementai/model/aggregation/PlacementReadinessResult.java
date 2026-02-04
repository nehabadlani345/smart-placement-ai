package com.smartplacementai.model.aggregation;

import java.util.List;

public class PlacementReadinessResult {

    private String resumeId;
    private double readinessScore;

    private List<String> weakestSkills;
    private List<String> strongestSkills;

    private int totalJobsAnalyzed;

    // ---------- Getters & Setters ----------

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
}
