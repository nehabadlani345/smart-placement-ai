package com.smartplacementai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String rawToken) {
        String link = frontendUrl + "/verify-email?token=" + rawToken;
        send(to, "Verify your email — AI Placement OS",
                "Welcome! Please verify your email by opening this link:\n\n" + link +
                        "\n\nThis link expires in 24 hours.");
    }

    public void sendPasswordResetEmail(String to, String rawToken) {
        String link = frontendUrl + "/reset-password?token=" + rawToken;
        send(to, "Reset your password — AI Placement OS",
                "We received a request to reset your password. Open this link to set a new one:\n\n" + link +
                        "\n\nThis link expires in 1 hour. If you didn't request this, you can ignore this email.");
    }

    private void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}