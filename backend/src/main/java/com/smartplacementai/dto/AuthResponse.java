package com.smartplacementai.dto;

public class AuthResponse {

    private final String token;
    private final Long userId;
    private final String name;
    private final String email;
    private final String role;
    private final boolean emailVerified;
    
    public AuthResponse(String token, Long userId, String name, String email, String role) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
		this.emailVerified = true; 
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isEmailVerified() { return emailVerified; }
}