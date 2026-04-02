package com.example.demo.recommendation.exception;

/**
 * Exception levée lors d'erreurs d'appel à l'API Gemini
 * ou de parsing de la réponse
 */
public class LLMException extends RuntimeException {

    public LLMException(String message) {
        super(message);
    }

    public LLMException(String message, Throwable cause) {
        super(message, cause);
    }
}
