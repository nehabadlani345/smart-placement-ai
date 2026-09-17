package com.smartplacementai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.smartplacementai.model.mongo.ResumeDocument;
import com.smartplacementai.repository.mongo.ResumeRepository;
import com.smartplacementai.service.ResumeTextExtractor;

@RestController
@RequestMapping("/resume")
public class ResumeUploadController {

    private final ResumeRepository resumeRepository;
    private final ResumeTextExtractor resumeTextExtractor;

    public ResumeUploadController(ResumeRepository resumeRepository,
                                  ResumeTextExtractor resumeTextExtractor) {
        this.resumeRepository = resumeRepository;
        this.resumeTextExtractor = resumeTextExtractor;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        String extractedText =
                resumeTextExtractor.extractText(file);

        ResumeDocument resume =
                new ResumeDocument(userId, file.getOriginalFilename());

        resume.setRawText(extractedText);

        resumeRepository.save(resume);

        return ResponseEntity.ok("Resume uploaded and processed successfully");
    }

    @GetMapping("/test")
public String test() {
    return "resume controller working";
}

}
