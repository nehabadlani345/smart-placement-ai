package com.smartplacementai.dto;

import java.util.List;

public class RoadmapDto {
    private String id;
    private String targetRole;
    private Integer weeklyStudyHours;
    private Integer version;
    private List<PhaseDto> phases;

    public static class PhaseDto {
        private String title;
        private Integer weekNumber;
        private List<TaskDto> tasks;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Integer getWeekNumber() { return weekNumber; }
        public void setWeekNumber(Integer weekNumber) { this.weekNumber = weekNumber; }
        public List<TaskDto> getTasks() { return tasks; }
        public void setTasks(List<TaskDto> tasks) { this.tasks = tasks; }
    }

    public static class TaskDto {
        private String id;
        private String title;
        private String description;
        private Double estimatedHours;
        private String priority;
        private boolean completed;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Double getEstimatedHours() { return estimatedHours; }
        public void setEstimatedHours(Double estimatedHours) { this.estimatedHours = estimatedHours; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public Integer getWeeklyStudyHours() { return weeklyStudyHours; }
    public void setWeeklyStudyHours(Integer weeklyStudyHours) { this.weeklyStudyHours = weeklyStudyHours; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public List<PhaseDto> getPhases() { return phases; }
    public void setPhases(List<PhaseDto> phases) { this.phases = phases; }
}