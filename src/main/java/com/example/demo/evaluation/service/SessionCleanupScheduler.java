package com.example.demo.evaluation.service;

import com.example.demo.evaluation.domain.SessionTest;
import com.example.demo.evaluation.domain.enums.StatutSession;
import com.example.demo.evaluation.repository.SessionTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SessionCleanupScheduler
 * 
 * ✅ FIX M2: Gérer les sessions EN_COURS expirées
 * 
 * Logique pédagogique:
 * - Sessions actives depuis 3+ heures = timeout
 * - Calculer scores même pour sessions expirées
 * - Libérer ressources
 * 
 * Exécution: Toutes les 5 minutes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SessionCleanupScheduler {
    
    private final SessionTestRepository sessionRepository;
    private final SessionTestService sessionTestService;
    
    /**
     * Fermer les sessions expirées (> 3 heures)
     * 
     * Exécuté toutes les 5 minutes (300 secondes)
     */
    @Scheduled(fixedDelay = 300000, initialDelay = 60000)
    public void closeExpiredSessions() {
        log.debug("🧹 SessionCleanupScheduler: Vérifier sessions expirées...");
        
        // Sessions EN_COURS depuis 3+ heures
        LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);
        
        List<SessionTest> expiredSessions = sessionRepository
            .findByStatutAndDateDebutBefore(
                StatutSession.EN_COURS, 
                threeHoursAgo
            );
        
        if (expiredSessions.isEmpty()) {
            log.debug("✅ Aucune session expirée à nettoyer");
            return;
        }
        
        log.warn("⏰ {} sessions expirées détectées. Nettoyage...", expiredSessions.size());
        
        for (SessionTest session : expiredSessions) {
            try {
                log.info("Fermeture session {} (datetime: {}, utilisateur: {})", 
                    session.getId(), 
                    session.getDateDebut(),
                    session.getUtilisateur().getEmail());
                
                // Fermer la session avec statut TIMEOUT
                // ✅ Pédagogie: Calculer scores même pour session expirée
                sessionTestService.terminateSession(
                    session, 
                    StatutSession.TIMEOUT, 
                    "auto-cleanup-timeout"
                );
                
                log.info("✅ Session {} fermée avec scores calculés", session.getId());
                
            } catch (Exception e) {
                log.error("❌ Erreur fermeture session {}: {}", 
                    session.getId(), e.getMessage(), e);
                // Continuer avec les autres sessions
            }
        }
        
        log.info("🧹 Cleanup terminé: {} sessions nettoyées", expiredSessions.size());
    }
    
    /**
     * Récupérer list sessions expirées (pour tests/monitoringn)
     */
    public List<SessionTest> getExpiredSessions() {
        LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);
        return sessionRepository.findByStatutAndDateDebutBefore(
            StatutSession.EN_COURS, 
            threeHoursAgo
        );
    }
}
