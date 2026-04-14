package com.example.demo.dashboard.service;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.auth.domain.enums.Role;
import com.example.demo.auth.infrastructure.repository.UtilisateurRepository;
import com.example.demo.dashboard.dto.*;
import com.example.demo.evaluation.domain.ReponseEtudiant;
import com.example.demo.evaluation.domain.SessionTest;
import com.example.demo.evaluation.domain.enums.StatutSession;
import com.example.demo.evaluation.repository.ReponseEtudiantRepository;
import com.example.demo.evaluation.repository.SessionTestRepository;
import com.example.demo.questions.domain.Question;
import com.example.demo.questions.domain.enums.NiveauDifficulte;
import com.example.demo.questions.domain.enums.TypeQuestion;
import com.example.demo.questions.repository.QuestionRepository;
import com.example.demo.referentiel.domain.Competence;
import com.example.demo.referentiel.infrastructure.CompetenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour les statistiques du dashboard admin
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatistiquesService {
    
    private final SessionTestRepository sessionTestRepository;
    private final ReponseEtudiantRepository reponseEtudiantRepository;
    private final QuestionRepository questionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CompetenceRepository competenceRepository;
    
    /**
     * GET /api/v1/dashboard/admin/statistiques/competences
     * Statistiques des compétences
     */
    public StatistiquesCompetencesDTO getStatistiquesCompetences() {
        log.debug("Calcul des statistiques des compétences");
        
        List<Competence> competences = competenceRepository.findAll();
        List<StatistiquesCompetencesDTO.TopCompetenceDTO> topCompetences = new ArrayList<>();
        List<StatistiquesCompetencesDTO.CompetenceTendanceDTO> tendances = new ArrayList<>();
        
        for (Competence competence : competences) {
            // Calculer les stats pour cette compétence
            List<ReponseEtudiant> reponses = reponseEtudiantRepository.findAll().stream()
                    .filter(r -> r.getQuestionSession().getQuestion().getCompetences() != null &&
                           r.getQuestionSession().getQuestion().getCompetences().contains(competence))
                    .collect(Collectors.toList());
            
            if (!reponses.isEmpty()) {
                Double scoreMoyen = calculerScoreMoyenCompetence(reponses);
                Long nombreApprenants = reponses.stream()
                        .map(r -> r.getSession().getUtilisateur().getId())
                        .distinct()
                        .count();
                Double tauxReussite = (double) reponses.stream()
                        .filter(ReponseEtudiant::getEstCorrecte)
                        .count() / reponses.size();
                
                topCompetences.add(StatistiquesCompetencesDTO.TopCompetenceDTO.builder()
                        .id(competence.getId())
                        .nom(competence.getIntitule())
                        .scoreMoyen(Math.round(scoreMoyen * 100.0) / 100.0)
                        .nombreApprenants(nombreApprenants)
                        .tauxReussite(Math.round(tauxReussite * 100.0) / 100.0)
                        .build());
            }
        }
        
        // Trier par score moyen décroissant et garder les top 10
        topCompetences.sort((a, b) -> b.getScoreMoyen().compareTo(a.getScoreMoyen()));
        topCompetences = topCompetences.stream().limit(10).collect(Collectors.toList());
        
        // Calculer les tendances (dernières 3 semaines)
        for (Competence competence : competences.stream().limit(10).collect(Collectors.toList())) {
            Map<String, StatistiquesCompetencesDTO.ScoreWeekDTO> semaines = new HashMap<>();
            
            for (int i = 3; i >= 1; i--) {
                LocalDate debut = LocalDate.now().minusWeeks(i);
                LocalDate fin = debut.plusWeeks(1);
                
                List<ReponseEtudiant> reponsesSemaine = reponseEtudiantRepository.findAll().stream()
                        .filter(r -> r.getQuestionSession().getQuestion().getCompetences() != null &&
                               r.getQuestionSession().getQuestion().getCompetences().contains(competence) &&
                               r.getDateReponse().toLocalDate().isAfter(debut) &&
                               r.getDateReponse().toLocalDate().isBefore(fin))
                        .collect(Collectors.toList());
                
                if (!reponsesSemaine.isEmpty()) {
                    Double scoreWeek = calculerScoreMoyenCompetence(reponsesSemaine);
                    semaines.put("semaine" + (3 - i + 1), StatistiquesCompetencesDTO.ScoreWeekDTO.builder()
                            .score(Math.round(scoreWeek * 100.0) / 100.0)
                            .nombreTests((long) reponsesSemaine.size())
                            .build());
                }
            }
            
            if (!semaines.isEmpty()) {
                tendances.add(StatistiquesCompetencesDTO.CompetenceTendanceDTO.builder()
                        .id(competence.getId())
                        .nom(competence.getIntitule())
                        .semaines(semaines)
                        .build());
            }
        }
        
        return StatistiquesCompetencesDTO.builder()
                .topCompetences(topCompetences)
                .competencesTendances(tendances.stream().limit(5).collect(Collectors.toList()))
                .build();
    }
    
    /**
     * GET /api/v1/dashboard/admin/statistiques/questions
     * Statistiques des questions par type et difficulté
     */
    public StatistiquesQuestionsDTO getStatistiquesQuestions() {
        log.debug("Calcul des statistiques des questions");
        
        Map<String, StatistiquesQuestionsDTO.QuestionStatsDTO> parType = new HashMap<>();
        Map<String, StatistiquesQuestionsDTO.QuestionStatsDTO> parDifficulte = new HashMap<>();
        
        // Par type de question
        for (TypeQuestion type : TypeQuestion.values()) {
            List<Question> questions = questionRepository.findByType(type);
            
            Long nombre = (long) questions.size();
            Double tauxReussite = calculerTauxReussiteQuestions(questions);
            
            parType.put(type.name(), StatistiquesQuestionsDTO.QuestionStatsDTO.builder()
                    .nombre(nombre)
                    .tauxReussite(Math.round(tauxReussite * 100.0) / 100.0)
                    .nombreUtilisations(questions.size() > 0 ? 
                            (long) reponseEtudiantRepository.findAll().stream()
                                .filter(r -> questions.contains(r.getQuestionSession().getQuestion()))
                                .count() : 0)
                    .build());
        }
        
        // Par difficulté
        for (NiveauDifficulte difficulte : NiveauDifficulte.values()) {
            List<Question> questions = questionRepository.findByDifficulte(difficulte);
            
            Long nombre = (long) questions.size();
            Double tauxReussite = calculerTauxReussiteQuestions(questions);
            
            parDifficulte.put(difficulte.name(), StatistiquesQuestionsDTO.QuestionStatsDTO.builder()
                    .nombre(nombre)
                    .tauxReussite(Math.round(tauxReussite * 100.0) / 100.0)
                    .nombreUtilisations(questions.size() > 0 ? 
                            (long) reponseEtudiantRepository.findAll().stream()
                                .filter(r -> questions.contains(r.getQuestionSession().getQuestion()))
                                .count() : 0)
                    .build());
        }
        
        return StatistiquesQuestionsDTO.builder()
                .parType(parType)
                .parDifficulte(parDifficulte)
                .build();
    }
    
    /**
     * GET /api/v1/dashboard/admin/statistiques/performances
     * Statistiques des performances des apprenants
     */
    public StatistiquesPerformancesDTO getStatistiquesPerformances() {
        log.debug("Calcul des statistiques des performances");
        
        // Distribution des scores
        List<StatistiquesPerformancesDTO.DistributionScoreDTO> distribution = new ArrayList<>();
        int[][] ranges = {{0, 20}, {20, 40}, {40, 60}, {60, 80}, {80, 100}};
        
        List<Double> scores = calculerScoresApprenants();
        long totalScores = scores.size();
        
        for (int[] range : ranges) {
            long count = scores.stream()
                    .filter(s -> s >= range[0] && s <= range[1])
                    .count();
            Double pourcentage = totalScores > 0 ? (double) count / totalScores : 0.0;
            
            distribution.add(StatistiquesPerformancesDTO.DistributionScoreDTO.builder()
                    .range(range[0] + "-" + range[1] + "%")
                    .nombre(count)
                    .pourcentage(Math.round(pourcentage * 100.0) / 100.0)
                    .build());
        }
        
        // Par type d'apprenant
        Map<String, StatistiquesPerformancesDTO.PerformanceApprenantsDTO> parTypApprenant = new HashMap<>();
        
        for (Role role : new Role[]{Role.ETUDIANT_FIE3, Role.CANDIDAT_VAE}) {
            List<Utilisateur> apprenants = utilisateurRepository.findAll().stream()
                    .filter(u -> u.getRole() == role)
                    .collect(Collectors.toList());
            
            if (!apprenants.isEmpty()) {
                Double scoreMoyen = calculerScoreMoyenParRole(role);
                Double tauxReussite = calculerTauxReussiteParRole(role);
                
                parTypApprenant.put(role.name(), StatistiquesPerformancesDTO.PerformanceApprenantsDTO.builder()
                        .scoreMoyen(Math.round(scoreMoyen * 100.0) / 100.0)
                        .nombreApprenants((long) apprenants.size())
                        .tauxReussite(Math.round(tauxReussite * 100.0) / 100.0)
                        .build());
            }
        }
        
        return StatistiquesPerformancesDTO.builder()
                .distributionScore(distribution)
                .parTypApprenant(parTypApprenant)
                .build();
    }
    
    /**
     * GET /api/v1/dashboard/admin/statistiques/activite
     * Statistiques d'activité sur une période donnée
     */
    public StatistiquesActiviteDTO getStatistiquesActivite(String periode) {
        log.debug("Calcul des statistiques d'activité pour la période: {}", periode);
        
        int jours = extractJours(periode);
        LocalDate dateDebut = LocalDate.now().minusDays(jours);
        LocalDate dateFin = LocalDate.now();
        
        List<StatistiquesActiviteDTO.SessionParJourDTO> sessionsParJour = new ArrayList<>();
        List<StatistiquesActiviteDTO.ApprenantsActifsDTO> apprenantsActifs = new ArrayList<>();
        
        for (LocalDate date = dateDebut; date.isBefore(dateFin.plusDays(1)); date = date.plusDays(1)) {
            LocalDateTime debut = date.atStartOfDay();
            LocalDateTime fin = date.atTime(LocalTime.MAX);
            
            List<SessionTest> sessions = sessionTestRepository.findAll().stream()
                    .filter(s -> s.getDateDebut().isAfter(debut) && s.getDateDebut().isBefore(fin))
                    .collect(Collectors.toList());
            
            if (!sessions.isEmpty()) {
                long nombre = sessions.size();
                Double dureeParMoyenne = sessions.stream()
                        .mapToDouble(s -> s.getDureeMaxSecondes())
                        .average()
                        .orElse(0.0);
                
                long terminees = sessions.stream()
                        .filter(s -> s.getStatut() == StatutSession.TERMINEE)
                        .count();
                Double completionRate = (double) terminees / nombre;
                
                sessionsParJour.add(StatistiquesActiviteDTO.SessionParJourDTO.builder()
                        .date(date)
                        .nombre(nombre)
                        .dureeParMoyenne(Math.round(dureeParMoyenne / 60.0) / 100.0) // en minutes
                        .completionRate(Math.round(completionRate * 100.0) / 100.0)
                        .build());
            }
            
            // Apprenants actifs
            Set<Long> apprenantsActifsJour = sessionTestRepository.findAll().stream()
                    .filter(s -> s.getDateDebut().isAfter(debut) && s.getDateDebut().isBefore(fin))
                    .map(s -> s.getUtilisateur().getId())
                    .collect(Collectors.toSet());
            
            if (!apprenantsActifsJour.isEmpty()) {
                apprenantsActifs.add(StatistiquesActiviteDTO.ApprenantsActifsDTO.builder()
                        .date(date)
                        .nombre((long) apprenantsActifsJour.size())
                        .build());
            }
        }
        
        long totalSessions = sessionsParJour.stream().mapToLong(StatistiquesActiviteDTO.SessionParJourDTO::getNombre).sum();
        long totalApprenants = apprenantsActifs.stream().mapToLong(StatistiquesActiviteDTO.ApprenantsActifsDTO::getNombre).sum();
        
        return StatistiquesActiviteDTO.builder()
                .sessionsParJour(sessionsParJour)
                .apprenantsActifs(apprenantsActifs)
                .totalSessions(totalSessions)
                .totalApprenantsActifs(totalApprenants)
                .build();
    }
    
    /**
     * GET /api/v1/dashboard/admin/statistiques/heatmap
     * Heatmap des compétences par difficulté
     */
    public StatistiquesHeatmapDTO getStatistiquesHeatmap() {
        log.debug("Calcul de la heatmap des statistiques");
        
        List<StatistiquesHeatmapDTO.HeatmapItemDTO> items = new ArrayList<>();
        List<Competence> competences = competenceRepository.findAll().stream()
                .limit(10)
                .collect(Collectors.toList());
        
        for (Competence competence : competences) {
            Map<String, Double> performance = new HashMap<>();
            
            for (NiveauDifficulte difficulte : NiveauDifficulte.values()) {
                List<Question> questions = questionRepository.findByDifficulte(difficulte).stream()
                        .filter(q -> q.getCompetences() != null &&
                               q.getCompetences().contains(competence))
                        .collect(Collectors.toList());
                
                if (!questions.isEmpty()) {
                    Double tauxReussite = calculerTauxReussiteQuestions(questions);
                    performance.put(difficulte.name(), Math.round(tauxReussite * 100.0) / 100.0);
                }
            }
            
            if (!performance.isEmpty()) {
                items.add(StatistiquesHeatmapDTO.HeatmapItemDTO.builder()
                        .competence(competence.getIntitule())
                        .competenceId(competence.getId())
                        .performanceParDifficulte(performance)
                        .build());
            }
        }
        
        return StatistiquesHeatmapDTO.builder()
                .competenceParDifficulte(items)
                .build();
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    
    private Double calculerScoreMoyenCompetence(List<ReponseEtudiant> reponses) {
        if (reponses.isEmpty()) return 0.0;
        long correctes = reponses.stream().filter(ReponseEtudiant::getEstCorrecte).count();
        return (double) (correctes * 100) / reponses.size();
    }
    
    private Double calculerTauxReussiteQuestions(List<Question> questions) {
        if (questions.isEmpty()) return 0.0;
        List<ReponseEtudiant> reponses = reponseEtudiantRepository.findAll().stream()
                .filter(r -> questions.contains(r.getQuestionSession().getQuestion()))
                .collect(Collectors.toList());
        
        if (reponses.isEmpty()) return 0.0;
        long correctes = reponses.stream().filter(ReponseEtudiant::getEstCorrecte).count();
        return (double) correctes / reponses.size();
    }
    
    private List<Double> calculerScoresApprenants() {
        List<Utilisateur> apprenants = utilisateurRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ETUDIANT_FIE3 || u.getRole() == Role.CANDIDAT_VAE)
                .collect(Collectors.toList());
        
        List<Double> scores = new ArrayList<>();
        for (Utilisateur apprenant : apprenants) {
            List<ReponseEtudiant> reponses = reponseEtudiantRepository.findAll().stream()
                    .filter(r -> r.getSession().getUtilisateur().getId().equals(apprenant.getId()))
                    .collect(Collectors.toList());
            
            if (!reponses.isEmpty()) {
                double score = (double) (reponses.stream().filter(ReponseEtudiant::getEstCorrecte).count() * 100) / reponses.size();
                scores.add(score);
            }
        }
        return scores;
    }
    
    private Double calculerScoreMoyenParRole(Role role) {
        List<Double> scores = utilisateurRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .map(u -> {
                    List<ReponseEtudiant> reponses = reponseEtudiantRepository.findAll().stream()
                            .filter(r -> r.getSession().getUtilisateur().getId().equals(u.getId()))
                            .collect(Collectors.toList());
                    if (reponses.isEmpty()) return 0.0;
                    return (double) (reponses.stream().filter(ReponseEtudiant::getEstCorrecte).count() * 100) / reponses.size();
                })
                .collect(Collectors.toList());
        
        return scores.isEmpty() ? 0.0 : scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    private Double calculerTauxReussiteParRole(Role role) {
        List<ReponseEtudiant> reponses = reponseEtudiantRepository.findAll().stream()
                .filter(r -> r.getSession().getUtilisateur().getRole() == role)
                .collect(Collectors.toList());
        
        if (reponses.isEmpty()) return 0.0;
        long correctes = reponses.stream().filter(ReponseEtudiant::getEstCorrecte).count();
        return (double) correctes / reponses.size();
    }
    
    private int extractJours(String periode) {
        if (periode == null || periode.isEmpty()) return 7; // Par défaut 7 jours
        return Integer.parseInt(periode.replaceAll("[^0-9]", ""));
    }
}
