package com.smartplacementai.repository.mongo;

import com.smartplacementai.model.mongo.RoadmapDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapRepository extends MongoRepository<RoadmapDocument, String> {
    Optional<RoadmapDocument> findByUserIdAndActiveTrue(Long userId);
    List<RoadmapDocument> findByUserIdOrderByVersionDesc(Long userId);
}