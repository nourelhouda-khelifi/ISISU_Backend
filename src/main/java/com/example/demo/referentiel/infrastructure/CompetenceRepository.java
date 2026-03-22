package com.example.demo.referentiel.infrastructure;

import com.example.demo.referentiel.domain.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetenceRepository extends JpaRepository<Competence, Long> {
    
    /**
     * Récupérer les compétences d'un module, ordonnées par numéro
     */
    @Query("SELECT c FROM Competence c WHERE c.module.id = :moduleId ORDER BY c.numeroOrdre ASC")
    List<Competence> findByModuleIdOrderedByNumero(@Param("moduleId") Long moduleId);
    
    /**
     * Récupérer les compétences d'un module
     */
    List<Competence> findByModuleId(Long moduleId);
}
