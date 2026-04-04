package com.example.demo.dashboard.service;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.dashboard.dto.*;
import com.example.demo.evaluation.domain.SessionTest;
import com.example.demo.evaluation.domain.ScoreCompetence;
import com.example.demo.evaluation.domain.enums.StatutSession;
import com.example.demo.evaluation.repository.ScoreCompetenceRepository;
import com.example.demo.evaluation.repository.SessionTestRepository;
import com.example.demo.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour le dashboard apprenant
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardApprenantService {
    
    private final SessionTestRepository sessionTestRepository;
    private final ScoreCompetenceRepository scoreCompetenceRepository;
    private final RecommendationService recommendationService;
    
    /**
     * Récupérer le dashboard complet de l'apprenant
     */
    public ApprenantDashboardDTO getApprenantDashboard(Utilisateur user) {
        log.debug("Récupération du dashboard apprenant pour user: {}", user.getId());
        
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .build();
        
        // Progression
        ProgressionDTO progression = calculerProgression(user);
        
        // Scores
        ScoresDTO scores = calculerScores(user);
        
        // Compétences
        List<CompetenceProgressDTO> competences = getCompetencesProgress(user);
        
        // Activité récente (7 jours)
        List<ActiviteDTO> activite = getActiviteRecente(user, 7);
        
        return ApprenantDashboardDTO.builder()
                .utilisateur(userInfo)
                .progression(progression)
                .scores(scores)
                .competences(competences)
                .activiteRecente(activite)
                .build();
    }
    
    /**
     * Récupérer le détail d'une session pour l'apprenant
     */
    public SessionDetailDTO getSessionDetail(Long sessionId, Utilisateur user) {
        log.debug("Récupération du détail de session {} pour user {}", sessionId, user.getId());
        
        SessionTest session = sessionTestRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session non trouvée"));
        
        if (!session.getUtilisateur().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Accès non autorisé");
        }
        
        List<ScoreCompetence> scores = scoreCompetenceRepository.findBySession(session);
        List<CompetenceScoreDetailDTO> competenceScores = scores.stream()
                .map(score -> CompetenceScoreDetailDTO.builder()
                        .nom(score.getCompetence().getIntitule())
                        .scoreAvant(0.0) // À implémenter si historique disponible
                        .scoreApres(score.getScoreObtenu())
                        .evolution("+0%") // À calculer si besoin
                        .build())
                .collect(Collectors.toList());
        
        return SessionDetailDTO.builder()
                .sessionId(session.getId())
                .dateDebut(session.getDateDebut())
                .dateFin(session.getDateFin())
                .statut(session.getStatut())
                .scoreGlobal(calculerScoreSession(session))
                .competences(competenceScores)
                .build();
    }
    
    /**
     * Récupérer les sessions de l'apprenant
     */
    public List<SessionTest> getSessions(Utilisateur user) {
        log.debug("Récupération des sessions de l'apprenant {}", user.getId());
        return sessionTestRepository.findByUtilisateurOrderByDateDebutDesc(user);
    }
    
    /**
     * Récupérer les compétences et leur progression
     */
    public List<CompetenceProgressDTO> getCompetencesProgress(Utilisateur user) {
        log.debug("Récupération de la progression des compétences pour {}", user.getId());
        
        List<SessionTest> sessions = sessionTestRepository.findByUtilisateurOrderByDateDebutDesc(user);
        Map<String, CompetenceProgressDTO> competenceMap = new LinkedHashMap<>();
        
        for (SessionTest session : sessions) {
            List<ScoreCompetence> scores = scoreCompetenceRepository.findBySession(session);
            
            for (ScoreCompetence score : scores) {
                String competenceKey = score.getCompetence().getIntitule();
                competenceMap.putIfAbsent(competenceKey, CompetenceProgressDTO.builder()
                        .id(score.getCompetence().getId())
                        .nom(competenceKey)
                        .niveau(score.getNiveauAtteint())
                        .score(score.getScoreObtenu())
                        .lacunes("") // À implémenter si détails disponibles
                        .build());
            }
        }
        
        return new ArrayList<>(competenceMap.values());
    }
    
    /**
     * Récupérer l'activité récente
     */
    public List<ActiviteDTO> getActiviteRecente(Utilisateur user, int joursAntecedents) {
        log.debug("Récupération de l'activité récente pour {}", user.getId());
        
        List<SessionTest> sessions = sessionTestRepository.findByUtilisateurOrderByDateDebutDesc(user);
        LocalDateTime dateLimit = LocalDateTime.now().minusDays(joursAntecedents);
        
        return sessions.stream()
                .filter(session -> session.getDateDebut().isAfter(dateLimit))
                .map(session -> ActiviteDTO.builder()
                        .date(session.getDateDebut())
                        .type("SESSION")
                        .titre("Session d'évaluation")
                        .resultat(String.format("%.2f", calculerScoreSession(session)))
                        .build())
                .collect(Collectors.toList());
    }
    
    // ============ Méthodes utilitaires ============
    
    private ProgressionDTO calculerProgression(Utilisateur user) {
        List<SessionTest> sessions = sessionTestRepository.findByUtilisateurOrderByDateDebutDesc(user);
        long sessionsTerminees = sessions.stream()
                .filter(s -> s.getStatut() == StatutSession.TERMINEE)
                .count();
        
        long totalQuestions = sessions.stream()
                .flatMap(s -> s.getQuestionSessions().stream())
                .count();
        
        long questionsRepondues = sessions.stream()
                .flatMap(s -> s.getReponses().stream())
                .count();
        
        int completionPercent = totalQuestions > 0 
                ? (int) ((questionsRepondues * 100) / totalQuestions) 
                : 0;
        
        return ProgressionDTO.builder()
                .completionPercent(completionPercent)
                .sessionsTerminees(sessionsTerminees)
                .questionsRepondues(questionsRepondues)
                .build();
    }
    
    private ScoresDTO calculerScores(Utilisateur user) {
        List<SessionTest> sessions = sessionTestRepository.findByUtilisateurOrderByDateDebutDesc(user);
        
        if (sessions.isEmpty()) {
            return ScoresDTO.builder()
                    .scoreMoyenGlobal(0.0)
                    .scoreDerniereSession(0.0)
                    .evolutionScore("+0%")
                    .build();
        }
        
        Double scoreMoyen = sessions.stream()
                .mapToDouble(this::calculerScoreSession)
                .average()
                .orElse(0.0);
        
        Double dernierScore = calculerScoreSession(sessions.get(0));
        
        String evolution = sessions.size() > 1 
                ? String.format("+%.0f%%", (dernierScore - calculerScoreSession(sessions.get(1))) * 100)
                : "+0%";
        
        return ScoresDTO.builder()
                .scoreMoyenGlobal(scoreMoyen)
                .scoreDerniereSession(dernierScore)
                .evolutionScore(evolution)
                .build();
    }
    
    private Double calculerScoreSession(SessionTest session) {
        List<ScoreCompetence> scores = scoreCompetenceRepository.findBySession(session);
        if (scores.isEmpty()) return 0.0;
        
        return scores.stream()
                .mapToDouble(score -> score.getScoreObtenu() != null ? score.getScoreObtenu() : 0.0)
                .average()
                .orElse(0.0);
    }
}
