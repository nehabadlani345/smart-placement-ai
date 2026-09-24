package com.smartplacementai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RoadmapGenerateRequest {

    @NotBlank
    private String targetRole;

    @Min(1)
    private Integer durationValue = 4;

    @NotBlank
    private String durationUnit = "WEEK"; // DAY, WEEK, MONTH

    @Min(1)
    private Integer studyHoursPerPeriod = 10;

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
}