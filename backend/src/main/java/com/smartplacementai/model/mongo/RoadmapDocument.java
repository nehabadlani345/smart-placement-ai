package com.smartplacementai.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "roadmaps")
public class RoadmapDocument {

    @Id
    private String id;

    private Long userId;
    private String targetRole;
    private Integer weeklyStudyHours;
    private Integer version;
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    private List<Phase> phases;

    public static class Phase {
        private String title;
        private Integer weekNumber;
        private List<Task> tasks;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Integer getWeekNumber() { return weekNumber; }
        public void setWeekNumber(Integer weekNumber) { this.weekNumber = weekNumber; }
        public List<Task> getTasks() { return tasks; }
        public void setTasks(List<Task> tasks) { this.tasks = tasks; }
    }

    public static class Task {
        private String id = UUID.randomUUID().toString();
        private String title;
        private String description;
        private Double estimatedHours;
        private String priority; // HIGH, MEDIUM, LOW
        private boolean completed = false;

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
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public Integer getWeeklyStudyHours() { return weeklyStudyHours; }
    public void setWeeklyStudyHours(Integer weeklyStudyHours) { this.weeklyStudyHours = weeklyStudyHours; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<Phase> getPhases() { return phases; }
    public void setPhases(List<Phase> phases) { this.phases = phases; }
}