package com.smartplacementai.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.smartplacementai.model.mongo.CompanyDocument;

public interface CompanyRepository
        extends MongoRepository<CompanyDocument, String> {
}
