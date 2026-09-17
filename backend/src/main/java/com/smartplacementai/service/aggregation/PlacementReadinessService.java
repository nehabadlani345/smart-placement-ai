package com.smartplacementai.service.aggregation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.smartplacementai.model.aggregation.ImprovementSuggestion;
import com.smartplacementai.model.aggregation.PlacementReadinessResult;
import com.smartplacementai.model.mongo.ResumeJobMatchDocument;
import com.smartplacementai.repository.mongo.ResumeJobMatchRepository;

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
     * 🔥 CORE METHOD (FINAL VERSION)
     */
    public PlacementReadinessResult calculateReadiness(
            String resumeId,
            double resumeQualityScore,
            double experienceConfidenceScore
    ) {

        List<ResumeJobMatchDocument> matches =
                matchRepository.findByResumeId(resumeId);

        // ✅ HANDLE EMPTY MATCHES (CRITICAL FIX)
        if (matches == null || matches.isEmpty()) {

            PlacementReadinessResult result = new PlacementReadinessResult();

            result.setResumeId(resumeId);
            result.setResumeQualityScore(round(resumeQualityScore));
            result.setAverageAtsScore(0);
            result.setExperienceConfidenceScore(round(experienceConfidenceScore));

            double overall =
                    (resumeQualityScore * RESUME_QUALITY_WEIGHT) +
                    (experienceConfidenceScore * EXPERIENCE_WEIGHT);

            result.setOverallScore(round(overall));
            result.setReadinessLevel(classifyReadiness(overall));
            result.setTotalJobsAnalyzed(0);

            result.setWeakestSkills(new ArrayList<>());
            result.setStrongestSkills(new ArrayList<>());

            // 🔥 ADD NEW FIELDS
            result.setInsights(generateInsights(result));
            result.setImprovements(generateImprovements(result));
            result.setConfidenceLevel(calculateConfidence(0));

            return result;
        }

        // ✅ NORMAL FLOW
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

        // 🔥 ADD NEW FIELDS
        result.setInsights(generateInsights(result));
        result.setImprovements(generateImprovements(result));
        result.setConfidenceLevel(calculateConfidence(matches.size()));

        return result;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private double calculateAverageAtsScore(List<ResumeJobMatchDocument> matches) {
        if (matches.isEmpty()) return 0.0;

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

    // =========================================================
    // 🔥 INSIGHTS
    // =========================================================

    private List<String> generateInsights(PlacementReadinessResult result) {

        List<String> insights = new ArrayList<>();

        if (result.getResumeQualityScore() < 60) {
            insights.add("Your resume quality is below average and may fail ATS filters");
        }

        if (result.getAverageAtsScore() < 65) {
            insights.add("Your profile is not well aligned with job requirements");
        }

        if (result.getExperienceConfidenceScore() < 50) {
            insights.add("Your experience does not strongly support your target roles");
        }

        if (insights.isEmpty()) {
            insights.add("Your profile is well balanced and competitive");
        }

        return insights;
    }

    // =========================================================
    // 🔥 IMPROVEMENTS
    // =========================================================

    private List<ImprovementSuggestion> generateImprovements(
            PlacementReadinessResult result) {

        List<ImprovementSuggestion> list = new ArrayList<>();

        if (result.getResumeQualityScore() < 70) {
            ImprovementSuggestion s = new ImprovementSuggestion();
            s.setAction("Improve resume formatting and keyword optimization");
            s.setImpact("+5 to +10 score");
            list.add(s);
        }

        if (result.getAverageAtsScore() < 70) {
            ImprovementSuggestion s = new ImprovementSuggestion();
            s.setAction("Align skills with job descriptions");
            s.setImpact("+8 score");
            list.add(s);
        }

        if (result.getExperienceConfidenceScore() < 60) {
            ImprovementSuggestion s = new ImprovementSuggestion();
            s.setAction("Add more relevant project or internship experience");
            s.setImpact("+6 score");
            list.add(s);
        }

        return list;
    }

    // =========================================================
    // 🔥 CONFIDENCE LEVEL
    // =========================================================

    private String calculateConfidence(int jobsAnalyzed) {

        if (jobsAnalyzed >= 5) return "HIGH";
        if (jobsAnalyzed >= 3) return "MEDIUM";
        return "LOW";
    }
}