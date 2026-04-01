package com.example.demo.evaluation.repository;

import com.example.demo.evaluation.domain.QuestionSession;
import com.example.demo.evaluation.domain.SessionTest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionSessionRepository extends JpaRepository<QuestionSession, Long> {
    
    /**
     * Trouver toutes les questions d'une session (ordonnées)
     */
    List<QuestionSession> findBySessionOrderByOrdre(SessionTest session);
    
    /**
     * Compter les questions non répondues
     */
    long countBySessionAndEstRepondueFalse(SessionTest session);
}

