package com.smartplacementai.controller;

import com.smartplacementai.dto.DashboardDto;
import com.smartplacementai.security.SecurityUser;
import com.smartplacementai.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> dashboard(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUserId();
        return ResponseEntity.ok(analyticsService.getDashboard(userId));
    }
}