package com.example.demo.dashboard.service;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.auth.domain.enums.Role;
import com.example.demo.auth.domain.enums.StatutCompte;
import com.example.demo.auth.infrastructure.repository.UtilisateurRepository;
import com.example.demo.dashboard.dto.*;
import com.example.demo.evaluation.domain.SessionTest;
import com.example.demo.evaluation.domain.ScoreCompetence;
import com.example.demo.evaluation.domain.enums.StatutSession;
import com.example.demo.evaluation.repository.ScoreCompetenceRepository;
import com.example.demo.evaluation.repository.SessionTestRepository;
import com.example.demo.referentiel.domain.UniteEnseignement;
import com.example.demo.referentiel.domain.ModuleFIE;
import com.example.demo.referentiel.domain.enums.Semestre;
import com.example.demo.referentiel.infrastructure.UniteEnseignementRepository;
import com.example.demo.referentiel.infrastructure.ModuleFIERepository;
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
    private final UniteEnseignementRepository uniteEnseignementRepository;
    private final ModuleFIERepository moduleFIERepository;
    
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
        
        // Calculer les top compétences et lacunes
        List<CompetenceStatsDTO> topCompetences = calculerTopCompetences();
        List<CompetenceStatsDTO> competencesLacunes = calculerCompetencesLacunes();
        
        return AdminDashboardDTO.builder()
                .totalUtilisateurs(totalUsers)
                .totalEtudiantsFIE3(totalFIE3)
                .totalCandidatsVAE(totalVAE)
                .scoreMoyenGlobal(scoreMoyen)
                .sessionsEnCours(sessionsEnCours)
                .tauxReussite(tauxReussite)
                .competencesTopPerformance(topCompetences)
                .competencesLacunes(competencesLacunes)
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
    
    /**
     * Récupérer les scores agrégés par UE pour un étudiant spécifique
     * Utilisé par l'admin pour voir les UE d'un étudiant
     */
    public List<UEScoreDTO> getScoresUEPourEtudiant(Long idEtudiant, Semestre semestre) {
        log.debug("Récupération scores UE pour étudiant {} - S{}", idEtudiant, semestre);
        
        Utilisateur etudiant = utilisateurRepository.findById(idEtudiant)
                .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé"));
        
        // Récupérer toutes les sessions de l'étudiant
        List<SessionTest> sessions = sessionTestRepository.findByUtilisateurOrderByDateDebutDesc(etudiant);
        if (sessions.isEmpty()) return Collections.emptyList();
        
        // Récupérer tous les scores de l'étudiant
        List<ScoreCompetence> allScores = sessions.stream()
                .flatMap(s -> scoreCompetenceRepository.findBySession(s).stream())
                .collect(Collectors.toList());
        
        if (allScores.isEmpty()) return Collections.emptyList();
        
        // Récupérer les UE du semestre spécifié
        List<UniteEnseignement> ues = uniteEnseignementRepository.findBySemestre(semestre);
        
        return ues.stream()
                .map(ue -> calculerScoreUEPourEtudiant(ue, allScores))
                .filter(ueScore -> ueScore.getNbCompetencesEvaluees() > 0)
                .sorted((a, b) -> b.getScoreMoyenUE().compareTo(a.getScoreMoyenUE()))
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les scores par UE pour TOUS les semestres (étudiant)
     */
    public List<UEScoreDTO> getScoresUEToutsSemestresPourEtudiant(Long idEtudiant) {
        log.debug("Récupération scores UE (tous semestres) pour étudiant {}", idEtudiant);
        
        Utilisateur etudiant = utilisateurRepository.findById(idEtudiant)
                .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé"));
        
        List<SessionTest> sessions = sessionTestRepository.findByUtilisateurOrderByDateDebutDesc(etudiant);
        if (sessions.isEmpty()) return Collections.emptyList();
        
        List<ScoreCompetence> allScores = sessions.stream()
                .flatMap(s -> scoreCompetenceRepository.findBySession(s).stream())
                .collect(Collectors.toList());
        
        if (allScores.isEmpty()) return Collections.emptyList();
        
        // Récupérer toutes les UE
        List<UniteEnseignement> ues = uniteEnseignementRepository.findAll();
        
        return ues.stream()
                .map(ue -> calculerScoreUEPourEtudiant(ue, allScores))
                .filter(ueScore -> ueScore.getNbCompetencesEvaluees() > 0)
                .sorted((a, b) -> b.getScoreMoyenUE().compareTo(a.getScoreMoyenUE()))
                .collect(Collectors.toList());
    }
    
    /**
     * Calculer le score pour une UE spécifique (pour un étudiant via ses scores)
     */
    private UEScoreDTO calculerScoreUEPourEtudiant(UniteEnseignement ue, List<ScoreCompetence> scoresScolaire) {
        // Récupérer les modules de cette UE
        List<ModuleFIE> modules = moduleFIERepository.findByUniteEnseignementId(ue.getId());
        
        List<Long> moduleIds = modules.stream().map(ModuleFIE::getId).collect(Collectors.toList());
        
        // Filtrer les scores pour les compétences appartenant aux modules de cette UE
        List<ScoreCompetence> scoresUE = scoresScolaire.stream()
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
    
    /**
     * Calculer les TOP 5 compétences avec les meilleures performances
     */
    private List<CompetenceStatsDTO> calculerTopCompetences() {
        List<com.example.demo.evaluation.domain.ScoreCompetence> allScores = scoreCompetenceRepository.findAll();
        
        if (allScores.isEmpty()) return Collections.emptyList();
        
        return allScores.stream()
                .collect(Collectors.groupingBy(
                        score -> score.getCompetence(),
                        Collectors.averagingDouble(score -> score.getScoreObtenu() != null ? score.getScoreObtenu() : 0.0)
                ))
                .entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(entry -> {
                    long nbApprenants = allScores.stream()
                            .filter(s -> s.getCompetence().getId().equals(entry.getKey().getId()))
                            .map(s -> s.getSession().getUtilisateur().getId())
                            .distinct()
                            .count();
                    
                    long nbAcquis = allScores.stream()
                            .filter(s -> s.getCompetence().getId().equals(entry.getKey().getId()))
                            .filter(s -> s.getStatut() != null && "ACQUIS".equals(s.getStatut().toString()))
                            .count();
                    
                    long nbAdaptes = allScores.stream()
                            .filter(s -> s.getCompetence().getId().equals(entry.getKey().getId()))
                            .count();
                    
                    Double tauxAcquisition = nbAdaptes > 0 ? (double) (nbAcquis * 100 / nbAdaptes) : 0.0;
                    
                    return CompetenceStatsDTO.builder()
                            .id(entry.getKey().getId())
                            .nom(entry.getKey().getIntitule())
                            .scoreMoyen(Math.round(entry.getValue() * 100.0) / 100.0)
                            .nombreApprenants(nbApprenants)
                            .tauxAcquisition(tauxAcquisition)
                            .evolution("↗")
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Calculer les compétences avec LACUNES (scores faibles)
     */
    private List<CompetenceStatsDTO> calculerCompetencesLacunes() {
        List<com.example.demo.evaluation.domain.ScoreCompetence> allScores = scoreCompetenceRepository.findAll();
        
        if (allScores.isEmpty()) return Collections.emptyList();
        
        return allScores.stream()
                .collect(Collectors.groupingBy(
                        score -> score.getCompetence(),
                        Collectors.averagingDouble(score -> score.getScoreObtenu() != null ? score.getScoreObtenu() : 0.0)
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue() < 50.0) // Seuil: moins de 50%
                .sorted((a, b) -> Double.compare(a.getValue(), b.getValue()))
                .limit(5)
                .map(entry -> {
                    long nbApprenants = allScores.stream()
                            .filter(s -> s.getCompetence().getId().equals(entry.getKey().getId()))
                            .map(s -> s.getSession().getUtilisateur().getId())
                            .distinct()
                            .count();
                    
                    long nbAcquis = allScores.stream()
                            .filter(s -> s.getCompetence().getId().equals(entry.getKey().getId()))
                            .filter(s -> s.getStatut() != null && "ACQUIS".equals(s.getStatut().toString()))
                            .count();
                    
                    long nbAdaptes = allScores.stream()
                            .filter(s -> s.getCompetence().getId().equals(entry.getKey().getId()))
                            .count();
                    
                    Double tauxAcquisition = nbAdaptes > 0 ? (double) (nbAcquis * 100 / nbAdaptes) : 0.0;
                    
                    return CompetenceStatsDTO.builder()
                            .id(entry.getKey().getId())
                            .nom(entry.getKey().getIntitule())
                            .scoreMoyen(Math.round(entry.getValue() * 100.0) / 100.0)
                            .nombreApprenants(nbApprenants)
                            .tauxAcquisition(tauxAcquisition)
                            .evolution("↘")
                            .build();
                })
                .collect(Collectors.toList());
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
