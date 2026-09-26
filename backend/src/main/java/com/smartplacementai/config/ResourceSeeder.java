package com.smartplacementai.config;

import com.smartplacementai.model.sql.Resource;
import com.smartplacementai.repository.sql.ResourceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ResourceSeeder implements CommandLineRunner {

    private final ResourceRepository resourceRepository;

    public ResourceSeeder(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public void run(String... args) {
        if (resourceRepository.count() > 0) return; // idempotent — only seed once

        resourceRepository.saveAll(java.util.List.of(
            new Resource("Java Documentation", "Official Java SE API docs.", "https://docs.oracle.com/en/java/javase/21/", "DOC", "Java", "INTERMEDIATE"),
            new Resource("Spring Boot Reference", "Official Spring Boot documentation.", "https://docs.spring.io/spring-boot/index.html", "DOC", "Spring Boot", "INTERMEDIATE"),
            new Resource("Baeldung", "In-depth Java and Spring tutorials.", "https://www.baeldung.com/", "ARTICLE", "Spring Boot", "INTERMEDIATE"),
            new Resource("NeetCode 150", "Curated DSA problem list with video explanations.", "https://neetcode.io/practice", "REPO", "DSA", "INTERMEDIATE"),
            new Resource("LeetCode", "Practice coding problems for interviews.", "https://leetcode.com/", "REPO", "DSA", "BEGINNER"),
            new Resource("System Design Primer", "The most popular open-source system design guide.", "https://github.com/donnemartin/system-design-primer", "REPO", "System Design", "ADVANCED"),
            new Resource("Angular Documentation", "Official Angular framework docs.", "https://angular.dev/", "DOC", "Angular", "INTERMEDIATE"),
            new Resource("MDN Web Docs", "Reference for HTML, CSS, and JavaScript.", "https://developer.mozilla.org/", "DOC", "Web Fundamentals", "BEGINNER"),
            new Resource("Grokking the Coding Interview", "Pattern-based approach to coding interviews.", "https://www.designgurus.io/course/grokking-the-coding-interview", "ARTICLE", "DSA", "INTERMEDIATE"),
            new Resource("freeCodeCamp", "Free full-stack development curriculum.", "https://www.freecodecamp.org/", "VIDEO", "Web Fundamentals", "BEGINNER"),
            new Resource("Java Multithreading Cheat Sheet", "Quick reference for concurrency in Java.", "https://www.baeldung.com/java-concurrency", "CHEATSHEET", "Java", "ADVANCED"),
            new Resource("PostgreSQL Tutorial", "Comprehensive SQL and PostgreSQL guide.", "https://www.postgresqltutorial.com/", "ARTICLE", "Databases", "BEGINNER"),
            new Resource("Spring Security Reference", "Official Spring Security documentation.", "https://docs.spring.io/spring-security/reference/", "DOC", "Spring Boot", "ADVANCED"),
            new Resource("Excalidraw", "Free whiteboard tool for sketching system design diagrams.", "https://excalidraw.com/", "ARTICLE", "System Design", "BEGINNER")
        ));
    }
}