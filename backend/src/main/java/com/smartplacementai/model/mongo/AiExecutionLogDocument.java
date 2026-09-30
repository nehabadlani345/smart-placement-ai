package com.smartplacementai.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "ai_prompt_execution_logs")
public class AiExecutionLogDocument {

    @Id
    private String id;

    private String feature; // "ats", "jd", "roadmap"
    private Long userId;
    private long latencyMs;
    private boolean success;
    private String errorMessage;

    // TTL index: MongoDB auto-deletes documents 90 days after createdAt.
    // auto-index-creation must stay true (see application.yml) for this to be created.
    @Indexed(expireAfterSeconds = 60 * 60 * 24 * 90)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AiExecutionLogDocument() {}

    public AiExecutionLogDocument(String feature, Long userId, long latencyMs, boolean success, String errorMessage) {
        this.feature = feature;
        this.userId = userId;
        this.latencyMs = latencyMs;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    // getters only needed for now — this is write-only until an admin viewer exists
    public String getId() { return id; }
    public String getFeature() { return feature; }
    public Long getUserId() { return userId; }
    public long getLatencyMs() { return latencyMs; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
