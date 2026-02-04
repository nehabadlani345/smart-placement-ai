package com.smartplacementai.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartplacementai.model.mongo.JobDescriptionDocument;
import com.smartplacementai.repository.mongo.JobDescriptionRepository;

@RestController
@RequestMapping("/job")
public class JobDescriptionController {

    private final JobDescriptionRepository jobRepository;

    public JobDescriptionController(JobDescriptionRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<JobDescriptionDocument> createJob(
            @RequestBody JobDescriptionDocument job) {

        job.setCreatedAt(LocalDateTime.now());

        JobDescriptionDocument saved =
                jobRepository.save(job);

        return ResponseEntity.ok(saved);
    }
}
