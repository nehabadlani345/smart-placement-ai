package com.smartplacementai.service;

import java.util.List;

public enum ResumeSection {

    SKILLS(List.of("skills", "technical skills")),
    EDUCATION(List.of("education", "academic background")),
    EXPERIENCE(List.of("experience", "work experience")),
    PROJECTS(List.of("projects", "academic projects"));

    private final List<String> headers;

    ResumeSection(List<String> headers) {
        this.headers = headers;
    }

    public List<String> getHeaders() {
        return headers;
    }
}
