package com.smartplacementai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartplacementai.repository.sql.UserRepository;

@RestController
public class HealthController {

    private final UserRepository UserRepository;

    public HealthController(UserRepository userRepository) {
        this.UserRepository = userRepository;
    }

   @GetMapping("/health")
public String health() {
    // User user = new User("Test User", "test@example.com");
    // userRepository.save(user);
    return "Backend + PostgreSQL working";
}

}

