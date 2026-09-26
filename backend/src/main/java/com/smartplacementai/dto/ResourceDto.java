package com.smartplacementai.dto;

public class ResourceDto {
    private Long id;
    private String title;
    private String description;
    private String url;
    private String resourceType;
    private String topic;
    private String difficulty;
    private boolean bookmarked;

    public ResourceDto(Long id, String title, String description, String url,
                        String resourceType, String topic, String difficulty, boolean bookmarked) {
        this.id = id; this.title = title; this.description = description; this.url = url;
        this.resourceType = resourceType; this.topic = topic; this.difficulty = difficulty; this.bookmarked = bookmarked;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
    public String getResourceType() { return resourceType; }
    public String getTopic() { return topic; }
    public String getDifficulty() { return difficulty; }
    public boolean isBookmarked() { return bookmarked; }
}