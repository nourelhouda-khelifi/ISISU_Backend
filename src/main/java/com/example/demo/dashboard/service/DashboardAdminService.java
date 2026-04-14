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
    
    /**
     * Récupérer l'historique complet d'un étudiant (sessions + scores)
     */
    public AdminHistoriqueEtudiantDTO getHistoriqueEtudiant(Long idEtudiant) {
        log.debug("Récupération de l'historique de l'étudiant {}", idEtudiant);
        
        Utilisateur etudiant = utilisateurRepository.findById(idEtudiant)
                .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé avec l'ID: " + idEtudiant));
        
        List<SessionTest> sessions = sessionTestRepository.findByUtilisateurOrderByDateDebutDesc(etudiant);
        
        // Convertir chaque session en DTO
        List<AdminHistoriqueEtudiantDTO.SessionHistoriqueDTO> sessionsDTO = sessions.stream()
                .map(session -> convertSessionToHistoriqueDTO(session))
                .collect(Collectors.toList());
        
        // Calculer stats globales
        long sessionsTerminees = sessions.stream()
                .filter(s -> s.getStatut() == StatutSession.TERMINEE)
                .count();
        
        Double scoreGlobalMoyen = sessions.stream()
                .filter(s -> s.getStatut() == StatutSession.TERMINEE)
                .flatMap(session -> scoreCompetenceRepository.findBySession(session).stream())
                .mapToDouble(score -> score.getScoreObtenu() != null ? score.getScoreObtenu() : 0.0)
                .average()
                .orElse(0.0);
        
        return AdminHistoriqueEtudiantDTO.builder()
                .utilisateurId(etudiant.getId())
                .email(etudiant.getEmail())
                .nom(etudiant.getNom())
                .prenom(etudiant.getPrenom())
                .totalSessions((long) sessions.size())
                .sessionsTerminees(sessionsTerminees)
                .scoreGlobalMoyen(Math.round(scoreGlobalMoyen * 100.0) / 100.0)
                .sessions(sessionsDTO)
                .build();
    }
    
    /**
     * Convertir une SessionTest en SessionHistoriqueDTO
     */
    private AdminHistoriqueEtudiantDTO.SessionHistoriqueDTO convertSessionToHistoriqueDTO(SessionTest session) {
        List<AdminHistoriqueEtudiantDTO.CompetenceScoreDTO> scoresCompetences = 
            scoreCompetenceRepository.findBySession(session).stream()
                .map(score -> AdminHistoriqueEtudiantDTO.CompetenceScoreDTO.builder()
                        .competenceId(score.getCompetence().getId())
                        .nom(score.getCompetence().getIntitule())
                        .scoreObtenu(score.getScoreObtenu())
                        .niveauAtteint(score.getNiveauAtteint() != null ? score.getNiveauAtteint().toString() : "N/A")
                        .statut(score.getStatut() != null ? score.getStatut().toString() : "N/A")
                        .nbBonnesReponses(score.getNbBonnesReponses())
                        .nbQuestions(score.getNbQuestions())
                        .build())
                .collect(Collectors.toList());
        
        // Calculer durée en minutes
        Integer dureeMinutes = 0;
        if (session.getDateDebut() != null && session.getDateFin() != null) {
            dureeMinutes = (int) java.time.temporal.ChronoUnit.MINUTES
                    .between(session.getDateDebut(), session.getDateFin());
        }
        
        // Score global de la session (moyenne des scores)
        Double scoreGlobal = scoresCompetences.isEmpty() ? 0.0 :
                scoresCompetences.stream()
                        .mapToDouble(s -> s.getScoreObtenu() != null ? s.getScoreObtenu() : 0.0)
                        .average()
                        .orElse(0.0);
        
        // Calculer nombre de questions
        Integer totalQuestions = session.getQuestionSessions().size();
        Integer questionsRepondues = (int) session.getQuestionSessions().stream()
                .filter(qs -> qs.getEstRepondue() != null && qs.getEstRepondue())
                .count();
        
        return AdminHistoriqueEtudiantDTO.SessionHistoriqueDTO.builder()
                .sessionId(session.getId())
                .numeroSession(session.getNumeroSession())
                .dateDebut(session.getDateDebut())
                .dateFin(session.getDateFin())
                .statut(session.getStatut().toString())
                .dureeMinutes(dureeMinutes)
                .scoreGlobal(Math.round(scoreGlobal * 100.0) / 100.0)
                .totalQuestions(totalQuestions)
                .questionsRepondues(questionsRepondues)
                .scoresCompetences(scoresCompetences)
                .raison(session.getRaison())
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
