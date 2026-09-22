package com.smartplacementai.controller;

import com.smartplacementai.dto.JdAnalyzeRequest;
import com.smartplacementai.dto.JdCompatibilityDto;
import com.smartplacementai.security.SecurityUser;
import com.smartplacementai.service.JdService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jd")
public class JdController {

    private final JdService jdService;

    public JdController(JdService jdService) {
        this.jdService = jdService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<JdCompatibilityDto> analyze(@Valid @RequestBody JdAnalyzeRequest request,
                                                        Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUserId();
        return ResponseEntity.ok(jdService.analyze(userId, request));
    }
    
    @GetMapping("/latest")
    public ResponseEntity<JdCompatibilityDto> latest(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUserId();
        return ResponseEntity.ok(jdService.getLatest(userId));
    }
}