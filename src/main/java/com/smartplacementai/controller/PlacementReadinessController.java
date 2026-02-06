package com.smartplacementai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartplacementai.model.aggregation.PlacementReadinessResult;
import com.smartplacementai.service.aggregation.PlacementReadinessService;
import com.smartplacementai.service.aggregation.ResumeQualityScoreService;

@RestController
public class PlacementReadinessController {

    private final PlacementReadinessService placementReadinessService;
    private final ResumeQualityScoreService resumeQualityScoreService;

    public PlacementReadinessController(
            PlacementReadinessService placementReadinessService,
            ResumeQualityScoreService resumeQualityScoreService
    ) {
        this.placementReadinessService = placementReadinessService;
        this.resumeQualityScoreService = resumeQualityScoreService;
    }

    // @GetMapping("/placement-readiness/{resumeId}")
    // public PlacementReadinessResult getReadiness(@PathVariable String resumeId) {

    //     // 1️⃣ Fetch Resume Quality Score (already implemented earlier)
    //     ResumeQualityScoreResult qualityResult =
    //             resumeQualityScoreService.calculate(resumeId);

    //     double resumeQualityScore = qualityResult.getTotalScore();

    //     // 2️⃣ TEMP experience confidence (deterministic placeholder)
    //     // NOTE: This will be redesigned properly in STEP 4
    //     double experienceConfidenceScore = 50.0;

    //     // 3️⃣ Aggregate placement readiness
    //     return placementReadinessService.calculateReadiness(
    //             resumeId,
    //             resumeQualityScore,
    //             experienceConfidenceScore
    //     );
    // }

    @GetMapping("/placement-readiness/{resumeId}")
public PlacementReadinessResult getReadiness(@PathVariable String resumeId) {

    System.out.println("STEP 1: Controller reached");

    var qualityResult =
            resumeQualityScoreService.calculate(resumeId);

    System.out.println("STEP 2: Resume quality calculated");

    double resumeQualityScore = qualityResult.getTotalScore();
    double experienceConfidenceScore = 50.0;

    PlacementReadinessResult result =
            placementReadinessService.calculateReadiness(
                    resumeId,
                    resumeQualityScore,
                    experienceConfidenceScore
            );

    System.out.println("STEP 3: Placement readiness calculated");

    return result;
}

}
