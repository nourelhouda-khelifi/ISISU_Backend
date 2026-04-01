package com.example.demo.referentiel.infrastructure;

import com.example.demo.referentiel.domain.ModuleFIE;
import com.example.demo.referentiel.domain.enums.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleFIERepository extends JpaRepository<ModuleFIE, Long> {
    
    /**
     * Récupérer tous les modules évaluables
     */
    List<ModuleFIE> findByEvaluableTrue();
    
    /**
     * Récupérer les modules d'un semestre
     */
    List<ModuleFIE> findBySemestre(Semestre semestre);
    
    /**
     * Récupérer les modules évaluables d'un semestre
     */
    List<ModuleFIE> findBySemestreAndEvaluableTrue(Semestre semestre);
    
    /**
     * Récupérer un module par son code
     */
    Optional<ModuleFIE> findByCode(String code);
    
    /**
     * Récupérer les modules d'une UE
     */
    @Query("SELECT m FROM ModuleFIE m WHERE m.uniteEnseignement.id = :ueId ORDER BY m.code")
    List<ModuleFIE> findByUniteEnseignementId(@Param("ueId") Long ueId);
    
    /**
     * Récupérer les modules qui dépendent d'un module donné
     * (i.e., les modules qui ont celui-ci comme prérequis)
     * Utilisé pour calculer les dépendances bloquantes
     */
    @Query("SELECT m FROM ModuleFIE m JOIN m.modulesPrerequisList p WHERE p.id = :moduleId")
    List<ModuleFIE> findDependentModules(@Param("moduleId") Long moduleId);
}
