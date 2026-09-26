package com.smartplacementai.repository.sql;

import com.smartplacementai.model.sql.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByTopicIgnoreCase(String topic);
    List<Resource> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String t, String d);
}