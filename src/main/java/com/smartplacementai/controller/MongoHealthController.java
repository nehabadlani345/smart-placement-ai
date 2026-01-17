package com.smartplacementai.controller;

import com.smartplacementai.model.mongo.ResumeDocument;
import com.smartplacementai.repository.mongo.ResumeRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mongo")
public class MongoHealthController {

    private final ResumeRepository resumeRepository;

    public MongoHealthController(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    @GetMapping("/health")
    public String mongoHealth() {
        ResumeDocument resume =
                new ResumeDocument(1L, "sample_resume.pdf");

        resumeRepository.save(resume);

        return "MongoDB working";
    }
}
