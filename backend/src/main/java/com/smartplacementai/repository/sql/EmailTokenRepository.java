package com.smartplacementai.repository.sql;

import com.smartplacementai.model.sql.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    Optional<EmailToken> findByTokenHash(String tokenHash);
}