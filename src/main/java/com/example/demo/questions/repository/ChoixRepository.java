package com.example.demo.questions.repository;

import com.example.demo.questions.domain.Choix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChoixRepository extends JpaRepository<Choix, Long> {

    /**
     * Trouver tous les choix d'une question
     * Ordonnés par numéro d'ordre
     */
    List<Choix> findByQuestionIdOrderByOrdre(Long questionId);

    /**
     * Trouver les choix corrects d'une question
     */
    List<Choix> findByQuestionIdAndEstCorrectTrue(Long questionId);

    /**
     * Trouver les choix incorrects d'une question
     */
    List<Choix> findByQuestionIdAndEstCorrectFalse(Long questionId);

    /**
     * Compter le nombre de choix corrects d'une question
     */
    long countByQuestionIdAndEstCorrectTrue(Long questionId);

    /**
     * Trouver un choix spécifique correct (pour vérifier la réponse)
     */
    @Query("""
        SELECT c FROM Choix c
        WHERE c.question.id = :questionId
        AND c.estCorrect = true
        AND LOWER(c.contenu) = LOWER(:contenu)
        """)
    List<Choix> findCorrectChoixByContenus(
        @Param("questionId") Long questionId,
        @Param("contenu") String contenu
    );
}
