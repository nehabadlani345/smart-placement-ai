package com.smartplacementai.repository.sql;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartplacementai.model.sql.User;

public interface userRepository extends JpaRepository<User, Long> {
}
