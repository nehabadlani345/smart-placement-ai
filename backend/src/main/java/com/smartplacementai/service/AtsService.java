package com.smartplacementai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartplacementai.dto.AtsReportDto;
import com.smartplacementai.exception.ResumeNotFoundException;
import com.smartplacementai.model.aggregation.ResumeQualityScoreResult;
import com.smartplacementai.model.mongo.AtsReportDocument;
import com.smartplacementai.model.mongo.ResumeDocument;
import com.smartplacementai.model.mongo.StructuredResumeDocument;
import com.smartplacementai.repository.mongo.AtsReportRepository;
import com.smartplacementai.repository.mongo.ResumeRepository;
import com.smartplacementai.service.aggregation.ResumeQualityScoreService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class AtsService {
 private final AtsReportRepository atsReportRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeParserService resumeParserService;
    private final ResumeQualityScoreService resumeQualityScoreService;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(AtsService.class);
   
    public AtsService(ResumeRepository resumeRepository,
                       ResumeParserService resumeParserService,
                       ResumeQualityScoreService resumeQualityScoreService,
                       AiClient aiClient,
                       ObjectMapper objectMapper, AtsReportRepository atsReportRepository) {
        this.atsReportRepository = atsReportRepository;
		this.resumeRepository = resumeRepository;
        this.resumeParserService = resumeParserService;
        this.resumeQualityScoreService = resumeQualityScoreService;
        this.aiClient = aiClient;
        this.objectMapper = objectMapper;
    }

    public AtsReportDto analyzeActiveResume(Long userId) {
        ResumeDocument resume = resumeRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResumeNotFoundException("No active resume found. Upload one first."));

        StructuredResumeDocument structured = resumeParserService.parseAndStructure(resume.getId());
        ResumeQualityScoreResult deterministic = resumeQualityScoreService.calculate(structured.getId());

        AtsReportDto report = new AtsReportDto();
        report.setResumeId(resume.getId());
        report.setTotalScore(deterministic.getTotalScore());
        report.setFormatScore(deterministic.getFormatScore());
        report.setSectionScore(deterministic.getSectionScore());
        report.setKeywordScore(deterministic.getKeywordScore());
        report.setSkillClarityScore(deterministic.getSkillClarityScore());
        report.setExperiencePresentationScore(deterministic.getExperiencePresentationScore());

        enrichWithAi(report, structured);
        // at the end of analyzeActiveResume(), right before "return report;":
        AtsReportDocument snapshot = new AtsReportDocument();
        snapshot.setUserId(userId);
        snapshot.setResumeId(report.getResumeId());
        snapshot.setTotalScore(report.getTotalScore());
        snapshot.setFormatScore(report.getFormatScore());
        snapshot.setSectionScore(report.getSectionScore());
        snapshot.setKeywordScore(report.getKeywordScore());
        snapshot.setSkillClarityScore(report.getSkillClarityScore());
        snapshot.setExperiencePresentationScore(report.getExperiencePresentationScore());
        snapshot.setAiStrengths(report.getAiStrengths());
        snapshot.setAiImprovements(report.getAiImprovements());
        snapshot.setAiSummary(report.getAiSummary());
        atsReportRepository.save(snapshot);
        return report;
    }

    public AtsReportDto getLatestReport(Long userId) {
        AtsReportDocument doc = atsReportRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new com.smartplacementai.exception.AtsReportNotFoundException(
                        "No ATS report yet. Run an analysis first."));

        AtsReportDto dto = new AtsReportDto();
        dto.setResumeId(doc.getResumeId());
        dto.setTotalScore(doc.getTotalScore());
        dto.setFormatScore(doc.getFormatScore());
        dto.setSectionScore(doc.getSectionScore());
        dto.setKeywordScore(doc.getKeywordScore());
        dto.setSkillClarityScore(doc.getSkillClarityScore());
        dto.setExperiencePresentationScore(doc.getExperiencePresentationScore());
        dto.setAiStrengths(doc.getAiStrengths());
        dto.setAiImprovements(doc.getAiImprovements());
        dto.setAiSummary(doc.getAiSummary());
        return dto;
    }
    
    
    private void enrichWithAi(AtsReportDto report, StructuredResumeDocument structured) {
        String systemPrompt = "You are an expert technical resume reviewer for software engineering placements. " +
                "Given structured resume sections and deterministic ATS sub-scores (0-100 each), return JSON with " +
                "exactly these keys: \"strengths\" (array, up to 4 short strings), \"improvements\" (array, up to 4 " +
                "short specific actionable strings), \"summary\" (1-2 sentence plain summary). Ground every point " +
                "in the actual resume content given, never invent details not present.";

        String userPrompt = "Resume sections (JSON): " + toJson(structured.getSections()) +
                "\nDeterministic sub-scores: format=" + report.getFormatScore() +
                ", sections=" + report.getSectionScore() +
                ", keywords=" + report.getKeywordScore() +
                ", skillClarity=" + report.getSkillClarityScore() +
                ", experiencePresentation=" + report.getExperiencePresentationScore();

        try {
            String rawJson = aiClient.generateJson(systemPrompt, userPrompt);
            JsonNode node = objectMapper.readTree(rawJson);
            report.setAiStrengths(toStringList(node.get("strengths")));
            report.setAiImprovements(toStringList(node.get("improvements")));
            report.setAiSummary(node.has("summary") ? node.get("summary").asText() : "");
        } catch (Exception e) {
            // AI is a best-effort layer on top of the deterministic score — never let it break the report.
        	log.error("Gemini call failed: {}", e.getMessage(), e);
        	report.setAiStrengths(List.of());
            report.setAiImprovements(List.of());
            report.setAiSummary("AI suggestions are temporarily unavailable. The scores above are still accurate.");
        }
    }

    private List<String> toStringList(JsonNode arrayNode) {
        List<String> result = new ArrayList<>();
        if (arrayNode != null && arrayNode.isArray()) {
            arrayNode.forEach(n -> result.add(n.asText()));
        }
        return result;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}