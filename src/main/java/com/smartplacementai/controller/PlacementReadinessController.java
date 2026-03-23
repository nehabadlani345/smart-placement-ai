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

    // =========================
    // CONSTRUCTOR INJECTION
    // =========================
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

    // =========================
    // MAIN API ENDPOINT
    // =========================
    @GetMapping("/placement-readiness/{resumeId}")
    public PlacementReadinessReport getReadiness(@PathVariable String resumeId) {

        // =========================
        // 1️⃣ GET RESUME QUALITY
        // =========================
        ResumeQualityScoreResult qualityResult =
                resumeQualityScoreService.calculate(resumeId);

        double resumeQualityScore = qualityResult.getTotalScore();

        // =========================
        // 2️⃣ GET EXPERIENCE CONFIDENCE
        // =========================
       StructuredResumeDocument structuredResume =
        structuredResumeRepository.findByRawResumeId(resumeId)
        .orElseThrow(() -> new RuntimeException("Structured resume not found"));

        double experienceConfidenceScore =
                experienceConfidenceService.calculateExperienceConfidence(structuredResume);

        // =========================
        // 3️⃣ CORE AGGREGATION (SERVICE CALL)
        // =========================
        PlacementReadinessResult internalResult =
                placementReadinessService.calculateReadiness(
                        resumeId,
                        resumeQualityScore,
                        experienceConfidenceScore
                );

        // =========================
        // 4️⃣ BUILD FINAL API RESPONSE
        // =========================
        PlacementReadinessReport report = new PlacementReadinessReport();

        report.setResumeId(internalResult.getResumeId());
        report.setReadinessScore(internalResult.getOverallScore());
        report.setReadinessLevel(internalResult.getReadinessLevel());
        report.setWeakestSkills(internalResult.getWeakestSkills());
        report.setStrongestSkills(internalResult.getStrongestSkills());
        report.setTotalJobsAnalyzed(internalResult.getTotalJobsAnalyzed());

        // =========================
        // 5️⃣ SCORE BREAKDOWN
        // =========================
        PlacementReadinessReport.ScoreBreakdown breakdown =
                new PlacementReadinessReport.ScoreBreakdown();

        breakdown.setResumeQuality(internalResult.getResumeQualityScore());
        breakdown.setAtsMatch(internalResult.getAverageAtsScore());
        breakdown.setExperienceConfidence(
                internalResult.getExperienceConfidenceScore()
        );

        report.setBreakdown(breakdown);

        // =========================
        // ⚠️ IMPORTANT:
        // Insights, Improvements, Confidence
        // SHOULD COME FROM SERVICE (NOT CONTROLLER)
        // =========================

        report.setInsights(internalResult.getInsights());
        report.setImprovements(internalResult.getImprovements());
        report.setConfidenceLevel(internalResult.getConfidenceLevel());
        System.out.println("ResumeId: " + resumeId);
        return report;
    }
}