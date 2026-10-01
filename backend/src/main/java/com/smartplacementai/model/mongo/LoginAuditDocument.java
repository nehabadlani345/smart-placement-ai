package com.smartplacementai.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "login_audit_log")
public class LoginAuditDocument {
    @Id private String id;
    private String email;
    private String event; // LOGIN_FAILED, PASSWORD_RESET_REQUESTED, PASSWORD_RESET_COMPLETED
    private String ipAddress;
    private LocalDateTime createdAt = LocalDateTime.now();

    public LoginAuditDocument() {}
    public LoginAuditDocument(String email, String event, String ipAddress) {
        this.email = email; this.event = event; this.ipAddress = ipAddress;
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getEvent() { return event; }
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}