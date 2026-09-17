package com.smartplacementai.repository.mongo;

import com.smartplacementai.model.mongo.ResumeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResumeRepository 
        extends MongoRepository<ResumeDocument, String> {

    List<ResumeDocument> findByUserId(Long userId);
}
