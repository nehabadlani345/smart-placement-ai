package com.smartplacementai.service.aggregation;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.smartplacementai.model.mongo.StructuredResumeDocument;

@Service
public class ExperienceConfidenceService {

    /**
     * Industry-grade experience confidence calculation.
     * Focuses on evidence, skill usage, and consistency.
     */
    public double calculateExperienceConfidence(StructuredResumeDocument resume) {

        double evidenceScore = calculateExperienceEvidence(resume);
        double skillUsageScore = calculateSkillUsageInExperience(resume);
        double continuityScore = calculateRoleContinuity(resume);
        double yearsScore = calculateYearsSignal(resume);

        // Weighted aggregation (industry aligned)
        double finalScore =
                (evidenceScore * 0.35) +
                (skillUsageScore * 0.30) +
                (continuityScore * 0.20) +
                (yearsScore * 0.15);

        return Math.round(finalScore * 10.0) / 10.0; // 1 decimal precision
    }

    // ---------------- SIGNAL 1 ----------------
    // Experience Evidence Density
    private double calculateExperienceEvidence(StructuredResumeDocument resume) {

        if (resume.getSections() == null) return 0;

        List<String> experienceLines =
                resume.getSections().getOrDefault("experience", List.of());

        int lines = experienceLines.size();

        if (lines >= 6) return 100;
        if (lines >= 4) return 80;
        if (lines >= 2) return 50;
        if (lines >= 1) return 30;

        return 0;
    }

    // ---------------- SIGNAL 2 ----------------
    // Skill usage inside experience section
    private double calculateSkillUsageInExperience(StructuredResumeDocument resume) {

        if (resume.getSections() == null) return 0;

        List<String> skills =
                resume.getSections().getOrDefault("skills", List.of());

        List<String> experienceLines =
                resume.getSections().getOrDefault("experience", List.of());

        if (skills.isEmpty() || experienceLines.isEmpty()) return 0;

        String experienceText =
                String.join(" ", experienceLines).toLowerCase();

        long matchedSkills =
                skills.stream()
                      .map(String::toLowerCase)
                      .filter(experienceText::contains)
                      .distinct()
                      .count();

        double usageRatio = (double) matchedSkills / skills.size();

        if (usageRatio >= 0.6) return 100;
        if (usageRatio >= 0.4) return 80;
        if (usageRatio >= 0.2) return 50;

        return 20;
    }

    // ---------------- SIGNAL 3 ----------------
    // Role / project continuity
    private double calculateRoleContinuity(StructuredResumeDocument resume) {

        if (resume.getSections() == null) return 0;

        List<String> experienceLines =
                resume.getSections().getOrDefault("experience", List.of());

        if (experienceLines.size() < 2) return 30;

        // Simple deterministic continuity check:
        // presence of repeated role/action keywords
        Set<String> continuityKeywords =
                Set.of("developed", "designed", "implemented", "built", "maintained");

        long continuityHits =
                experienceLines.stream()
                               .map(String::toLowerCase)
                               .filter(line ->
                                   continuityKeywords.stream().anyMatch(line::contains))
                               .count();

        if (continuityHits >= 3) return 100;
        if (continuityHits >= 2) return 70;
        if (continuityHits >= 1) return 50;

        return 30;
    }

    // ---------------- SIGNAL 4 ----------------
    // Years signal (soft, capped)
    private double calculateYearsSignal(StructuredResumeDocument resume) {

        int years = resume.getTotalExperienceYears();

        if (years <= 0) return 0;
        if (years == 1) return 40;
        if (years == 2) return 60;
        if (years >= 3) return 80;

        return 0;
    }
}
