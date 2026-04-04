package com.example.demo.evaluation.repository;

import com.example.demo.evaluation.domain.SessionTest;
import com.example.demo.evaluation.domain.enums.StatutSession;
import com.example.demo.auth.domain.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionTestRepository extends JpaRepository<SessionTest, Long> {
    
    /**
     * Trouver toutes les sessions d'un utilisateur
     */
    List<SessionTest> findByUtilisateurOrderByDateDebutDesc(Utilisateur utilisateur);
    
    /**
     * Trouver la session en cours pour un utilisateur (la plus récente)
     */
    Optional<SessionTest> findFirstByUtilisateurAndStatutOrderByDateDebutDesc(Utilisateur utilisateur, StatutSession statut);
    
    /**
     * Trouver les sessions terminées d'un utilisateur (les 2 dernières)
     */
    @Query("SELECT s FROM SessionTest s WHERE s.utilisateur = :utilisateur " +
           "AND s.statut = 'TERMINEE' ORDER BY s.dateDebut DESC")
    List<SessionTest> findLastTwoTerminated(@Param("utilisateur") Utilisateur utilisateur);
    
    /**
     * Compter le nombre de sessions complétées pour un utilisateur
     */
    long countByUtilisateurAndStatut(Utilisateur utilisateur, StatutSession statut);
    
    /**
     * Récupérer les 3 dernières sessions d'un utilisateur (pour progression)
     */
    List<SessionTest> findTop3ByUtilisateurOrderByDateDebutDesc(Utilisateur utilisateur);
    
    /**
     * Compter le nombre total de sessions pour un utilisateur
     */
    long countByUtilisateur(Utilisateur utilisateur);
    
    /**
     * Compter le nombre de sessions par statut
     */
    long countByStatut(StatutSession statut);
    
    /**
     * ✅ FIX M2: Trouver les sessions expirées (EN_COURS depuis 3+ heures)
     * Pour SessionCleanupScheduler → fermer et calculer scores
     */
    List<SessionTest> findByStatutAndDateDebutBefore(
        StatutSession statut,
        LocalDateTime dateDebutBefore
    );
}
