package com.example.demo.recommendation.service;

import com.example.demo.recommendation.dto.AnalyseLLM;
import com.example.demo.recommendation.exception.LLMException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service pour communiquer avec l'API Gemini
 * Utilise le SDK officiel google-genai pour simplifier les appels
 */
@Service
@Slf4j
public class GeminiClient {

    private static final String DEFAULT_MODEL = "gemini-2.5-flash";
    
    @Value("${GEMINI_MODEL:gemini-2.5-flash}")
    private String model;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Client client;

    /**
     * Initialiser le client de manière lazy à la première utilisation
     */
    private synchronized Client getClient() {
        if (client == null) {
            try {
                log.info("Initialisation du Gemini Client...");
                this.client = new Client();
                log.info("Gemini Client initialisé avec succès");
            } catch (Exception e) {
                log.error("Erreur lors de l'initialisation du Gemini Client", e);
                throw new RuntimeException("Impossible d'initialiser Gemini Client: " + e.getMessage(), e);
            }
        }
        return client;
    }

    /**
     * Envoie le prompt au LLM Gemini et reçoit une analyse JSON
     *
     * @param prompt Le texte d'entrée (construit par PromptBuilder)
     * @return AnalyseLLM parsée depuis la réponse Gemini
     * @throws LLMException en cas d'erreur d'appel ou de parsing
     */
    public AnalyseLLM analyser(String prompt) {
        log.info("Appel Gemini LLM pour analyse recommendations avec modèle: {}", model);

        try {
            // Récupérer le client (initialisation lazy si nécessaire)
            Client geminiClient = getClient();

            // Appel à l'API Gemini avec le SDK officiel
            GenerateContentResponse response = geminiClient.models.generateContent(model, prompt, null);

            log.debug("Réponse Gemini reçue");

            // Parser la réponse
            return parseResponse(response);

        } catch (Exception e) {
            log.error("Erreur lors de l'appel Gemini", e);
            throw new LLMException("Erreur appel LLM Gemini : " + e.getMessage(), e);
        }
    }

    /**
     * Parse la réponse de Gemini pour extraire le texte
     * puis désérialise ce texte en AnalyseLLM
     *
     * @param response La réponse de Gemini
     * @return AnalyseLLM parsée
     * @throws LLMException en cas d'erreur de parsing
     */
    private AnalyseLLM parseResponse(GenerateContentResponse response) {
        try {
            // Extraire le texte de la réponse
            String textContent = response.text();

            if (textContent == null || textContent.isEmpty()) {
                throw new LLMException("Réponse vide de Gemini");
            }

            // Nettoyer le markdown si Gemini retourne du JSON enrobé dans ```json ... ```
            textContent = cleanMarkdownJson(textContent);

            log.debug("Contenu texte extrait, parsing en AnalyseLLM");

            // Désérialiser le JSON string en AnalyseLLM
            AnalyseLLM analysis = objectMapper.readValue(textContent, AnalyseLLM.class);

            log.info("Analyse LLM créée avec succès");
            return analysis;

        } catch (LLMException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur parsing réponse Gemini", e);
            throw new LLMException("Erreur parsing réponse Gemini : " + e.getMessage(), e);
        }
    }

    /**
     * Nettoie la réponse Gemini si elle contient du markdown (```json...```)
     * Extrait le JSON pur pour le parsing
     *
     * @param content Le texte potentiellement enrobé en markdown
     * @return Le JSON pur
     */
    private String cleanMarkdownJson(String content) {
        // Pattern: ```json\n{...}```  ou ```\n{...}```
        if (content.contains("```")) {
            // Extraire le contenu entre les backticks
            int startIdx = content.indexOf("```");
            int endIdx = content.lastIndexOf("```");

            if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
                // Sauter le premier ``` et éventuellement "json"
                String cleaned = content.substring(startIdx + 3, endIdx).trim();

                // Supprimer le "json" ou autres labels après les backticks
                if (cleaned.startsWith("json")) {
                    cleaned = cleaned.substring(4).trim();
                }

                log.debug("Markdown JSON nettoyé");
                return cleaned;
            }
        }

        return content;
    }
}
