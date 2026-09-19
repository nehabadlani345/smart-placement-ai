package com.smartplacementai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiClient implements AiClient {

    private final RestClient restClient = RestClient.create("https://generativelanguage.googleapis.com");

    @Value("${app.gemini.api-key}")
    private String apiKey;

    @Value("${app.gemini.model:gemini-3.6-flash}")
    private String model;

    @Override
    public String generateJson(String systemPrompt, String userPrompt) {
        String url = "/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of(
                        "text", systemPrompt + "\n\nRespond ONLY with valid JSON. No markdown, no code fences, no preamble."))),
                "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt)))),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        int maxAttempts = 3;
        long backoffMs = 1000;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                Map<String, Object> response = restClient.post()
                        .uri(url)
                        .body(body)
                        .retrieve()
                        .body(Map.class);

                return extractText(response);

            } catch (org.springframework.web.client.HttpServerErrorException.ServiceUnavailable e) {
                if (attempt == maxAttempts) {
                    throw e; // give up after the last attempt — let the caller's existing catch block handle it
                }
                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
                backoffMs *= 2; // 1s, then 2s, then would've been 4s
            }
        }

        throw new IllegalStateException("Unreachable"); // loop always returns or throws above
    }
//    @SuppressWarnings("unchecked")
//    private String extractText(Map<String, Object> response) {
//        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
//        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
//        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
//        return (String) parts.get(0).get("text");
//    }
    
    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        if (response == null) {
            throw new IllegalStateException("Gemini returned an empty response");
        }
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            Object promptFeedback = response.get("promptFeedback");
            throw new IllegalStateException("Gemini returned no candidates. promptFeedback=" + promptFeedback + ", full response=" + response);
        }
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        return (String) parts.get(0).get("text");
    }
}