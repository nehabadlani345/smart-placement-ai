package com.smartplacementai.controller;

import com.smartplacementai.dto.DashboardDto;
import com.smartplacementai.security.CurrentUser;
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
    private final CurrentUser currentUser;

    public AnalyticsController(AnalyticsService analyticsService, CurrentUser currentUser) {
        this.analyticsService = analyticsService;
        this.currentUser = currentUser;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> dashboard(Authentication authentication) {
        Long userId = currentUser.id(authentication);
        return ResponseEntity.ok(analyticsService.getDashboard(userId));
    }
}