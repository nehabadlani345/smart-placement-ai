package com.smartplacementai.repository.mongo;

import com.smartplacementai.model.mongo.JdReportDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface JdReportRepository extends MongoRepository<JdReportDocument, String> {
    Optional<JdReportDocument> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}