package com.smartplacementai.service.aggregation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.smartplacementai.model.aggregation.PlacementReadinessResult;
import com.smartplacementai.model.mongo.ResumeJobMatchDocument;
import com.smartplacementai.repository.mongo.ResumeJobMatchRepository;

@Service
public class PlacementReadinessService {

    private final ResumeJobMatchRepository matchRepository;

    public PlacementReadinessService(ResumeJobMatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public PlacementReadinessResult calculateReadiness(String resumeId) {

        List<ResumeJobMatchDocument> matches =
                matchRepository.findByResumeId(resumeId);

        if (matches.isEmpty()) {
            throw new RuntimeException("No job matches found for resume");
        }

        // ---------------------------
        // 1️⃣ READINESS SCORE (AVG ATS)
        // ---------------------------
        double avgAts =
                matches.stream()
                        .mapToInt(ResumeJobMatchDocument::getAtsScore)
                        .average()
                        .orElse(0.0);

        // ---------------------------
        // 2️⃣ WEAK SKILLS FREQUENCY
        // ---------------------------
        Map<String, Integer> weakSkillCount = new HashMap<>();

        for (ResumeJobMatchDocument match : matches) {
            for (String skill : match.getMissingRequiredSkills()) {
                weakSkillCount.put(
                        skill,
                        weakSkillCount.getOrDefault(skill, 0) + 1
                );
            }
        }

        List<String> weakestSkills =
                weakSkillCount.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .map(Map.Entry::getKey)
                        .toList();

        // ---------------------------
        // 3️⃣ STRONG SKILLS FREQUENCY
        // ---------------------------
        Map<String, Integer> strongSkillCount = new HashMap<>();

        for (ResumeJobMatchDocument match : matches) {
            for (String skill : match.getMatchedRequiredSkills()) {
                strongSkillCount.put(
                        skill,
                        strongSkillCount.getOrDefault(skill, 0) + 1
                );
            }
        }

        List<String> strongestSkills =
                strongSkillCount.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .map(Map.Entry::getKey)
                        .toList();

        // ---------------------------
        // 4️⃣ BUILD RESULT
        // ---------------------------
        PlacementReadinessResult result = new PlacementReadinessResult();
        result.setResumeId(resumeId);
        result.setReadinessScore(avgAts);
        result.setWeakestSkills(weakestSkills);
        result.setStrongestSkills(strongestSkills);
        result.setTotalJobsAnalyzed(matches.size());

        return result;
    }
}
