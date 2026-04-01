package com.example.demo.evaluation.presentation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handler global des exceptions de l'évaluation
 */
@ControllerAdvice
@Slf4j
public class EvaluationExceptionHandler {

    /**
     * Exception: Session non trouvée
     */
    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSessionNotFound(
            SessionNotFoundException ex, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Session Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Exception: Session expirée
     */
    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleSessionExpired(
            SessionExpiredException ex, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.GONE.value());
        body.put("error", "Session Expired");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.GONE);
    }

    /**
     * Exception: Réponse invalide
     */
    @ExceptionHandler(InvalidAnswerException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAnswer(
            InvalidAnswerException ex, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Invalid Answer");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception: Accès non autorisé à une session
     */
    @ExceptionHandler(UnauthorizedSessionAccessException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedAccess(
            UnauthorizedSessionAccessException ex, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Unauthorized Access");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    /**
     * Exception générique non gérée
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        log.error("Erreur non gérée dans évaluation", ex);
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * Exception: Session non trouvée
 */
class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(Long sessionId) {
        super("Session avec ID " + sessionId + " non trouvée");
    }
    
    public SessionNotFoundException(String message) {
        super(message);
    }
}

/**
 * Exception: Session expirée (timeout 30 min)
 */
class SessionExpiredException extends RuntimeException {
    public SessionExpiredException(Long sessionId) {
        super("Session " + sessionId + " expirée (timeout 30 minutes)");
    }
}

/**
 * Exception: Réponse invalide
 */
class InvalidAnswerException extends RuntimeException {
    public InvalidAnswerException(String message) {
        super(message);
    }
}

/**
 * Exception: Accès non autorisé
 */
class UnauthorizedSessionAccessException extends RuntimeException {
    public UnauthorizedSessionAccessException(String message) {
        super(message);
    }
}
