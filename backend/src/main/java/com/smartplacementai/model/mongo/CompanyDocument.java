package com.smartplacementai.model.mongo;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "companies")
public class CompanyDocument {

    @Id
    private String id;

    private String companyName;

    private List<String> coreSkills;       // must-know skills
    private List<String> techStack;        // tools, frameworks
    private List<String> interviewFocus;   // DSA, system design, etc

    public CompanyDocument() {}

    // ---------- Getters ----------

    public String getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public List<String> getCoreSkills() {
        return coreSkills;
    }

    public List<String> getTechStack() {
        return techStack;
    }

    public List<String> getInterviewFocus() {
        return interviewFocus;
    }

    // ---------- Setters ----------

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCoreSkills(List<String> coreSkills) {
        this.coreSkills = coreSkills;
    }

    public void setTechStack(List<String> techStack) {
        this.techStack = techStack;
    }

    public void setInterviewFocus(List<String> interviewFocus) {
        this.interviewFocus = interviewFocus;
    }
}
