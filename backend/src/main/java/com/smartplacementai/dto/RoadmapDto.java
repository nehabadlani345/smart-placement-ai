package com.smartplacementai.dto;

import java.util.List;

public class RoadmapDto {

    private String id;

    private String targetRole;

    /*
     * User-controlled roadmap duration.
     */
    private Integer durationValue;

    /*
     * DAY, WEEK, MONTH
     */
    private String durationUnit;

    /*
     * Study hours available for each period.
     */
    private Integer studyHoursPerPeriod;

    private Integer version;

    private List<PhaseDto> phases;

    // =========================================================
    // PHASE DTO
    // =========================================================

    public static class PhaseDto {

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
         * Human-readable period label.
         *
         * Example:
         * Day 1
         * Week 2
         * Month 1
         */
        private String periodLabel;

        private List<TaskDto> tasks;

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

        public List<TaskDto> getTasks() {
            return tasks;
        }

        public void setTasks(List<TaskDto> tasks) {
            this.tasks = tasks;
        }
    }

    // =========================================================
    // TASK DTO
    // =========================================================

    public static class TaskDto {

        private String id;

        private String title;

        private String description;

        private Double estimatedHours;

        private String priority;

        private boolean completed;

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

    public void setId(String id) {
        this.id = id;
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

    public List<PhaseDto> getPhases() {
        return phases;
    }

    public void setPhases(List<PhaseDto> phases) {
        this.phases = phases;
    }
}