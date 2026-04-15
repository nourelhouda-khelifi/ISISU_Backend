package com.example.demo.dashboard.service;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.dashboard.dto.*;
import com.example.demo.evaluation.domain.SessionTest;
import com.example.demo.evaluation.domain.ScoreCompetence;
import com.example.demo.evaluation.domain.enums.StatutSession;
import com.example.demo.evaluation.repository.ScoreCompetenceRepository;
import com.example.demo.evaluation.repository.SessionTestRepository;
import com.example.demo.recommendation.service.RecommendationService;
import com.example.demo.referentiel.domain.UniteEnseignement;
import com.example.demo.referentiel.domain.ModuleFIE;
import com.example.demo.referentiel.domain.enums.Semestre;
import com.example.demo.referentiel.infrastructure.UniteEnseignementRepository;
import com.example.demo.referentiel.infrastructure.ModuleFIERepository;
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
    private final UniteEnseignementRepository uniteEnseignementRepository;
    private final ModuleFIERepository moduleFIERepository;
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
    
    /**
     * Récupérer les scores agrégés par UE
     * Calcule la moyenne des compétences pour chaque UE
     */
    public List<UEScoreDTO> getScoresParUE(Utilisateur user, Semestre semestre) {
        log.debug("Récupération des scores par UE pour user {} - S{}", user.getId(), semestre);
        
        // Récupérer toutes les sessions de l'apprenant
        List<SessionTest> sessions = sessionTestRepository.findByUtilisateurOrderByDateDebutDesc(user);
        if (sessions.isEmpty()) return Collections.emptyList();
        
        // Récupérer tous les scores de cet apprenant
        List<ScoreCompetence> allScores = sessions.stream()
                .flatMap(s -> scoreCompetenceRepository.findBySession(s).stream())
                .collect(Collectors.toList());
        
        if (allScores.isEmpty()) return Collections.emptyList();
        
        // Récupérer les UE du semestre spécifié
        List<UniteEnseignement> ues = uniteEnseignementRepository.findBySemestre(semestre);
        
        return ues.stream()
                .map(ue -> calculerScoreUE(ue, allScores))
                .sorted((a, b) -> b.getScoreMoyenUE().compareTo(a.getScoreMoyenUE()))
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les scores par UE pour TOUS les semestres
     */
    public List<UEScoreDTO> getScoresToutesUE(Utilisateur user) {
        log.debug("Récupération des scores par UE (toutes) pour user {}", user.getId());
        
        List<SessionTest> sessions = sessionTestRepository.findByUtilisateurOrderByDateDebutDesc(user);
        if (sessions.isEmpty()) return Collections.emptyList();
        
        List<ScoreCompetence> allScores = sessions.stream()
                .flatMap(s -> scoreCompetenceRepository.findBySession(s).stream())
                .collect(Collectors.toList());
        
        if (allScores.isEmpty()) return Collections.emptyList();
        
        // Récupérer toutes les UE
        List<UniteEnseignement> ues = uniteEnseignementRepository.findAll();
        
        return ues.stream()
                .map(ue -> calculerScoreUE(ue, allScores))
                .filter(ueScore -> ueScore.getNbCompetencesEvaluees() > 0)
                .sorted((a, b) -> b.getScoreMoyenUE().compareTo(a.getScoreMoyenUE()))
                .collect(Collectors.toList());
    }
    
    /**
     * Calculer le score pour une UE spécifique
     */
    private UEScoreDTO calculerScoreUE(UniteEnseignement ue, List<ScoreCompetence> allScores) {
        // Récupérer les modules de cette UE
        List<ModuleFIE> modules = moduleFIERepository.findByUniteEnseignementId(ue.getId());
        
        List<Long> moduleIds = modules.stream().map(ModuleFIE::getId).collect(Collectors.toList());
        
        // Filtrer les scores pour les compétences appartenant aux modules de cette UE
        List<ScoreCompetence> scoresUE = allScores.stream()
                .filter(score -> moduleIds.contains(score.getCompetence().getModule().getId()))
                .collect(Collectors.toList());
        
        // Calculer les statistiques
        Double scoreMoyen = scoresUE.isEmpty() ? 0.0
                : scoresUE.stream()
                    .mapToDouble(score -> score.getScoreObtenu() != null ? score.getScoreObtenu() : 0.0)
                    .average()
                    .orElse(0.0);
        
        int nbCompetencesEvaluees = scoresUE.size();
        
        long nbCompetencesAcquises = scoresUE.stream()
                .filter(score -> score.getStatut() != null 
                        && ("ACQUIS".equals(score.getStatut().toString()) 
                            || "MAITRISE".equals(score.getStatut().toString())))
                .count();
        
        Double tauxAcquisition = nbCompetencesEvaluees > 0 
                ? (nbCompetencesAcquises * 100.0) / nbCompetencesEvaluees
                : 0.0;
        
        String statut;
        if (scoreMoyen < 40) statut = "LACUNE";
        else if (scoreMoyen < 60) statut = "A_RENFORCER";
        else statut = "BON";
        
        // Calculer les scores par module
        List<UEScoreDTO.ModuleScoreDTO> moduleScores = modules.stream()
                .map(module -> {
                    List<ScoreCompetence> scoresModule = scoresUE.stream()
                            .filter(score -> score.getCompetence().getModule().getId().equals(module.getId()))
                            .collect(Collectors.toList());
                    
                    Double scoreMoyenModule = scoresModule.isEmpty() ? 0.0
                            : scoresModule.stream()
                                .mapToDouble(score -> score.getScoreObtenu() != null ? score.getScoreObtenu() : 0.0)
                                .average()
                                .orElse(0.0);
                    
                    String statutModule;
                    if (scoreMoyenModule < 40) statutModule = "LACUNE";
                    else if (scoreMoyenModule < 60) statutModule = "A_RENFORCER";
                    else statutModule = "BON";
                    
                    return UEScoreDTO.ModuleScoreDTO.builder()
                            .moduleId(module.getId())
                            .codeModule(module.getCode())
                            .nomModule(module.getNom())
                            .scoreMoyenModule(Math.round(scoreMoyenModule * 100.0) / 100.0)
                            .nbCompetences(scoresModule.size())
                            .statut(statutModule)
                            .build();
                })
                .collect(Collectors.toList());
        
        return UEScoreDTO.builder()
                .ueId(ue.getId())
                .codeUE(ue.getCode())
                .libelle(ue.getLibelle())
                .scoreMoyenUE(Math.round(scoreMoyen * 100.0) / 100.0)
                .nbCompetencesEvaluees(nbCompetencesEvaluees)
                .nbCompetencesAcquises((int) nbCompetencesAcquises)
                .tauxAcquisition(Math.round(tauxAcquisition * 100.0) / 100.0)
                .moduleScores(moduleScores)
                .statut(statut)
                .build();
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
