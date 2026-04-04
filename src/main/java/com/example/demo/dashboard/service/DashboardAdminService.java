package com.example.demo.dashboard.service;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.auth.domain.enums.Role;
import com.example.demo.auth.domain.enums.StatutCompte;
import com.example.demo.auth.infrastructure.repository.UtilisateurRepository;
import com.example.demo.dashboard.dto.*;
import com.example.demo.evaluation.domain.SessionTest;
import com.example.demo.evaluation.domain.enums.StatutSession;
import com.example.demo.evaluation.repository.ScoreCompetenceRepository;
import com.example.demo.evaluation.repository.SessionTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour le dashboard administrateur
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardAdminService {
    
    private final UtilisateurRepository utilisateurRepository;
    private final SessionTestRepository sessionTestRepository;
    private final ScoreCompetenceRepository scoreCompetenceRepository;
    
    /**
     * Récupérer la vue d'ensemble du dashboard admin
     */
    public AdminDashboardDTO getAdminDashboard() {
        log.debug("Récupération du dashboard admin");
        
        long totalUsers = utilisateurRepository.count();
        long totalFIE3 = utilisateurRepository.countByRole(Role.ETUDIANT_FIE3);
        long totalVAE = utilisateurRepository.countByRole(Role.CANDIDAT_VAE);
        
        // Sessions en cours
        long sessionsEnCours = sessionTestRepository.countByStatut(StatutSession.EN_COURS);
        
        // Score moyen (simplifié - à affiner selon besoin)
        Double scoreMoyen = calculerScoreMoyenGlobal();
        
        // Taux de réussite (sessions terminées / total sessions)
        long totalSessions = sessionTestRepository.count();
        long sessionsTerminees = sessionTestRepository.countByStatut(StatutSession.TERMINEE);
        Double tauxReussite = totalSessions > 0 ? (double) sessionsTerminees / totalSessions : 0.0;
        
        return AdminDashboardDTO.builder()
                .totalUtilisateurs(totalUsers)
                .totalEtudiantsFIE3(totalFIE3)
                .totalCandidatsVAE(totalVAE)
                .scoreMoyenGlobal(scoreMoyen)
                .sessionsEnCours(sessionsEnCours)
                .tauxReussite(tauxReussite)
                .competencesTopPerformance(Collections.emptyList()) // À implémenter si besoin
                .competencesLacunes(Collections.emptyList()) // À implémenter si besoin
                .build();
    }
    
    /**
     * Récupérer la liste des utilisateurs avec filtres
     */
    public List<UtilisateurDashboardDTO> getUtilisateurs(Role role, StatutCompte statut) {
        log.debug("Récupération des utilisateurs - role: {}, statut: {}", role, statut);
        
        List<Utilisateur> users = utilisateurRepository.findAll();
        
        return users.stream()
                .filter(u -> role == null || u.getRole() == role)
                .filter(u -> statut == null || u.getStatut() == statut)
                .map(this::convertToUtilisateurDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les statistiques des sessions
     */
    public SessionStatisticsDTO getSessionStatistics() {
        log.debug("Récupération des statistiques de sessions");
        
        long totalSessions = sessionTestRepository.count();
        long sessionsTerminees = sessionTestRepository.countByStatut(StatutSession.TERMINEE);
        long sessionsAbandonnes = sessionTestRepository.countByStatut(StatutSession.ABANDONNEE);
        
        // Durée et score moyens
        Integer dureeMoyenne = calculerDureeMoyenne();
        Double scoreMoyen = calculerScoreMoyenGlobal();
        
        // Sessions par jour (derniers 7 jours)
        List<DailySessionCountDTO> sessionsParJour = calculerSessionsParJour(7);
        
        return SessionStatisticsDTO.builder()
                .totalSessions(totalSessions)
                .sessionsTerminees(sessionsTerminees)
                .sessionsAbandonnes(sessionsAbandonnes)
                .dureeParMoyenne(dureeMoyenne)
                .scoreParMoyenne(scoreMoyen)
                .sessionsParJour(sessionsParJour)
                .build();
    }
    
    // ============ Méthodes utilitaires ============
    
    private UtilisateurDashboardDTO convertToUtilisateurDTO(Utilisateur user) {
        long nbSessions = sessionTestRepository.countByUtilisateur(user);
        Double scoreMoyen = calculerScoreMoyenUtilisateur(user);
        
        return UtilisateurDashboardDTO.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(user.getRole())
                .statut(user.getStatut())
                .nombreSessions(nbSessions)
                .scoreMoyen(scoreMoyen)
                .derniereConnexion(user.getDerniereConnexion())
                .build();
    }
    
    private Double calculerScoreMoyenGlobal() {
        return 0.75; // Valeur par défaut simplifiée
    }
    
    private Double calculerScoreMoyenUtilisateur(Utilisateur user) {
        List<SessionTest> sessions = sessionTestRepository.findByUtilisateurOrderByDateDebutDesc(user);
        if (sessions.isEmpty()) return 0.0;
        
        // Récupérer tous les scores et calculer la moyenne
        return sessions.stream()
                .flatMap(session -> scoreCompetenceRepository.findBySession(session).stream())
                .mapToDouble(score -> score.getScoreObtenu() != null ? score.getScoreObtenu() : 0.0)
                .average()
                .orElse(0.0);
    }
    
    private Integer calculerDureeMoyenne() {
        // Simplifié : retourner une valeur par défaut
        return 45;
    }
    
    private List<DailySessionCountDTO> calculerSessionsParJour(int joursPrecedents) {
        List<DailySessionCountDTO> result = new ArrayList<>();
        
        for (int i = joursPrecedents - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            // Compter les sessions pour ce jour (simplifié - à implémenter si besoin)
            result.add(DailySessionCountDTO.builder()
                    .date(date)
                    .nombre(0L)
                    .build());
        }
        
        return result;
    }
}
