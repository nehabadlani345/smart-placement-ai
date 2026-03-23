package com.smartplacementai.repository.mongo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.smartplacementai.model.mongo.StructuredResumeDocument;

public interface StructuredResumeRepository
        extends MongoRepository<StructuredResumeDocument, String> {
                Optional<StructuredResumeDocument> findByRawResumeId(String rawResumeId);
}
