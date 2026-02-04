package com.smartplacementai.repository.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.smartplacementai.model.mongo.ResumeJobMatchDocument;

public interface ResumeJobMatchRepository
        extends MongoRepository<ResumeJobMatchDocument, String> {

    List<ResumeJobMatchDocument> findByResumeId(String resumeId);
}

