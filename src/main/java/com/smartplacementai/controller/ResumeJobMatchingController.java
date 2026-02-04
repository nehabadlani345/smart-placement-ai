package com.smartplacementai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartplacementai.model.matching.MatchingResult;
import com.smartplacementai.service.matching.ResumeJobMatchingService;

@RestController
@RequestMapping("/match")
public class ResumeJobMatchingController {

    private final ResumeJobMatchingService matchingService;

    public ResumeJobMatchingController(ResumeJobMatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping
    public ResponseEntity<MatchingResult> matchResumeWithJob(
            @RequestParam String resumeId,
            @RequestParam String jobId
    ) {
        MatchingResult result = matchingService.match(resumeId, jobId);
        return ResponseEntity.ok(result);
    }
}
