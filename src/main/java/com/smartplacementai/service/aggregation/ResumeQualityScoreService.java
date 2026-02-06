package com.smartplacementai.service.aggregation;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.smartplacementai.model.aggregation.ResumeQualityScoreResult;
import com.smartplacementai.model.mongo.ResumeDocument;
import com.smartplacementai.model.mongo.StructuredResumeDocument;
import com.smartplacementai.repository.mongo.ResumeRepository;
import com.smartplacementai.repository.mongo.StructuredResumeRepository;

@Service
public class ResumeQualityScoreService {

    private final StructuredResumeRepository structuredResumeRepository;
    private final ResumeRepository resumeRepository;

    public ResumeQualityScoreService(
            StructuredResumeRepository structuredResumeRepository,
            ResumeRepository resumeRepository
    ) {
        this.structuredResumeRepository = structuredResumeRepository;
        this.resumeRepository = resumeRepository;
    }

    public ResumeQualityScoreResult calculate(String resumeId) {

        // 1. Fetch structured resume
        StructuredResumeDocument structured =
                structuredResumeRepository.findById(resumeId)
                        .orElseThrow(() -> new RuntimeException("Structured resume not found"));

        // 2. Fetch raw resume (for format + keywords)
        ResumeDocument raw =
                resumeRepository.findById(structured.getRawResumeId())
                        .orElseThrow(() -> new RuntimeException("Raw resume not found"));

        String rawText = raw.getRawText().toLowerCase();

        // 3. Individual scores
        int formatScore = calculateFormatScore(rawText);
        int sectionScore = calculateSectionScore(structured.getSections());
        int keywordScore = calculateKeywordScore(rawText);
        int skillClarityScore = calculateSkillClarityScore(structured);
        int experienceScore = calculateExperiencePresentationScore(structured);

        int total =
                formatScore
                + sectionScore
                + keywordScore
                + skillClarityScore
                + experienceScore;

        // 4. Build response
        ResumeQualityScoreResult result = new ResumeQualityScoreResult();
        result.setResumeId(resumeId);
        result.setFormatScore(formatScore);
        result.setSectionScore(sectionScore);
        result.setKeywordScore(keywordScore);
        result.setSkillClarityScore(skillClarityScore);
        result.setExperiencePresentationScore(experienceScore);
        result.setTotalScore(total);

        return result;
    }

    // ---------- helper methods ----------

    private int calculateFormatScore(String text) {
        int score = 30;

        long noisySymbols =
                text.chars().filter(c -> "|=<>".indexOf(c) >= 0).count();
        if (noisySymbols > 50) score -= 10;

        long uppercaseLines =
                text.lines().filter(l -> l.equals(l.toUpperCase())).count();
        if (uppercaseLines > 10) score -= 10;

        return Math.max(score, 0);
    }

    private int calculateSectionScore(Map<String, List<String>> sections) {
        if (sections == null) return 0;

        int score = 0;
        if (!sections.getOrDefault("skills", List.of()).isEmpty()) score += 6;
        if (!sections.getOrDefault("education", List.of()).isEmpty()) score += 6;
        if (!sections.getOrDefault("experience", List.of()).isEmpty()) score += 6;
        if (!sections.getOrDefault("projects", List.of()).isEmpty()) score += 6;

        return score;
    }

    private int calculateKeywordScore(String text) {
        int count = 0;
        for (String keyword : KeywordDictionary.TECH_KEYWORDS) {
            if (text.contains(keyword)) {
                count++;
            }
        }

        if (count >= 10) return 25;
        if (count >= 6) return 18;
        if (count >= 3) return 10;
        return 5;
    }

    private int calculateSkillClarityScore(StructuredResumeDocument resume) {

    // ✅ FIX: null-safe guard
    if (resume.getSections() == null) {
        return 0;
    }

    List<String> skills =
            resume.getSections().getOrDefault("skills", List.of());

    if (skills.isEmpty()) return 0;

    int score = 5; // skills exist
    if (skills.size() <= 12) score += 5; // no dumping

    String experienceText =
            String.join(" ",
                    resume.getSections()
                          .getOrDefault("experience", List.of()))
                    .toLowerCase();

    for (String skill : skills) {
        if (experienceText.contains(skill.toLowerCase())) {
            score += 5;
            break;
        }
    }

    return score;
}

   private int calculateExperiencePresentationScore(StructuredResumeDocument resume) {

    // ✅ FIX: null-safe guard
    if (resume.getSections() == null) {
        return 0;
    }

    if (resume.getTotalExperienceYears() <= 0) return 0;

    int lines =
            resume.getSections()
                  .getOrDefault("experience", List.of())
                  .size();

    return lines >= 2 ? 5 : 3;
}

}
