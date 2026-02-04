package com.smartplacementai.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.smartplacementai.exception.InvalidJobException;
import com.smartplacementai.exception.InvalidResumeException;
import com.smartplacementai.exception.JobNotFoundException;
import com.smartplacementai.exception.ResumeNotFoundException;



@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResumeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResumeNotFound(
            ResumeNotFoundException ex) {

        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleJobNotFound(
            JobNotFoundException ex) {

        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidResumeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidResume(
            InvalidResumeException ex) {

        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(InvalidJobException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJob(
        InvalidJobException ex) {

    return buildResponse(
            HttpStatus.UNPROCESSABLE_ENTITY,
            ex.getMessage()
    );
    }


    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String message) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}
