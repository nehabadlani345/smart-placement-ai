package com.smartplacementai.dto;

import java.time.LocalDateTime;

public class SessionDto {
    private Long id;
    private String deviceInfo;
    private String ipAddress;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public SessionDto(Long id, String deviceInfo, String ipAddress, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id; this.deviceInfo = deviceInfo; this.ipAddress = ipAddress;
        this.createdAt = createdAt; this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public String getDeviceInfo() { return deviceInfo; }
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
}