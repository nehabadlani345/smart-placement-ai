package com.smartplacementai.controller;

import com.smartplacementai.dto.RoadmapDto;
import com.smartplacementai.dto.RoadmapGenerateRequest;
import com.smartplacementai.security.SecurityUser;
import com.smartplacementai.service.RoadmapService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roadmap")
public class RoadmapController {

    private final RoadmapService roadmapService;

    public RoadmapController(RoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @PostMapping("/generate")
    public ResponseEntity<RoadmapDto> generate(@Valid @RequestBody RoadmapGenerateRequest request,
                                                Authentication authentication) {
        return ResponseEntity.ok(roadmapService.generate(userId(authentication), request));
    }

    @GetMapping("/current")
    public ResponseEntity<RoadmapDto> current(Authentication authentication) {
        return ResponseEntity.ok(roadmapService.getCurrent(userId(authentication)));
    }

    @PatchMapping("/tasks/{taskId}/complete")
    public ResponseEntity<RoadmapDto> completeTask(@PathVariable String taskId,
                                                    @RequestParam(defaultValue = "true") boolean completed,
                                                    Authentication authentication) {
        return ResponseEntity.ok(roadmapService.completeTask(userId(authentication), taskId, completed));
    }

    @GetMapping("/planner")
    public ResponseEntity<RoadmapDto.PhaseDto> planner(@RequestParam int period, Authentication authentication) {
        return ResponseEntity.ok(roadmapService.getPlannerForPeriod(userId(authentication), period));
    }

    private Long userId(Authentication authentication) {
        return ((SecurityUser) authentication.getPrincipal()).getUserId();
    }
}