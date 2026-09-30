package com.smartplacementai.repository.mongo;

import com.smartplacementai.model.mongo.AiExecutionLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AiExecutionLogRepository extends MongoRepository<AiExecutionLogDocument, String> {
}
