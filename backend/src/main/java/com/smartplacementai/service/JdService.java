package com.smartplacementai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartplacementai.dto.JdAnalyzeRequest;
import com.smartplacementai.dto.JdCompatibilityDto;
import com.smartplacementai.exception.ResumeNotFoundException;
import com.smartplacementai.model.matching.MatchingResult;
import com.smartplacementai.model.mongo.JobDescriptionDocument;
import com.smartplacementai.model.mongo.ResumeDocument;
import com.smartplacementai.model.mongo.StructuredResumeDocument;
import com.smartplacementai.repository.mongo.JdReportRepository;
import com.smartplacementai.repository.mongo.JobDescriptionRepository;
import com.smartplacementai.repository.mongo.ResumeRepository;
import com.smartplacementai.service.matching.ResumeJobMatchingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JdService {

    private final ResumeRepository resumeRepository;
    private final ResumeParserService resumeParserService;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final ResumeJobMatchingService matchingService;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;
    private final JdReportRepository jdReportRepository;
    private static final Logger log = LoggerFactory.getLogger(AtsService.class);
    
    public JdService(ResumeRepository resumeRepository,
                      ResumeParserService resumeParserService,
                      JobDescriptionRepository jobDescriptionRepository,
                      ResumeJobMatchingService matchingService,
                      AiClient aiClient,
                      ObjectMapper objectMapper, JdReportRepository jdReportRepository) {
        this.resumeRepository = resumeRepository;
        this.resumeParserService = resumeParserService;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.matchingService = matchingService;
        this.aiClient = aiClient;
        this.objectMapper = objectMapper;
		this.jdReportRepository = jdReportRepository;
    }

    public JdCompatibilityDto analyze(Long userId, JdAnalyzeRequest request) {
        ResumeDocument resume = resumeRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResumeNotFoundException("No active resume found. Upload one first."));

        StructuredResumeDocument structured = resumeParserService.parseAndStructure(resume.getId());

        JobDescriptionDocument job = new JobDescriptionDocument();
        job.setCompanyName(request.getCompanyName());
        job.setRole(request.getRole());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setPreferredSkills(request.getPreferredSkills());
        job.setMinExperience(request.getMinExperience());
        job.setMaxExperience(request.getMaxExperience());
        job.setCreatedAt(LocalDateTime.now());
        JobDescriptionDocument savedJob = jobDescriptionRepository.save(job);

        MatchingResult result = matchingService.match(structured.getId(), savedJob.getId());

        JdCompatibilityDto dto = new JdCompatibilityDto();
        dto.setJobId(savedJob.getId());
        dto.setAtsScore(result.getAtsScore());
        dto.setMatchedRequiredSkills(result.getMatchedRequiredSkills());
        dto.setMissingRequiredSkills(result.getMissingRequiredSkills());
        dto.setMatchedPreferredSkills(result.getMatchedPreferredSkills());
        dto.setMissingPreferredSkills(result.getMissingPreferredSkills());
        dto.setScoreBreakdown(result.getScoreBreakdown());

        enrichWithAi(dto, request);
        
        jdReportRepository.save(new com.smartplacementai.model.mongo.JdReportDocument(
                userId, request.getRole(), dto.getAtsScore(), dto.getMissingRequiredSkills()));
        
        return dto;
    }

    private void enrichWithAi(JdCompatibilityDto dto, JdAnalyzeRequest request) {
        String systemPrompt = "You are a career coach explaining a resume-vs-job-description match. " +
                "IMPORTANT: a 'missing' skill means it wasn't found as text in the resume — this does NOT necessarily " +
                "mean the candidate lacks the skill, only that it isn't evidenced in writing. Reflect this nuance. " +
                "Return JSON with exactly these keys: \"explanation\" (2-3 honest, specific sentences), " +
                "\"recommendedActions\" (array, up to 4 short concrete next steps).";

        String userPrompt = "Target role: " + request.getRole() +
                "\nMatched required skills: " + dto.getMatchedRequiredSkills() +
                "\nMissing required skills: " + dto.getMissingRequiredSkills() +
                "\nMatched preferred skills: " + dto.getMatchedPreferredSkills() +
                "\nMissing preferred skills: " + dto.getMissingPreferredSkills();

        try {
            String rawJson = aiClient.generateJson(systemPrompt, userPrompt);
            JsonNode node = objectMapper.readTree(rawJson);
            dto.setAiExplanation(node.has("explanation") ? node.get("explanation").asText() : "");
            dto.setAiRecommendedActions(toStringList(node.get("recommendedActions")));
        } catch (Exception e) {
        	log.error("Gemini call failed: {}", e.getMessage(), e);
            dto.setAiExplanation("AI explanation is temporarily unavailable. The match scores above are still accurate.");
            dto.setAiRecommendedActions(List.of());
        }
    }

    private List<String> toStringList(JsonNode arrayNode) {
        List<String> result = new ArrayList<>();
        if (arrayNode != null && arrayNode.isArray()) {
            arrayNode.forEach(n -> result.add(n.asText()));
        }
        return result;
    }
}