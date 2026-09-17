package com.smartplacementai.exception;

public class ResumeNotFoundException extends RuntimeException {

    public ResumeNotFoundException(String resumeId) {
        super("Structured resume not found for id: " + resumeId);
    }
}
