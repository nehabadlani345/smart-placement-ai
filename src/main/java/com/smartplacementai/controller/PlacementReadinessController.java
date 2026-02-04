package com.smartplacementai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartplacementai.model.aggregation.PlacementReadinessResult;
import com.smartplacementai.service.aggregation.PlacementReadinessService;

@RestController
@RequestMapping("/readiness")
public class PlacementReadinessController {

    private final PlacementReadinessService readinessService;

    public PlacementReadinessController(PlacementReadinessService readinessService) {
        this.readinessService = readinessService;
    }

    @GetMapping
    public ResponseEntity<PlacementReadinessResult> getReadiness(
            @RequestParam String resumeId
    ) {
        PlacementReadinessResult result =
                readinessService.calculateReadiness(resumeId);

        return ResponseEntity.ok(result);
    }
}
