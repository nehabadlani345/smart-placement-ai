package com.smartplacementai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartplacementai.repository.sql.userRepository;

@RestController
public class HealthController {

    private final userRepository userRepository;

    public HealthController(userRepository userRepository) {
        this.userRepository = userRepository;
    }

   @GetMapping("/health")
public String health() {
    // User user = new User("Test User", "test@example.com");
    // userRepository.save(user);
    return "Backend + PostgreSQL working";
}

}

