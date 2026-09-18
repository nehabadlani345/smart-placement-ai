package com.smartplacementai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RoadmapGenerateRequest {
    @NotBlank
    private String targetRole;

    @Min(1)
    private Integer weeklyStudyHours = 10;

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public Integer getWeeklyStudyHours() { return weeklyStudyHours; }
    public void setWeeklyStudyHours(Integer weeklyStudyHours) { this.weeklyStudyHours = weeklyStudyHours; }
}