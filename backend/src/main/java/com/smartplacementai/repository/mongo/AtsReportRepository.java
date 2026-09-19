package com.smartplacementai.repository.mongo;

import com.smartplacementai.model.mongo.AtsReportDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface AtsReportRepository extends MongoRepository<AtsReportDocument, String> {
    Optional<AtsReportDocument> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}