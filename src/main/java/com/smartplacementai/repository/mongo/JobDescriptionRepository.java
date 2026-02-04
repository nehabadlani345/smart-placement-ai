package com.smartplacementai.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.smartplacementai.model.mongo.JobDescriptionDocument;

public interface JobDescriptionRepository
        extends MongoRepository<JobDescriptionDocument, String> {
}
