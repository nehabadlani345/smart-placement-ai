package com.smartplacementai.repository.mongo;

import com.smartplacementai.model.mongo.ResumeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends MongoRepository<ResumeDocument, String> {
    List<ResumeDocument> findByUserId(Long userId);
    Optional<ResumeDocument> findByUserIdAndActiveTrue(Long userId);
    List<ResumeDocument> findByUserIdOrderByVersionDesc(Long userId);
}