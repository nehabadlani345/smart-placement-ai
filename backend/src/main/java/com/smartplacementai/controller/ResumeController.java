package com.smartplacementai.controller;

import com.smartplacementai.dto.ResumeSummaryDto;
import com.smartplacementai.exception.InvalidResumeException;
import com.smartplacementai.exception.ResumeNotFoundException;
import com.smartplacementai.model.mongo.ResumeDocument;
import com.smartplacementai.repository.mongo.ResumeRepository;
import com.smartplacementai.security.SecurityUser;
import com.smartplacementai.service.FileValidationUtil;
import com.smartplacementai.service.ResumeStorageService;
import com.smartplacementai.service.ResumeTextExtractor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {

    private final ResumeRepository resumeRepository;
    private final ResumeTextExtractor resumeTextExtractor;
    private final ResumeStorageService storageService;
    private final FileValidationUtil fileValidationUtil;

    public ResumeController(ResumeRepository resumeRepository,
                             ResumeTextExtractor resumeTextExtractor,
                             ResumeStorageService storageService,
                             FileValidationUtil fileValidationUtil) {
        this.resumeRepository = resumeRepository;
        this.resumeTextExtractor = resumeTextExtractor;
        this.storageService = storageService;
        this.fileValidationUtil = fileValidationUtil;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResumeSummaryDto> upload(@RequestParam("file") MultipartFile file,
                                                     Authentication authentication) throws IOException {
        Long userId = currentUserId(authentication);

        try {
            fileValidationUtil.validate(file);
        } catch (IllegalArgumentException ex) {
            throw new InvalidResumeException(ex.getMessage());
        }

        resumeRepository.findByUserIdAndActiveTrue(userId)
                .ifPresent(prev -> {
                    prev.setActive(false);
                    resumeRepository.save(prev);
                });

        int nextVersion = resumeRepository.findByUserIdOrderByVersionDesc(userId)
                .stream().findFirst().map(r -> r.getVersion() == null ? 1 : r.getVersion() + 1).orElse(1);

        String storageKey = storageService.store(file, userId);
        String extractedText = resumeTextExtractor.extractText(file);

        ResumeDocument resume = new ResumeDocument(userId, file.getOriginalFilename());
        resume.setRawText(extractedText);
        resume.setStorageKey(storageKey);
        resume.setFileSizeBytes(file.getSize());
        resume.setVersion(nextVersion);
        resume.setActive(true);

        ResumeDocument saved = resumeRepository.save(resume);
        return ResponseEntity.ok(toDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<ResumeSummaryDto>> list(Authentication authentication) {
        Long userId = currentUserId(authentication);
        List<ResumeSummaryDto> resumes = resumeRepository.findByUserIdOrderByVersionDesc(userId)
                .stream().map(this::toDto).toList();
        return ResponseEntity.ok(resumes);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id,
                                                          Authentication authentication) throws IOException {
        ResumeDocument resume = getOwnedResume(id, authentication);
        InputStreamResource resource = new InputStreamResource(storageService.retrieve(resume.getStorageKey()));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resume.getOriginalFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) throws IOException {
        ResumeDocument resume = getOwnedResume(id, authentication);
        storageService.delete(resume.getStorageKey());
        resumeRepository.delete(resume);
        return ResponseEntity.noContent().build();
    }

    private ResumeDocument getOwnedResume(String id, Authentication authentication) {
        Long userId = currentUserId(authentication);
        ResumeDocument resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResumeNotFoundException("Resume not found");
        }
        return resume;
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new org.springframework.security.access.AccessDeniedException("Not authenticated");
        }
        return securityUser.getUserId();
    }

    private ResumeSummaryDto toDto(ResumeDocument r) {
        return new ResumeSummaryDto(r.getId(), r.getOriginalFileName(), r.getVersion(),
                r.isActive(), r.getFileSizeBytes(), r.getUploadedAt());
    }
}