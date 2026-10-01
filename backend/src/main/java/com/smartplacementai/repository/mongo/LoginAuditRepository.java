package com.smartplacementai.repository.mongo;

import com.smartplacementai.model.mongo.LoginAuditDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoginAuditRepository extends MongoRepository<LoginAuditDocument, String> {
}