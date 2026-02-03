package com.smartplacementai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartplacementai.model.mongo.StructuredResumeDocument;
import com.smartplacementai.service.ResumeParserService;

@RestController
@RequestMapping("/resume")
public class ResumeStructureController {

    private final ResumeParserService resumeParserService;

    public ResumeStructureController(ResumeParserService resumeParserService) {
        this.resumeParserService = resumeParserService;
    }

    @PostMapping("/structure/{resumeId}")
    public ResponseEntity<StructuredResumeDocument> structureResume(
            @PathVariable String resumeId
    ) {
        StructuredResumeDocument structured =
                resumeParserService.parseAndStructure(resumeId);

        return ResponseEntity.ok(structured);
    }
}
