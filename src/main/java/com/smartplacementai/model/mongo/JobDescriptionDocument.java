package com.smartplacementai.model.mongo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "job_descriptions")
public class JobDescriptionDocument {

    @Id
    private String id;

    private String companyName;
    private String role;

    private List<String> requiredSkills;
    private List<String> preferredSkills;

    private Integer minExperience; // in years
    private Integer maxExperience;

    private LocalDateTime createdAt;

    public JobDescriptionDocument() {}

    // ---------- Getters ----------

    public String getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getRole() {
        return role;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public List<String> getPreferredSkills() {
        return preferredSkills;
    }

    public Integer getMinExperience() {
        return minExperience;
    }

    public Integer getMaxExperience() {
        return maxExperience;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ---------- Setters ----------

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public void setPreferredSkills(List<String> preferredSkills) {
        this.preferredSkills = preferredSkills;
    }

    public void setMinExperience(Integer minExperience) {
        this.minExperience = minExperience;
    }

    public void setMaxExperience(Integer maxExperience) {
        this.maxExperience = maxExperience;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
