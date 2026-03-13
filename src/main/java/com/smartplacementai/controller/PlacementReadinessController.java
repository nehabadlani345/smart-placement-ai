package com.smartplacementai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartplacementai.model.aggregation.PlacementReadinessReport;
import com.smartplacementai.model.aggregation.PlacementReadinessResult;
import com.smartplacementai.model.aggregation.ResumeQualityScoreResult;
import com.smartplacementai.model.mongo.StructuredResumeDocument;
import com.smartplacementai.repository.mongo.StructuredResumeRepository;
import com.smartplacementai.service.aggregation.ExperienceConfidenceService;
import com.smartplacementai.service.aggregation.PlacementReadinessService;
import com.smartplacementai.service.aggregation.ResumeQualityScoreService;

@RestController
public class PlacementReadinessController {

    private final PlacementReadinessService placementReadinessService;
    private final ResumeQualityScoreService resumeQualityScoreService;
    private final StructuredResumeRepository structuredResumeRepository;
    private final ExperienceConfidenceService experienceConfidenceService;

    

   public PlacementReadinessController(
        PlacementReadinessService placementReadinessService,
        ResumeQualityScoreService resumeQualityScoreService,
        StructuredResumeRepository structuredResumeRepository,
        ExperienceConfidenceService experienceConfidenceService
) {
    this.placementReadinessService = placementReadinessService;
    this.resumeQualityScoreService = resumeQualityScoreService;
    this.structuredResumeRepository = structuredResumeRepository;
    this.experienceConfidenceService = experienceConfidenceService;
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

//     @GetMapping("/placement-readiness/{resumeId}")
// public PlacementReadinessResult getReadiness(@PathVariable String resumeId) {

//     System.out.println("STEP 1: Controller reached");

//     var qualityResult =
//             resumeQualityScoreService.calculate(resumeId);

//     System.out.println("STEP 2: Resume quality calculated");

//     double resumeQualityScore = qualityResult.getTotalScore();
//     double experienceConfidenceScore = 50.0;

//     PlacementReadinessResult result =
//             placementReadinessService.calculateReadiness(
//                     resumeId,
//                     resumeQualityScore,
//                     experienceConfidenceScore
//             );

//     System.out.println("STEP 3: Placement readiness calculated");

//     return result;

@GetMapping("/placement-readiness/{resumeId}")
public PlacementReadinessReport getReadiness(@PathVariable String resumeId) {

    // 1️⃣ Resume Quality
    ResumeQualityScoreResult qualityResult =
            resumeQualityScoreService.calculate(resumeId);
            
    double resumeQualityScore = qualityResult.getTotalScore();

    // 2️⃣ TEMP experience confidence
    
   StructuredResumeDocument structuredResume =
        structuredResumeRepository.findById(resumeId)
            .orElseThrow(() -> new RuntimeException("Structured resume not found"));

double experienceConfidenceScore =
        experienceConfidenceService.calculateExperienceConfidence(structuredResume);


    // 3️⃣ Internal aggregation
    PlacementReadinessResult internalResult =
            placementReadinessService.calculateReadiness(
                    resumeId,
                    resumeQualityScore,
                    experienceConfidenceScore
            );

    // 4️⃣ Build API report
    PlacementReadinessReport report = new PlacementReadinessReport();
    report.setResumeId(internalResult.getResumeId());
    report.setReadinessScore(internalResult.getOverallScore());
    report.setReadinessLevel(internalResult.getReadinessLevel());
    report.setWeakestSkills(internalResult.getWeakestSkills());
    report.setStrongestSkills(internalResult.getStrongestSkills());
    report.setTotalJobsAnalyzed(internalResult.getTotalJobsAnalyzed());

    PlacementReadinessReport.ScoreBreakdown breakdown =
            new PlacementReadinessReport.ScoreBreakdown();
    breakdown.setResumeQuality(internalResult.getResumeQualityScore());
    breakdown.setAtsMatch(internalResult.getAverageAtsScore());
    breakdown.setExperienceConfidence(
            internalResult.getExperienceConfidenceScore()
    );

    report.setBreakdown(breakdown);

    return report;
}

}


