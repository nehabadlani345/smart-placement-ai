package com.smartplacementai.service;

import com.smartplacementai.dto.DashboardDto;
import com.smartplacementai.model.mongo.AtsReportDocument;
import com.smartplacementai.model.mongo.JdReportDocument;
import com.smartplacementai.model.mongo.RoadmapDocument;
import com.smartplacementai.repository.mongo.AtsReportRepository;
import com.smartplacementai.repository.mongo.JdReportRepository;
import com.smartplacementai.repository.mongo.ResumeRepository;
import com.smartplacementai.repository.mongo.RoadmapRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnalyticsService {

    private final ResumeRepository resumeRepository;
    private final AtsReportRepository atsReportRepository;
    private final JdReportRepository jdReportRepository;
    private final RoadmapRepository roadmapRepository;

    @Value("${app.readiness.weight-ats:0.4}")
    private double atsWeight;
    @Value("${app.readiness.weight-jd:0.3}")
    private double jdWeight;
    @Value("${app.readiness.weight-roadmap:0.3}")
    private double roadmapWeight;

    public AnalyticsService(ResumeRepository resumeRepository,
                             AtsReportRepository atsReportRepository,
                             JdReportRepository jdReportRepository,
                             RoadmapRepository roadmapRepository) {
        this.resumeRepository = resumeRepository;
        this.atsReportRepository = atsReportRepository;
        this.jdReportRepository = jdReportRepository;
        this.roadmapRepository = roadmapRepository;
    }

    public DashboardDto getDashboard(Long userId) {
        boolean hasResume = resumeRepository.findByUserIdAndActiveTrue(userId).isPresent();
        Optional<AtsReportDocument> latestAts = atsReportRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
        Optional<JdReportDocument> latestJd = jdReportRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
        Optional<RoadmapDocument> activeRoadmap = roadmapRepository.findByUserIdAndActiveTrue(userId);

        Integer atsScore = latestAts.map(AtsReportDocument::getTotalScore).orElse(null);
        Integer jdScore = latestJd.map(JdReportDocument::getAtsScore).orElse(null);
        Integer roadmapPercent = activeRoadmap.map(this::calculateCompletion).orElse(null);

        DashboardDto dto = new DashboardDto();
        dto.setReadinessScore(calculateWeightedScore(atsScore, jdScore, roadmapPercent));
        dto.setAtsScore(atsScore);
        dto.setJdScore(jdScore);
        dto.setRoadmapCompletionPercent(roadmapPercent);
        dto.setTopGaps(latestJd.map(JdReportDocument::getMissingRequiredSkills).orElse(List.of()));
        dto.setNextActions(buildNextActions(hasResume, latestAts.isPresent(), latestJd.isPresent(), activeRoadmap, roadmapPercent));

        return dto;
    }

    private int calculateWeightedScore(Integer ats, Integer jd, Integer roadmap) {
        double weightedSum = 0;
        double totalWeight = 0;

        if (ats != null) { weightedSum += ats * atsWeight; totalWeight += atsWeight; }
        if (jd != null) { weightedSum += jd * jdWeight; totalWeight += jdWeight; }
        if (roadmap != null) { weightedSum += roadmap * roadmapWeight; totalWeight += roadmapWeight; }

        return totalWeight > 0 ? (int) Math.round(weightedSum / totalWeight) : 0;
    }

    private int calculateCompletion(RoadmapDocument roadmap) {
        List<RoadmapDocument.Task> allTasks = roadmap.getPhases().stream()
                .flatMap(p -> p.getTasks().stream())
                .toList();
        if (allTasks.isEmpty()) return 0;
        long done = allTasks.stream().filter(RoadmapDocument.Task::isCompleted).count();
        return (int) Math.round((done * 100.0) / allTasks.size());
    }

    private List<DashboardDto.NextActionDto> buildNextActions(boolean hasResume, boolean hasAts, boolean hasJd,
                                                                Optional<RoadmapDocument> roadmap, Integer roadmapPercent) {
        List<DashboardDto.NextActionDto> actions = new ArrayList<>();

        if (!hasResume) {
            actions.add(new DashboardDto.NextActionDto("Upload your resume",
                    "Everything else builds on this — start here.", "/resumes"));
        }
        if (hasResume && !hasAts) {
            actions.add(new DashboardDto.NextActionDto("Run your ATS analysis",
                    "Get an AI-scored breakdown of your resume.", "/ats"));
        }
        if (hasResume && !hasJd) {
            actions.add(new DashboardDto.NextActionDto("Check a JD compatibility",
                    "See how you match against a real target role.", "/jd-match"));
        }
        if (roadmap.isEmpty()) {
            actions.add(new DashboardDto.NextActionDto("Generate your roadmap",
                    "Get a personalized week-by-week study plan.", "/roadmap"));
        } else if (roadmapPercent != null && roadmapPercent < 100) {
            actions.add(new DashboardDto.NextActionDto("Continue your roadmap",
                    roadmapPercent + "% complete — keep going.", "/roadmap"));
        }

        return actions.stream().limit(3).toList();
    }
}