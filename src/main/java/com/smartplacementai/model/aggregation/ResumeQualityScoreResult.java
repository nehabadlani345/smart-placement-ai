package com.smartplacementai.model.aggregation;

public class ResumeQualityScoreResult {

    private String resumeId;
    private int totalScore;

    private int formatScore;
    private int sectionScore;
    private int keywordScore;
    private int skillClarityScore;
    private int experiencePresentationScore;

    // ----- getters & setters -----

    public String getResumeId() {
        return resumeId;
    }

    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getFormatScore() {
        return formatScore;
    }

    public void setFormatScore(int formatScore) {
        this.formatScore = formatScore;
    }

    public int getSectionScore() {
        return sectionScore;
    }

    public void setSectionScore(int sectionScore) {
        this.sectionScore = sectionScore;
    }

    public int getKeywordScore() {
        return keywordScore;
    }

    public void setKeywordScore(int keywordScore) {
        this.keywordScore = keywordScore;
    }

    public int getSkillClarityScore() {
        return skillClarityScore;
    }

    public void setSkillClarityScore(int skillClarityScore) {
        this.skillClarityScore = skillClarityScore;
    }

    public int getExperiencePresentationScore() {
        return experiencePresentationScore;
    }

    public void setExperiencePresentationScore(int experiencePresentationScore) {
        this.experiencePresentationScore = experiencePresentationScore;
    }
}
