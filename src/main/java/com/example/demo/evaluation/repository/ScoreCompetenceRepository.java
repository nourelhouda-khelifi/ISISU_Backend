package com.example.demo.evaluation.repository;

import com.example.demo.evaluation.domain.ScoreCompetence;
import com.example.demo.evaluation.domain.SessionTest;
import com.example.demo.referentiel.domain.Competence;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreCompetenceRepository extends JpaRepository<ScoreCompetence, Long> {
    
    /**
     * Trouver tous les scores d'une session
     */
    List<ScoreCompetence> findBySessionOrderByCompetenceId(SessionTest session);
    
    /**
     * Trouver le score pour une compétence dans une session
     */
    Optional<ScoreCompetence> findBySessionAndCompetence(
        SessionTest session, 
        Competence competence
    );
    
    /**
     * Trouver tous les scores d'une session (alias pour findBySessionOrderByCompetenceId)
     */
    List<ScoreCompetence> findBySession(SessionTest session);
}
