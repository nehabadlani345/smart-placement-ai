package com.smartplacementai.controller;

import com.smartplacementai.dto.ResourceDto;
import com.smartplacementai.security.CurrentUser;
import com.smartplacementai.service.ResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final CurrentUser currentUser;

    public ResourceController(ResourceService resourceService, CurrentUser currentUser) {
        this.resourceService = resourceService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public ResponseEntity<List<ResourceDto>> list(@RequestParam(required = false) String topic,
                                                    @RequestParam(required = false) String search,
                                                    @RequestParam(defaultValue = "false") boolean bookmarkedOnly,
                                                    Authentication authentication) {
        Long userId = currentUser.id(authentication);
        return ResponseEntity.ok(resourceService.list(userId, topic, search, bookmarkedOnly));
    }

    @PostMapping("/{id}/bookmark")
    public ResponseEntity<Void> toggleBookmark(@PathVariable Long id, Authentication authentication) {
        Long userId = currentUser.id(authentication);
        resourceService.toggleBookmark(userId, id);
        return ResponseEntity.noContent().build();
    }
}