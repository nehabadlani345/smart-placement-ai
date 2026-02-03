package com.smartplacementai.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.smartplacementai.model.mongo.ResumeDocument;
import com.smartplacementai.model.mongo.StructuredResumeDocument;
import com.smartplacementai.repository.mongo.ResumeRepository;
import com.smartplacementai.repository.mongo.StructuredResumeRepository;

@Service
public class ResumeParserService {

    private final ResumeRepository resumeRepository;
    private final StructuredResumeRepository structuredResumeRepository;

    public ResumeParserService(
            ResumeRepository resumeRepository,
            StructuredResumeRepository structuredResumeRepository
    ) {
        this.resumeRepository = resumeRepository;
        this.structuredResumeRepository = structuredResumeRepository;
    }

    public StructuredResumeDocument parseAndStructure(String resumeId) {

        ResumeDocument rawResume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        Map<String, List<String>> sections =
                extractSections(rawResume.getRawText());
        
        // normalize skills
if (sections.containsKey("skills")) {
    sections.put(
            "skills",
            normalizeSkills(sections.get("skills"))
    );
}

// normalize education
if (sections.containsKey("education")) {
    sections.put(
            "education",
            normalizeGenericSection(sections.get("education"))
    );
}

// normalize experience
if (sections.containsKey("experience")) {
    sections.put(
            "experience",
            normalizeGenericSection(sections.get("experience"))
    );
}

// normalize projects
if (sections.containsKey("projects")) {
    sections.put(
            "projects",
            normalizeGenericSection(sections.get("projects"))
    );
}

          Map<String, Double> confidenceScores =
            calculateConfidenceScores(sections);


        StructuredResumeDocument structured = new StructuredResumeDocument();
        structured.setRawResumeId(resumeId);
        structured.setUserId(rawResume.getUserId());
        structured.setSections(sections);
        structured.setConfidence(confidenceScores);
        structured.setStructuredAt(LocalDateTime.now());

        return structuredResumeRepository.save(structured);
    }



    private Map<String, List<String>> extractSections(String rawText) {

        Map<String, List<String>> result = new HashMap<>();
        ResumeSection currentSection = null;

        String[] lines = rawText.split("\\r?\\n");

        for (String line : lines) {

             String cleaned = normalizeLine(line);
            if (cleaned.isEmpty()) {
                continue;
            }

            ResumeSection detected = detectSection(cleaned.toLowerCase());
            if (detected != null) {
                currentSection = detected;
                result.putIfAbsent(
                        currentSection.name().toLowerCase(),
                        new ArrayList<>()
                );
                continue;
            }

            if (currentSection != null) {
                result.get(currentSection.name().toLowerCase())
                        .add(cleaned);
            }
        }
        System.out.println("Detected section: " + currentSection);
        return result;
    }

      private String normalizeLine(String line) {

          String cleaned = line
                  .replaceAll("[•▪▫*-]", "")
                  .trim();

          // ignore separator lines
          if (cleaned.matches("^[=_-]{3,}$")) {
              return "";
          }

          return cleaned;
      }

      private List<String> normalizeSkills(List<String> rawSkillsLines) {

          Set<String> normalized = new LinkedHashSet<>();

          for (String line : rawSkillsLines) {

              String[] tokens = line.split("[,/|]");

              for (String token : tokens) {
                  String skill = token.trim().toLowerCase();

                  if (!skill.isEmpty()) {
                      normalized.add(skill);
                  }
              }
          }
          return new ArrayList<>(normalized);
      }

    private List<String> normalizeGenericSection(List<String> rawLines) {

      Set<String> normalized = new LinkedHashSet<>();

      for (String line : rawLines) {

          String cleaned = line
                  .replaceAll("[•▪▫*-]", "")
                  .trim();

          // ignore very small / noisy lines
          if (cleaned.length() < 3) {
              continue;
          }

          normalized.add(cleaned);
      }
      return new ArrayList<>(normalized);
    }


    private Map<String, Double> calculateConfidenceScores(
        Map<String, List<String>> sections) {

    Map<String, Double> confidence = new HashMap<>();

    for (Map.Entry<String, List<String>> entry : sections.entrySet()) {

        String section = entry.getKey();
        int size = entry.getValue().size();

        double score = 0.0;

        switch (section) {

            case "skills":
                score = size >= 5 ? 1.0 : size >= 3 ? 0.7 : 0.4;
                break;

            case "education":
                score = size >= 1 ? 0.7 : 0.0;
                break;

            case "experience":
                score = size >= 2 ? 1.0 : size == 1 ? 0.6 : 0.0;
                break;

            case "projects":
                score = size >= 1 ? 0.7 : 0.0;
                break;

            default:
                score = 0.3;
        }

        confidence.put(section, score);
    }
    return confidence;
}



    private ResumeSection detectSection(String line) {
        for (ResumeSection section : ResumeSection.values()) {
            for (String header : section.getHeaders()) {
                if (line.startsWith(header)) {
                  return section;
              }

            }
        }
        return null;
    }
}
