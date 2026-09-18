package com.smartplacementai.service;

public interface AiClient {
    String generateJson(String systemPrompt, String userPrompt);
}