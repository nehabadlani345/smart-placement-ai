package com.smartplacementai.service;

import com.smartplacementai.dto.ResourceDto;
import com.smartplacementai.model.sql.Bookmark;
import com.smartplacementai.model.sql.Resource;
import com.smartplacementai.repository.sql.BookmarkRepository;
import com.smartplacementai.repository.sql.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final BookmarkRepository bookmarkRepository;

    public ResourceService(ResourceRepository resourceRepository, BookmarkRepository bookmarkRepository) {
        this.resourceRepository = resourceRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    public List<ResourceDto> list(Long userId, String topic, String search, boolean bookmarkedOnly) {
        List<Resource> resources;
        if (search != null && !search.isBlank()) {
            resources = resourceRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
        } else if (topic != null && !topic.isBlank()) {
            resources = resourceRepository.findByTopicIgnoreCase(topic);
        } else {
            resources = resourceRepository.findAll();
        }

        Set<Long> bookmarkedIds = bookmarkRepository.findByUserId(userId).stream()
                .map(Bookmark::getResourceId).collect(Collectors.toSet());

        return resources.stream()
                .filter(r -> !bookmarkedOnly || bookmarkedIds.contains(r.getId()))
                .map(r -> new ResourceDto(r.getId(), r.getTitle(), r.getDescription(), r.getUrl(),
                        r.getResourceType(), r.getTopic(), r.getDifficulty(), bookmarkedIds.contains(r.getId())))
                .toList();
    }

    public void toggleBookmark(Long userId, Long resourceId) {
        bookmarkRepository.findByUserIdAndResourceId(userId, resourceId)
                .ifPresentOrElse(
                        existing -> bookmarkRepository.deleteByUserIdAndResourceId(userId, resourceId),
                        () -> bookmarkRepository.save(new Bookmark(userId, resourceId))
                );
    }
}