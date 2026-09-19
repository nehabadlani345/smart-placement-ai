package com.smartplacementai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartplacementai.dto.RoadmapDto;
import com.smartplacementai.dto.RoadmapGenerateRequest;
import com.smartplacementai.exception.AiGenerationException;
import com.smartplacementai.exception.ResumeNotFoundException;
import com.smartplacementai.exception.RoadmapNotFoundException;
import com.smartplacementai.model.mongo.ResumeDocument;
import com.smartplacementai.model.mongo.RoadmapDocument;
import com.smartplacementai.model.mongo.StructuredResumeDocument;
import com.smartplacementai.repository.mongo.ResumeRepository;
import com.smartplacementai.repository.mongo.RoadmapRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeParserService resumeParserService;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(AtsService.class);
    
    public RoadmapService(RoadmapRepository roadmapRepository,
                           ResumeRepository resumeRepository,
                           ResumeParserService resumeParserService,
                           AiClient aiClient,
                           ObjectMapper objectMapper) {
        this.roadmapRepository = roadmapRepository;
        this.resumeRepository = resumeRepository;
        this.resumeParserService = resumeParserService;
        this.aiClient = aiClient;
        this.objectMapper = objectMapper;
    }

    public RoadmapDto generate(Long userId, RoadmapGenerateRequest request) {
        ResumeDocument resume = resumeRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResumeNotFoundException("No active resume found. Upload one first."));

        StructuredResumeDocument structured = resumeParserService.parseAndStructure(resume.getId());

        List<RoadmapDocument.Phase> phases = callAiForPhases(structured, request);

        roadmapRepository.findByUserIdAndActiveTrue(userId).ifPresent(prev -> {
            prev.setActive(false);
            roadmapRepository.save(prev);
        });

        int nextVersion = roadmapRepository.findByUserIdOrderByVersionDesc(userId)
                .stream().findFirst().map(r -> r.getVersion() == null ? 1 : r.getVersion() + 1).orElse(1);

        RoadmapDocument doc = new RoadmapDocument();
        doc.setUserId(userId);
        doc.setTargetRole(request.getTargetRole());
        doc.setWeeklyStudyHours(request.getWeeklyStudyHours());
        doc.setVersion(nextVersion);
        doc.setActive(true);
        doc.setPhases(phases);

        RoadmapDocument saved = roadmapRepository.save(doc);
        return toDto(saved);
    }

    public RoadmapDto getCurrent(Long userId) {
        RoadmapDocument doc = roadmapRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new RoadmapNotFoundException("No roadmap yet. Generate one first."));
        return toDto(doc);
    }

    public RoadmapDto completeTask(Long userId, String taskId, boolean completed) {
        RoadmapDocument doc = roadmapRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new RoadmapNotFoundException("No active roadmap found."));

        doc.getPhases().stream()
                .flatMap(p -> p.getTasks().stream())
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .ifPresent(t -> t.setCompleted(completed));

        RoadmapDocument saved = roadmapRepository.save(doc);
        return toDto(saved);
    }

    /** Study Planner (F09): current week's tasks, priority-sorted, within the roadmap the user already has. */
    public RoadmapDto.PhaseDto getPlannerForWeek(Long userId, int weekNumber) {
        RoadmapDocument doc = roadmapRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new RoadmapNotFoundException("No roadmap yet. Generate one first."));

        RoadmapDocument.Phase phase = doc.getPhases().stream()
                .filter(p -> weekNumber == (p.getWeekNumber() == null ? -1 : p.getWeekNumber()))
                .findFirst()
                .orElseThrow(() -> new RoadmapNotFoundException("No plan found for week " + weekNumber));

        List<RoadmapDocument.Task> sorted = phase.getTasks().stream()
                .sorted(Comparator.comparingInt(this::priorityWeight))
                .toList();

        RoadmapDocument.Phase sortedPhase = new RoadmapDocument.Phase();
        sortedPhase.setTitle(phase.getTitle());
        sortedPhase.setWeekNumber(phase.getWeekNumber());
        sortedPhase.setTasks(sorted);

        return toPhaseDto(sortedPhase);
    }

    private int priorityWeight(RoadmapDocument.Task task) {
        if (task.getPriority() == null) return 2;
        return switch (task.getPriority().toUpperCase()) {
            case "HIGH" -> 0;
            case "MEDIUM" -> 1;
            default -> 2;
        };
    }

    private List<RoadmapDocument.Phase> callAiForPhases(StructuredResumeDocument structured, RoadmapGenerateRequest request) {
        String systemPrompt = "You are a placement preparation coach designing a study roadmap for a software " +
                "engineering job seeker. Given their resume sections, target role, and weekly available study hours, " +
                "produce a realistic multi-week plan. Return JSON with exactly this shape: " +
                "{\"phases\": [{\"title\": string, \"weekNumber\": integer starting at 1, \"tasks\": " +
                "[{\"title\": string, \"description\": string, \"estimatedHours\": number, " +
                "\"priority\": \"HIGH\"|\"MEDIUM\"|\"LOW\"}]}]}. " +
                "Generate 4 to 6 phases (weeks). Each week's total estimatedHours across its tasks should not " +
                "exceed the given weekly study hours by more than 20%. Base gaps and priorities on the resume's " +
                "actual current skills versus what the target role typically requires — don't invent resume content.";

        String userPrompt = "Target role: " + request.getTargetRole() +
                "\nWeekly study hours available: " + request.getWeeklyStudyHours() +
                "\nResume sections (JSON): " + toJson(structured.getSections());

        String rawJson;
        try {
            rawJson = aiClient.generateJson(systemPrompt, userPrompt);
        } catch (Exception e) {
        	log.error("Gemini call failed: {}", e.getMessage(), e);
            throw new AiGenerationException("Could not generate a roadmap right now. Please try again shortly.");
        }

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode phasesNode = root.get("phases");
            List<RoadmapDocument.Phase> phases = new ArrayList<>();

            if (phasesNode != null && phasesNode.isArray()) {
                for (JsonNode phaseNode : phasesNode) {
                    RoadmapDocument.Phase phase = new RoadmapDocument.Phase();
                    phase.setTitle(phaseNode.path("title").asText("Untitled phase"));
                    phase.setWeekNumber(phaseNode.path("weekNumber").asInt(0));

                    List<RoadmapDocument.Task> tasks = new ArrayList<>();
                    JsonNode tasksNode = phaseNode.get("tasks");
                    if (tasksNode != null && tasksNode.isArray()) {
                        for (JsonNode taskNode : tasksNode) {
                            RoadmapDocument.Task task = new RoadmapDocument.Task();
                            task.setTitle(taskNode.path("title").asText("Untitled task"));
                            task.setDescription(taskNode.path("description").asText(""));
                            task.setEstimatedHours(taskNode.path("estimatedHours").asDouble(1.0));
                            task.setPriority(taskNode.path("priority").asText("MEDIUM"));
                            tasks.add(task);
                        }
                    }
                    phase.setTasks(tasks);
                    phases.add(phase);
                }
            }

            if (phases.isEmpty()) {
                throw new AiGenerationException("AI returned an empty roadmap. Please try again.");
            }
            return phases;

        } catch (AiGenerationException e) {
            throw e;
        } catch (Exception e) {
        	log.error("Gemini call failed: {}", e.getMessage(), e);
            // Malformed JSON from the model — this is the schema-validation safety net the spec calls for.
            throw new AiGenerationException("The AI response couldn't be parsed into a roadmap. Please try again.");
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }

    private RoadmapDto toDto(RoadmapDocument doc) {
        RoadmapDto dto = new RoadmapDto();
        dto.setId(doc.getId());
        dto.setTargetRole(doc.getTargetRole());
        dto.setWeeklyStudyHours(doc.getWeeklyStudyHours());
        dto.setVersion(doc.getVersion());
        dto.setPhases(doc.getPhases().stream().map(this::toPhaseDto).toList());
        return dto;
    }

    private RoadmapDto.PhaseDto toPhaseDto(RoadmapDocument.Phase phase) {
        RoadmapDto.PhaseDto p = new RoadmapDto.PhaseDto();
        p.setTitle(phase.getTitle());
        p.setWeekNumber(phase.getWeekNumber());
        p.setTasks(phase.getTasks().stream().map(this::toTaskDto).toList());
        return p;
    }

    private RoadmapDto.TaskDto toTaskDto(RoadmapDocument.Task task) {
        RoadmapDto.TaskDto t = new RoadmapDto.TaskDto();
        t.setId(task.getId());
        t.setTitle(task.getTitle());
        t.setDescription(task.getDescription());
        t.setEstimatedHours(task.getEstimatedHours());
        t.setPriority(task.getPriority());
        t.setCompleted(task.isCompleted());
        return t;
    }
}