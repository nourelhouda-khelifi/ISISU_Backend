package com.example.demo.recommendation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour le module Recommendation et LLM
 */
@Configuration
public class RecommendationConfig {

    /**
     * Fournit un bean ObjectMapper pour la sérialisation/désérialisation JSON
     * Utilisé par GeminiClient et autres composants
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
