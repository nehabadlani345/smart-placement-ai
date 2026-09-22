package com.smartplacementai.controller;

import com.smartplacementai.dto.AtsReportDto;
import com.smartplacementai.security.SecurityUser;
import com.smartplacementai.service.AtsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ats")
public class AtsController {

    private final AtsService atsService;

    public AtsController(AtsService atsService) {
        this.atsService = atsService;
    }

    @GetMapping("/analyze")
    public ResponseEntity<AtsReportDto> analyze(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUserId();
        return ResponseEntity.ok(atsService.analyzeActiveResume(userId));
    }
    
    @GetMapping("/latest")
    public ResponseEntity<AtsReportDto> latest(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUserId();
        return ResponseEntity.ok(atsService.getLatestReport(userId));
    }
    
}