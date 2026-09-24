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

    /*
     * User-controlled roadmap duration.
     *
     * Examples:
     * 4 WEEK
     * 10 DAY
     * 3 MONTH
     */
    private Integer durationValue;

    /*
     * DAY, WEEK, MONTH
     */
    private String durationUnit;

    /*
     * Number of study hours available for each period.
     *
     * Example:
     * 10 hours per week
     * 2 hours per day
     * 20 hours per month
     */
    private Integer studyHoursPerPeriod;

    private Integer version;

    private boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    private List<Phase> phases;

    // =========================================================
    // PHASE
    // =========================================================

    public static class Phase {

        private String title;

        /*
         * Generic period number.
         *
         * Day 1
         * Week 1
         * Month 1
         */
        private Integer periodNumber;

        /*
         * Human-readable label.
         *
         * Example:
         * Day 3
         * Week 2
         * Month 1
         */
        private String periodLabel;

        private List<Task> tasks;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getPeriodNumber() {
            return periodNumber;
        }

        public void setPeriodNumber(Integer periodNumber) {
            this.periodNumber = periodNumber;
        }

        public String getPeriodLabel() {
            return periodLabel;
        }

        public void setPeriodLabel(String periodLabel) {
            this.periodLabel = periodLabel;
        }

        public List<Task> getTasks() {
            return tasks;
        }

        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
        }
    }

    // =========================================================
    // TASK
    // =========================================================

    public static class Task {

        private String id = UUID.randomUUID().toString();

        private String title;

        private String description;

        private Double estimatedHours;

        /*
         * HIGH, MEDIUM, LOW
         */
        private String priority;

        private boolean completed = false;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getEstimatedHours() {
            return estimatedHours;
        }

        public void setEstimatedHours(Double estimatedHours) {
            this.estimatedHours = estimatedHours;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }

    // =========================================================
    // TOP-LEVEL GETTERS / SETTERS
    // =========================================================

    public String getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(Integer durationValue) {
        this.durationValue = durationValue;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }

    public Integer getStudyHoursPerPeriod() {
        return studyHoursPerPeriod;
    }

    public void setStudyHoursPerPeriod(Integer studyHoursPerPeriod) {
        this.studyHoursPerPeriod = studyHoursPerPeriod;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Phase> getPhases() {
        return phases;
    }

    public void setPhases(List<Phase> phases) {
        this.phases = phases;
    }
}