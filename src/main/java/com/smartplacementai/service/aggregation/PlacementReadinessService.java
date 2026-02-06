package com.smartplacementai.service.aggregation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.smartplacementai.model.aggregation.PlacementReadinessResult;
import com.smartplacementai.model.mongo.ResumeJobMatchDocument;
import com.smartplacementai.repository.mongo.ResumeJobMatchRepository;

/**
 * Aggregates all placement evidence into a single readiness result.
 * This service does NOT calculate ATS or Resume Quality.
 */
@Service
public class PlacementReadinessService {

    private static final double RESUME_QUALITY_WEIGHT = 0.30;
    private static final double ATS_WEIGHT = 0.50;
    private static final double EXPERIENCE_WEIGHT = 0.20;

    private final ResumeJobMatchRepository matchRepository;

    public PlacementReadinessService(ResumeJobMatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    /**
     * Core aggregation method.
     */
    public PlacementReadinessResult calculateReadiness(
            String resumeId,
            double resumeQualityScore,
            double experienceConfidenceScore
    ) {

        List<ResumeJobMatchDocument> matches =
                matchRepository.findByResumeId(resumeId);

        double averageAtsScore = calculateAverageAtsScore(matches);

        double overallScore =
                (resumeQualityScore * RESUME_QUALITY_WEIGHT)
                        + (averageAtsScore * ATS_WEIGHT)
                        + (experienceConfidenceScore * EXPERIENCE_WEIGHT);

        PlacementReadinessResult result = new PlacementReadinessResult();
        result.setResumeId(resumeId);

        result.setResumeQualityScore(round(resumeQualityScore));
        result.setAverageAtsScore(round(averageAtsScore));
        result.setExperienceConfidenceScore(round(experienceConfidenceScore));
        result.setOverallScore(round(overallScore));

        result.setReadinessLevel(classifyReadiness(overallScore));
        result.setTotalJobsAnalyzed(matches.size());

        result.setWeakestSkills(extractWeakestSkills(matches));
        result.setStrongestSkills(extractStrongestSkills(matches));

        return result;
    }

    // ---------- INTERNAL HELPERS ----------

    private double calculateAverageAtsScore(List<ResumeJobMatchDocument> matches) {
        if (matches.isEmpty()) {
            return 0.0;
        }

        return matches.stream()
                .mapToInt(ResumeJobMatchDocument::getAtsScore)
                .average()
                .orElse(0.0);
    }

    private List<String> extractWeakestSkills(List<ResumeJobMatchDocument> matches) {
        Map<String, Integer> count = new HashMap<>();

        for (ResumeJobMatchDocument match : matches) {
            for (String skill : match.getMissingRequiredSkills()) {
                count.put(skill, count.getOrDefault(skill, 0) + 1);
            }
        }

        return count.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<String> extractStrongestSkills(List<ResumeJobMatchDocument> matches) {
        Map<String, Integer> count = new HashMap<>();

        for (ResumeJobMatchDocument match : matches) {
            for (String skill : match.getMatchedRequiredSkills()) {
                count.put(skill, count.getOrDefault(skill, 0) + 1);
            }
        }

        return count.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    private String classifyReadiness(double score) {
        if (score >= 80) return "HIGH";
        if (score >= 60) return "MEDIUM";
        if (score >= 40) return "LOW";
        return "NOT_READY";
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
