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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeParserService resumeParserService;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;

    private static final Logger log =
            LoggerFactory.getLogger(RoadmapService.class);

    public RoadmapService(
            RoadmapRepository roadmapRepository,
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

    // =========================================================
    // GENERATE ROADMAP
    // =========================================================

    public RoadmapDto generate(
            Long userId,
            RoadmapGenerateRequest request) {

        ResumeDocument resume =
                resumeRepository.findByUserIdAndActiveTrue(userId)
                        .orElseThrow(() ->
                                new ResumeNotFoundException(
                                        "No active resume found. Upload one first."
                                )
                        );

        StructuredResumeDocument structured =
                resumeParserService.parseAndStructure(resume.getId());

        /*
         * Keep duration within safe AI generation limits.
         */
        int clampedDuration =
                clampDuration(
                        request.getDurationValue(),
                        request.getDurationUnit()
                );

        /*
         * Generate the roadmap phases from AI.
         */
        List<RoadmapDocument.Phase> phases =
                callAiForPhases(
                        structured,
                        request,
                        clampedDuration
                );

        /*
         * Deactivate previous active roadmap.
         */
        roadmapRepository.findByUserIdAndActiveTrue(userId)
                .ifPresent(previous -> {

                    previous.setActive(false);

                    roadmapRepository.save(previous);
                });

        /*
         * Calculate next roadmap version.
         */
        int nextVersion =
                roadmapRepository
                        .findByUserIdOrderByVersionDesc(userId)
                        .stream()
                        .findFirst()
                        .map(r ->
                                r.getVersion() == null
                                        ? 1
                                        : r.getVersion() + 1
                        )
                        .orElse(1);

        /*
         * Create new roadmap document.
         */
        RoadmapDocument doc = new RoadmapDocument();

        doc.setUserId(userId);

        doc.setTargetRole(request.getTargetRole());

        doc.setDurationValue(clampedDuration);

        doc.setDurationUnit(
                request.getDurationUnit().toUpperCase()
        );

        doc.setStudyHoursPerPeriod(
                request.getStudyHoursPerPeriod()
        );

        doc.setVersion(nextVersion);

        doc.setActive(true);

        doc.setPhases(phases);

        RoadmapDocument saved =
                roadmapRepository.save(doc);

        return toDto(saved);
    }

    // =========================================================
    // CLAMP DURATION
    // =========================================================

    /*
     * Keeps AI output bounded and parseable regardless
     * of what the user types.
     *
     * DAY   -> maximum 30
     * WEEK  -> maximum 12
     * MONTH -> maximum 6
     */
    private int clampDuration(
            int requested,
            String unit) {

        int max = switch (unit.toUpperCase()) {

            case "DAY" -> 30;

            case "MONTH" -> 6;

            default -> 12; // WEEK
        };

        return Math.min(
                Math.max(requested, 1),
                max
        );
    }

    // =========================================================
    // GET CURRENT ROADMAP
    // =========================================================

    public RoadmapDto getCurrent(Long userId) {

        RoadmapDocument doc =
                roadmapRepository
                        .findByUserIdAndActiveTrue(userId)
                        .orElseThrow(() ->
                                new RoadmapNotFoundException(
                                        "No roadmap yet. Generate one first."
                                )
                        );

        return toDto(doc);
    }

    // =========================================================
    // COMPLETE / UNCOMPLETE TASK
    // =========================================================

    public RoadmapDto completeTask(
            Long userId,
            String taskId,
            boolean completed) {

        RoadmapDocument doc =
                roadmapRepository
                        .findByUserIdAndActiveTrue(userId)
                        .orElseThrow(() ->
                                new RoadmapNotFoundException(
                                        "No active roadmap found."
                                )
                        );

        doc.getPhases()
                .stream()
                .flatMap(
                        phase -> phase.getTasks().stream()
                )
                .filter(
                        task -> task.getId().equals(taskId)
                )
                .findFirst()
                .ifPresent(
                        task -> task.setCompleted(completed)
                );

        RoadmapDocument saved =
                roadmapRepository.save(doc);

        return toDto(saved);
    }

    // =========================================================
    // STUDY PLANNER
    // =========================================================

    /**
     * Study Planner (F09):
     *
     * Returns tasks for a particular period,
     * priority sorted.
     *
     * Works for:
     *
     * DAY
     * WEEK
     * MONTH
     *
     * Example:
     *
     * periodNumber = 1
     *
     * could mean:
     * Day 1
     * Week 1
     * Month 1
     *
     * depending on durationUnit.
     */
    public RoadmapDto.PhaseDto getPlannerForPeriod(
            Long userId,
            int periodNumber) {

        RoadmapDocument doc =
                roadmapRepository
                        .findByUserIdAndActiveTrue(userId)
                        .orElseThrow(() ->
                                new RoadmapNotFoundException(
                                        "No roadmap yet. Generate one first."
                                )
                        );

        RoadmapDocument.Phase phase =
                doc.getPhases()
                        .stream()
                        .filter(
                                p -> periodNumber ==
                                        (
                                                p.getPeriodNumber() == null
                                                        ? -1
                                                        : p.getPeriodNumber()
                                        )
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new RoadmapNotFoundException(
                                        "No plan found for period "
                                                + periodNumber
                                )
                        );

        /*
         * Sort tasks:
         *
         * HIGH   -> first
         * MEDIUM -> second
         * LOW    -> last
         */
        List<RoadmapDocument.Task> sorted =
                phase.getTasks()
                        .stream()
                        .sorted(
                                Comparator.comparingInt(
                                        this::priorityWeight
                                )
                        )
                        .toList();

        /*
         * Create a new phase object so that the original
         * roadmap task ordering is not modified.
         */
        RoadmapDocument.Phase sortedPhase =
                new RoadmapDocument.Phase();

        sortedPhase.setTitle(
                phase.getTitle()
        );

        sortedPhase.setPeriodNumber(
                phase.getPeriodNumber()
        );

        sortedPhase.setPeriodLabel(
                phase.getPeriodLabel()
        );

        sortedPhase.setTasks(sorted);

        return toPhaseDto(sortedPhase);
    }

    // =========================================================
    // TASK PRIORITY
    // =========================================================

    private int priorityWeight(
            RoadmapDocument.Task task) {

        if (task.getPriority() == null) {
            return 2;
        }

        return switch (
                task.getPriority().toUpperCase()
        ) {

            case "HIGH" -> 0;

            case "MEDIUM" -> 1;

            default -> 2;
        };
    }

    // =========================================================
    // AI ROADMAP GENERATION
    // =========================================================

    private List<RoadmapDocument.Phase> callAiForPhases(
            StructuredResumeDocument structured,
            RoadmapGenerateRequest request,
            int duration) {

        String unit =
                request.getDurationUnit().toUpperCase();

        String unitLabel =
                unit.charAt(0)
                        + unit.substring(1).toLowerCase();

        /*
         * Example:
         *
         * DAY   -> Day
         * WEEK  -> Week
         * MONTH -> Month
         */
        String systemPrompt =
                "You are a placement preparation coach designing a study roadmap for a software "
                        + "engineering job seeker. Given their resume sections, target role, and available study hours per "
                        + "period, produce a realistic plan spanning exactly "
                        + duration
                        + " "
                        + unitLabel.toLowerCase()
                        + "(s). "

                        + "Return JSON with exactly this shape: "

                        + "{\"phases\": [{"
                        + "\"title\": string, "
                        + "\"periodNumber\": integer starting at 1, "
                        + "\"periodLabel\": \""
                        + unitLabel
                        + " N\" (matching periodNumber), "
                        + "\"tasks\": ["
                        + "{\"title\": string, "
                        + "\"description\": string, "
                        + "\"estimatedHours\": number, "
                        + "\"priority\": \"HIGH\"|\"MEDIUM\"|\"LOW\"}"
                        + "]"
                        + "}]}."

                        + " Produce exactly "
                        + duration
                        + " phases, one per "
                        + unitLabel.toLowerCase()
                        + ". "

                        + "Each phase's total estimatedHours across its tasks should not exceed "
                        + request.getStudyHoursPerPeriod()
                        + " hours (the stated hours available per "
                        + unitLabel.toLowerCase()
                        + ") by more than 20%. "

                        + "If the unit is DAY, tasks should be small and specific — "
                        + "a single topic or a short practice session per day, "
                        + "not a full week's worth of content. "

                        + "Base gaps and priorities on the resume's actual current skills "
                        + "versus what the target role typically requires — "
                        + "don't invent resume content.";

        String userPrompt =
                "Target role: "
                        + request.getTargetRole()

                        + "\nDuration: "
                        + duration
                        + " "
                        + unitLabel.toLowerCase()
                        + "(s)"

                        + "\nStudy hours available per "
                        + unitLabel.toLowerCase()
                        + ": "
                        + request.getStudyHoursPerPeriod()

                        + "\nResume sections (JSON): "
                        + toJson(
                                structured.getSections()
                        );

        String rawJson;

        // =====================================================
        // CALL AI
        // =====================================================

        try {

            rawJson =
                    aiClient.generateJson(
                            systemPrompt,
                            userPrompt
                    );

        } catch (Exception e) {

            log.error(
                    "Gemini call failed: {}",
                    e.getMessage(),
                    e
            );

            throw new AiGenerationException(
                    "Could not generate a roadmap right now. "
                            + "Please try again shortly."
            );
        }

        // =====================================================
        // PARSE AI RESPONSE
        // =====================================================

        try {

            JsonNode root =
                    objectMapper.readTree(rawJson);

            JsonNode phasesNode =
                    root.get("phases");

            List<RoadmapDocument.Phase> phases =
                    new ArrayList<>();

            if (phasesNode != null
                    && phasesNode.isArray()) {

                for (JsonNode phaseNode : phasesNode) {

                    RoadmapDocument.Phase phase =
                            new RoadmapDocument.Phase();

                    phase.setTitle(
                            phaseNode
                                    .path("title")
                                    .asText("Untitled phase")
                    );

                    phase.setPeriodNumber(
                            phaseNode
                                    .path("periodNumber")
                                    .asInt(0)
                    );

                    phase.setPeriodLabel(
                            phaseNode
                                    .path("periodLabel")
                                    .asText(
                                            unitLabel
                                                    + " "
                                                    + phase.getPeriodNumber()
                                    )
                    );

                    List<RoadmapDocument.Task> tasks =
                            new ArrayList<>();

                    JsonNode tasksNode =
                            phaseNode.get("tasks");

                    if (tasksNode != null
                            && tasksNode.isArray()) {

                        for (JsonNode taskNode :
                                tasksNode) {

                            RoadmapDocument.Task task =
                                    new RoadmapDocument.Task();

                            task.setTitle(
                                    taskNode
                                            .path("title")
                                            .asText(
                                                    "Untitled task"
                                            )
                            );

                            task.setDescription(
                                    taskNode
                                            .path("description")
                                            .asText("")
                            );

                            task.setEstimatedHours(
                                    taskNode
                                            .path("estimatedHours")
                                            .asDouble(1.0)
                            );

                            task.setPriority(
                                    taskNode
                                            .path("priority")
                                            .asText("MEDIUM")
                            );

                            tasks.add(task);
                        }
                    }

                    phase.setTasks(tasks);

                    phases.add(phase);
                }
            }

            if (phases.isEmpty()) {

                throw new AiGenerationException(
                        "AI returned an empty roadmap. "
                                + "Please try again."
                );
            }

            return phases;

        } catch (AiGenerationException e) {

            throw e;

        } catch (Exception e) {

            log.error(
                    "Gemini call failed: {}",
                    e.getMessage(),
                    e
            );

            throw new AiGenerationException(
                    "The AI response couldn't be parsed into a roadmap. "
                            + "Please try again."
            );
        }
    }

    // =========================================================
    // OBJECT TO JSON
    // =========================================================

    private String toJson(Object obj) {

        try {

            return objectMapper.writeValueAsString(obj);

        } catch (Exception e) {

            log.error(
                    "Could not convert object to JSON",
                    e
            );

            return "{}";
        }
    }

    // =========================================================
    // DOCUMENT -> DTO
    // =========================================================

    private RoadmapDto toDto(
            RoadmapDocument doc) {

        RoadmapDto dto =
                new RoadmapDto();

        dto.setId(
                doc.getId()
        );

        dto.setTargetRole(
                doc.getTargetRole()
        );

        dto.setDurationValue(
                doc.getDurationValue()
        );

        dto.setDurationUnit(
                doc.getDurationUnit()
        );

        dto.setStudyHoursPerPeriod(
                doc.getStudyHoursPerPeriod()
        );

        dto.setVersion(
                doc.getVersion()
        );

        if (doc.getPhases() != null) {

            dto.setPhases(
                    doc.getPhases()
                            .stream()
                            .map(this::toPhaseDto)
                            .toList()
            );

        } else {

            dto.setPhases(
                    new ArrayList<>()
            );
        }

        return dto;
    }

    // =========================================================
    // PHASE -> PHASE DTO
    // =========================================================

    private RoadmapDto.PhaseDto toPhaseDto(
            RoadmapDocument.Phase phase) {

        RoadmapDto.PhaseDto p =
                new RoadmapDto.PhaseDto();

        p.setTitle(
                phase.getTitle()
        );

        p.setPeriodNumber(
                phase.getPeriodNumber()
        );

        p.setPeriodLabel(
                phase.getPeriodLabel()
        );

        if (phase.getTasks() != null) {

            p.setTasks(
                    phase.getTasks()
                            .stream()
                            .map(this::toTaskDto)
                            .toList()
            );

        } else {

            p.setTasks(
                    new ArrayList<>()
            );
        }

        return p;
    }

    // =========================================================
    // TASK -> TASK DTO
    // =========================================================

    private RoadmapDto.TaskDto toTaskDto(
            RoadmapDocument.Task task) {

        RoadmapDto.TaskDto t =
                new RoadmapDto.TaskDto();

        t.setId(
                task.getId()
        );

        t.setTitle(
                task.getTitle()
        );

        t.setDescription(
                task.getDescription()
        );

        t.setEstimatedHours(
                task.getEstimatedHours()
        );

        t.setPriority(
                task.getPriority()
        );

        t.setCompleted(
                task.isCompleted()
        );

        return t;
    }
}