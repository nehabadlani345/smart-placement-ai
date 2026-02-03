package com.smartplacementai.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.smartplacementai.model.mongo.StructuredResumeDocument;

public interface StructuredResumeRepository
        extends MongoRepository<StructuredResumeDocument, String> {
}
