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

    @Value("${app.gemini.model:gemini-2.0-flash}")
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

        Map<String, Object> response = restClient.post()
                .uri(url)
                .body(body)
                .retrieve()
                .body(Map.class);

        return extractText(response);
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        return (String) parts.get(0).get("text");
    }
}