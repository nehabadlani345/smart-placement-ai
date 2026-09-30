package com.smartplacementai.service;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.http.client.SimpleClientHttpRequestFactory;


@Service
public class GeminiClient implements AiClient {

    @Value("${app.gemini.api-key}")
    private String apiKey;

    @Value("${app.gemini.model:gemini-3.6-flash}")
    private String model;

    @Value("${app.gemini.connect-timeout-seconds:10}")
    private int connectTimeoutSeconds;

    @Value("${app.gemini.read-timeout-seconds:60}")
    private int readTimeoutSeconds;

    private RestClient restClient;

    @PostConstruct
    private void init() {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(
                Duration.ofSeconds(connectTimeoutSeconds)
        );

        factory.setReadTimeout(
                Duration.ofSeconds(readTimeoutSeconds)
        );

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();}

    @Override
    public String generateJson(String systemPrompt, String userPrompt) {
        String url = "/v1beta/models/" + model + ":generateContent";

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
                        .header("x-goog-api-key", apiKey)
                        .body(body)
                        .retrieve()
                        .body(Map.class);

                return extractText(response);

            } catch (org.springframework.web.client.HttpServerErrorException.ServiceUnavailable e) {
                if (attempt == maxAttempts) {
                    throw e; // give up after the last attempt — let the caller's existing catch block handle it
                }
                sleepOrRethrow(backoffMs, e);
                backoffMs *= 2; // 1s, then 2s

            } catch (org.springframework.web.client.ResourceAccessException e) {
                // Covers connect/read timeouts (wraps SocketTimeoutException) — just as retriable as a 503.
                boolean isTimeout = e.getCause() instanceof SocketTimeoutException;
                if (!isTimeout || attempt == maxAttempts) {
                    throw e;
                }
                sleepOrRethrow(backoffMs, e);
                backoffMs *= 2;
            }
        }

        throw new IllegalStateException("Unreachable"); // loop always returns or throws above
    }

    private void sleepOrRethrow(long backoffMs, RuntimeException original) {
        try {
            Thread.sleep(backoffMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw original;
        }
    }

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
