package com.example.demo.questions.repository;

import com.example.demo.questions.domain.Question;
import com.example.demo.questions.domain.enums.NiveauDifficulte;
import com.example.demo.questions.domain.enums.TypeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Trouver toutes les questions actives
     */
    List<Question> findByActifTrue();

    /**
     * Trouver les questions par type
     */
    List<Question> findByType(TypeQuestion type);

    /**
     * Trouver les questions par niveau de difficulté
     */
    List<Question> findByDifficulte(NiveauDifficulte difficulte);

    /**
     * Trouver les questions par type et difficulté
     */
    List<Question> findByTypeAndDifficulte(TypeQuestion type, NiveauDifficulte difficulte);

    /**
     * Trouver les questions actives d'un type et difficulté donnés
     */
    List<Question> findByActifTrueAndTypeAndDifficulte(
        TypeQuestion type,
        NiveauDifficulte difficulte
    );

    /**
     * Trouver les questions liées à une compétence donnée
     * (utilisé pour sélectionner les questions d'un test)
     */
    @Query("""
        SELECT DISTINCT q FROM Question q
        JOIN q.competences c
        WHERE c.id = :competenceId
        AND q.actif = true
        ORDER BY q.id DESC
        """)
    List<Question> findByCompetenceId(@Param("competenceId") Long competenceId);

    /**
     * Trouver les questions liées à plusieurs compétences
     */
    @Query("""
        SELECT DISTINCT q FROM Question q
        JOIN q.competences c
        WHERE c.id IN :competenceIds
        AND q.actif = true
        """)
    List<Question> findByCompetenceIds(@Param("competenceIds") List<Long> competenceIds);

    /**
     * Trouver les questions d'une difficulté donnée pour des compétences
     */
    @Query("""
        SELECT DISTINCT q FROM Question q
        JOIN q.competences c
        WHERE c.id IN :competenceIds
        AND q.actif = true
        AND q.difficulte = :difficulte
        """)
    List<Question> findByCompetencesAndDifficulte(
        @Param("competenceIds") List<Long> competenceIds,
        @Param("difficulte") NiveauDifficulte difficulte
    );
}
