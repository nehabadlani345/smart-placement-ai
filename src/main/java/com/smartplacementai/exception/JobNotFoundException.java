package com.smartplacementai.exception;

public class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(String jobId) {
        super("Job description not found for id: " + jobId);
    }
}
