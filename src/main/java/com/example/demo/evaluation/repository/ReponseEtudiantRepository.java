package com.example.demo.evaluation.repository;

import com.example.demo.evaluation.domain.ReponseEtudiant;
import com.example.demo.evaluation.domain.SessionTest;
import com.example.demo.evaluation.domain.QuestionSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReponseEtudiantRepository extends JpaRepository<ReponseEtudiant, Long> {
    
    /**
     * Trouver la réponse pour une question dans une session
     */
    Optional<ReponseEtudiant> findBySessionAndQuestionSession(
        SessionTest session, 
        QuestionSession questionSession
    );
    
    /**
     * Trouver toutes les réponses d'une session
     */
    List<ReponseEtudiant> findBySessionOrderByDateReponse(SessionTest session);
}
