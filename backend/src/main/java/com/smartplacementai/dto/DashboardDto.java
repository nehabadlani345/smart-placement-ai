package com.smartplacementai.dto;

import java.util.List;

public class DashboardDto {
    private int readinessScore;
    private Integer atsScore;
    private Integer jdScore;
    private Integer roadmapCompletionPercent;
    private List<String> topGaps;
    private List<NextActionDto> nextActions;

    public static class NextActionDto {
        private String title;
        private String description;
        private String route;

        public NextActionDto(String title, String description, String route) {
            this.title = title;
            this.description = description;
            this.route = route;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getRoute() { return route; }
    }

    public int getReadinessScore() { return readinessScore; }
    public void setReadinessScore(int readinessScore) { this.readinessScore = readinessScore; }
    public Integer getAtsScore() { return atsScore; }
    public void setAtsScore(Integer atsScore) { this.atsScore = atsScore; }
    public Integer getJdScore() { return jdScore; }
    public void setJdScore(Integer jdScore) { this.jdScore = jdScore; }
    public Integer getRoadmapCompletionPercent() { return roadmapCompletionPercent; }
    public void setRoadmapCompletionPercent(Integer v) { this.roadmapCompletionPercent = v; }
    public List<String> getTopGaps() { return topGaps; }
    public void setTopGaps(List<String> topGaps) { this.topGaps = topGaps; }
    public List<NextActionDto> getNextActions() { return nextActions; }
    public void setNextActions(List<NextActionDto> nextActions) { this.nextActions = nextActions; }
}