package com.smartplacementai.controller;

import org.springframework.web.bind.annotation.*;

import com.smartplacementai.model.aggregation.PlacementReadinessReport;
import com.smartplacementai.model.aggregation.PlacementReadinessResult;
import com.smartplacementai.model.aggregation.ResumeQualityScoreResult;
import com.smartplacementai.model.mongo.StructuredResumeDocument;
import com.smartplacementai.repository.mongo.StructuredResumeRepository;
import com.smartplacementai.service.aggregation.ExperienceConfidenceService;
import com.smartplacementai.service.aggregation.PlacementReadinessService;
import com.smartplacementai.service.aggregation.ResumeQualityScoreService;

@RestController
@RequestMapping("/placement-readiness")
public class PlacementReadinessController {

    private final PlacementReadinessService placementReadinessService;
    private final ResumeQualityScoreService resumeQualityService;
    private final ExperienceConfidenceService experienceConfidenceService;
    private final StructuredResumeRepository structuredRepo;

    public PlacementReadinessController(
            PlacementReadinessService placementReadinessService,
            ResumeQualityScoreService resumeQualityService,
            ExperienceConfidenceService experienceConfidenceService,
            StructuredResumeRepository structuredRepo
    ) {
        this.placementReadinessService = placementReadinessService;
        this.resumeQualityService = resumeQualityService;
        this.experienceConfidenceService = experienceConfidenceService;
        this.structuredRepo = structuredRepo;
    }

    @GetMapping("/{resumeId}")
    public PlacementReadinessReport getReadiness(@PathVariable String resumeId) {

        // =========================
        // 1. RESUME QUALITY
        // =========================
        ResumeQualityScoreResult quality =
                resumeQualityService.calculateScore(resumeId);

        double resumeQualityScore = quality.getScore();

        // =========================
        // 2. EXPERIENCE CONFIDENCE
        // =========================
        StructuredResumeDocument structuredResume =
                structuredRepo.findByRawResumeId(resumeId)
                        .orElseThrow(() -> new RuntimeException("Structured resume not found"));

        double experienceConfidenceScore =
                experienceConfidenceService.calculateConfidence(structuredResume);

        // =========================
        // 3. CORE AGGREGATION
        // =========================
        PlacementReadinessResult internalResult =
                placementReadinessService.calculateReadiness(
                        resumeId,
                        resumeQualityScore,
                        experienceConfidenceScore
                );

        // =========================
        // 4. BUILD REPORT
        // =========================
        PlacementReadinessReport report = new PlacementReadinessReport();

        report.setResumeId(resumeId);
        report.setReadinessScore(internalResult.getOverallScore());
        report.setReadinessLevel(internalResult.getReadinessLevel());
        report.setTotalJobsAnalyzed(internalResult.getTotalJobsAnalyzed());
        report.setWeakestSkills(internalResult.getWeakestSkills());
        report.setStrongestSkills(internalResult.getStrongestSkills());

        // Breakdown
        PlacementReadinessReport.ScoreBreakdown breakdown =
                new PlacementReadinessReport.ScoreBreakdown();

        breakdown.setResumeQuality(internalResult.getResumeQualityScore());
        breakdown.setAtsMatch(internalResult.getAverageAtsScore());
        breakdown.setExperienceConfidence(internalResult.getExperienceConfidenceScore());

        report.setBreakdown(breakdown);

        // Add extra fields
        report.setInsights(internalResult.getInsights());
        report.setImprovements(internalResult.getImprovements());
        report.setConfidenceLevel(internalResult.getConfidenceLevel());

        return report;
    }
}