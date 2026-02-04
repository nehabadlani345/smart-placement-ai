package com.smartplacementai.service.matching;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.smartplacementai.exception.InvalidJobException;
import com.smartplacementai.exception.InvalidResumeException;
import com.smartplacementai.exception.JobNotFoundException;
import com.smartplacementai.exception.ResumeNotFoundException;
import com.smartplacementai.model.matching.MatchingResult;
import com.smartplacementai.model.mongo.JobDescriptionDocument;
import com.smartplacementai.model.mongo.ResumeJobMatchDocument;
import com.smartplacementai.model.mongo.StructuredResumeDocument;
import com.smartplacementai.repository.mongo.JobDescriptionRepository;
import com.smartplacementai.repository.mongo.ResumeJobMatchRepository;
import com.smartplacementai.repository.mongo.StructuredResumeRepository;


@Service
public class ResumeJobMatchingService {

    private final StructuredResumeRepository resumeRepository;
    private final JobDescriptionRepository jobRepository;
    private final ResumeJobMatchRepository matchRepository;


    public ResumeJobMatchingService(
        StructuredResumeRepository resumeRepository,
        JobDescriptionRepository jobRepository,
        ResumeJobMatchRepository matchRepository
) {
    this.resumeRepository = resumeRepository;
    this.jobRepository = jobRepository;
    this.matchRepository = matchRepository;
}


    public MatchingResult match(String resumeId, String jobId) {
   
        StructuredResumeDocument resume =
                resumeRepository.findById(resumeId)
                        .orElseThrow(() -> new ResumeNotFoundException(resumeId));

        JobDescriptionDocument job =
                jobRepository.findById(jobId)
                        .orElseThrow(() -> new JobNotFoundException(jobId));

        // ---------------------------
// 2.5️⃣ VALIDATE JOB
// ---------------------------
        if (job.getRequiredSkills() == null || job.getRequiredSkills().isEmpty()) {
        throw new InvalidJobException("Job has no required skills defined");
        }

        if (job.getMinExperience() != null && job.getMinExperience() < 0) {
        throw new InvalidJobException("Job minExperience cannot be negative");
        }

        if (job.getMinExperience() != null &&
        job.getMaxExperience() != null &&
        job.getMaxExperience() < job.getMinExperience()) {

        throw new InvalidJobException(
                "Job maxExperience cannot be less than minExperience"
        );
        }
                

        List<String> resumeSkillList =
                resume.getSections() == null
                         ? Collections.emptyList()
                        : resume.getSections().entrySet().stream()
                                .filter(e -> e.getKey().equalsIgnoreCase("skills"))
                                .findFirst()
                                 .map(Map.Entry::getValue)
                                 .orElse(Collections.emptyList());

         if (resumeSkillList.isEmpty()) {
            throw new InvalidResumeException("Resume has no skills section");
        }

     Set<String> resumeSkills =
        resumeSkillList.stream()
                .flatMap(skill -> Arrays.stream(skill.split("[,\\s]+")))
                .collect(Collectors.collectingAndThen(
                        Collectors.toSet(),
                        SkillNormalizer::normalize
                ));



        Set<String> requiredSkills =
                 job.getRequiredSkills() == null
                        ? Collections.emptySet()
                         : SkillNormalizer.normalize(new HashSet<>(job.getRequiredSkills()));

        Set<String> preferredSkills =
                 job.getPreferredSkills() == null
                         ? Collections.emptySet()
                        : SkillNormalizer.normalize(new HashSet<>(job.getPreferredSkills()));

                        
        List<String> matchedRequired = new ArrayList<>();
        List<String> missingRequired = new ArrayList<>();

        for (String skill : requiredSkills) {
            if (resumeSkills.contains(skill)) {
                matchedRequired.add(skill);
            } else {
                missingRequired.add(skill);
            }
        }


        List<String> matchedPreferred = new ArrayList<>();
        List<String> missingPreferred = new ArrayList<>();

        for (String skill : preferredSkills) {
            if (resumeSkills.contains(skill)) {
                matchedPreferred.add(skill);
            } else {
                missingPreferred.add(skill);
            }
        }

        int requiredSkillScore =
                requiredSkills.isEmpty()
                        ? 0
                        : (int) ((matchedRequired.size() * 50.0) / requiredSkills.size());

        int preferredSkillScore =
                preferredSkills.isEmpty()
                        ? 0
                        : (int) ((matchedPreferred.size() * 20.0) / preferredSkills.size());

        int experienceScore =
                job.getMinExperience() != null &&
                resume.getTotalExperienceYears() >= job.getMinExperience()
                        ? 20
                        : 0;


      //  double avgConfidence =
      //   resume.getConfidence() == null || resume.getConfidence().isEmpty()
      //           ? 0.0
      //           : resume.getConfidence().values()
      //                   .stream()
      //                   .mapToDouble(Double::doubleValue)
      //                   .average()
      //                   .orElse(0.0);

      //   int confidenceScore = (int) (avgConfidence * 10);

        int confidenceScore = 0;


        int atsScore =
                requiredSkillScore
                        + preferredSkillScore
                        + experienceScore
                        + confidenceScore;


        Map<String, Integer> scoreBreakdown = new LinkedHashMap<>();
        scoreBreakdown.put("requiredSkillScore", requiredSkillScore);
        scoreBreakdown.put("preferredSkillScore", preferredSkillScore);
        scoreBreakdown.put("experienceScore", experienceScore);
        scoreBreakdown.put("confidenceScore", confidenceScore);


        MatchingResult result = new MatchingResult();
        result.setResumeId(resumeId);
        result.setJobId(jobId);
        result.setAtsScore(atsScore);
        result.setMatchedRequiredSkills(matchedRequired);
        result.setMissingRequiredSkills(missingRequired);
        result.setMatchedPreferredSkills(matchedPreferred);
        result.setMissingPreferredSkills(missingPreferred);
        result.setScoreBreakdown(scoreBreakdown);

        ResumeJobMatchDocument matchDocument = new ResumeJobMatchDocument();
        matchDocument.setResumeId(resumeId);
        matchDocument.setJobId(jobId);
        matchDocument.setAtsScore(atsScore);
        matchDocument.setMatchedRequiredSkills(matchedRequired);
        matchDocument.setMissingRequiredSkills(missingRequired);
        matchDocument.setMatchedPreferredSkills(matchedPreferred);
        matchDocument.setMissingPreferredSkills(missingPreferred);
        matchDocument.setScoreBreakdown(scoreBreakdown);
        matchDocument.setCreatedAt(LocalDateTime.now());

        matchRepository.save(matchDocument);


        return result;

    
    }
}

