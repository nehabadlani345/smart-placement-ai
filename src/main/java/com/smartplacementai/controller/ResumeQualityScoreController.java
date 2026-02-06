package com.smartplacementai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartplacementai.model.aggregation.ResumeQualityScoreResult;
import com.smartplacementai.service.aggregation.ResumeQualityScoreService;

@RestController
@RequestMapping("/resume-quality")
public class ResumeQualityScoreController {

    private final ResumeQualityScoreService service;

    public ResumeQualityScoreController(ResumeQualityScoreService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ResumeQualityScoreResult> getScore(
            @RequestParam String resumeId
    ) {
        return ResponseEntity.ok(service.calculate(resumeId));
    }
}
