package com.smartplacementai.model.sql;

import jakarta.persistence.*;

@Entity
@Table(name = "resources")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String description;

    @Column(nullable = false)
    private String url;

    @Column(name = "resource_type")
    private String resourceType; // VIDEO, DOC, ARTICLE, REPO, CHEATSHEET

    private String topic; // "Java", "DSA", "System Design", "Spring Boot", "Angular"
    private String difficulty; // BEGINNER, INTERMEDIATE, ADVANCED

    public Resource() {}

    public Resource(String title, String description, String url, String resourceType, String topic, String difficulty) {
        this.title = title; this.description = description; this.url = url;
        this.resourceType = resourceType; this.topic = topic; this.difficulty = difficulty;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}