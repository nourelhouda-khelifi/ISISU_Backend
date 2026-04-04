package com.example.demo.recommendation.service;

import com.example.demo.evaluation.domain.ScoreCompetence;
import com.example.demo.evaluation.domain.SessionTest;
import com.example.demo.recommendation.dto.*;
import com.example.demo.evaluation.repository.ScoreCompetenceRepository;
import com.example.demo.evaluation.repository.SessionTestRepository;
import com.example.demo.referentiel.domain.Competence;
import com.example.demo.referentiel.domain.ModuleFIE;
import com.example.demo.referentiel.infrastructure.CompetenceRepository;
import com.example.demo.referentiel.infrastructure.ModuleFIERepository;
import com.example.demo.auth.domain.Utilisateur;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RecommendationService - PHASE 1: ALGO MANUEL
 * 
 * Calcule les données brutes structurées pour les recommandations.
 * Pas de LLM ici - juste du traitement algorithme pur.
 * 
 * Output: RecommendationData (JSON structuré)
 * 
 * Calcule:
 * - Profil étudiant
 * - Scores par module (pondérés)
 * - Progression (3 dernières sessions)
 * - Dépendances bloquantes
 * - Forces
 * - Lacunes critiques
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    
    private final ScoreCompetenceRepository scoreRepository;
    private final SessionTestRepository sessionRepository;
    private final ModuleFIERepository moduleFIERepository;
    private final CompetenceRepository competenceRepository;
    private final GeminiClient geminiClient;
    private final PromptBuilder promptBuilder;
    
    /**
     * Compute la RecommendationData complète pour une session
     */
    public RecommendationData computeStructuredData(SessionTest session) {
        log.info("Computing structured data for session {}", session.getId());
        
        // 1. Get student profile
        StudentProfile profile = buildProfile(session);
        
        // 2. Get all scores for this session
        List<ScoreCompetence> scores = scoreRepository.findBySession(session);
        
        // 3. Build module scores
        List<ModuleScore> moduleScores = buildModuleScores(scores);
        
        // 4. Analyze progression
        Progression progression = analyzeProgression(session.getUtilisateur());
        
        // 5. Calculate blocking dependencies
        Map<String, BlockingDependency> blockingDeps = calculateBlockingDependencies(scores);
        
        // 6. Extract strengths
        List<StrengthPoint> strengths = calculateStrengths(scores);
        
        // 7. Identify critical gaps
        List<CriticalGap> criticalGaps = calculateCriticalGaps(scores, blockingDeps);
        
        log.info("Structured data computed: {} modules, {} gaps, {} strengths",
            moduleScores.size(), criticalGaps.size(), strengths.size());
        
        return RecommendationData.builder()
            .studentProfile(profile)
            .scoresByModule(moduleScores)
            .progression(progression)
            .blockingDependencies(blockingDeps)
            .strengths(strengths)
            .criticalGaps(criticalGaps)
            .build();
    }
    
    // ══════════════════════════════════════════════════════════════════════════════════
    //  1. BUILD STUDENT PROFILE
    // ══════════════════════════════════════════════════════════════════════════════════
    
    private StudentProfile buildProfile(SessionTest session) {
        Utilisateur user = session.getUtilisateur();
        int nbSessions = (int) sessionRepository.countByUtilisateur(user);
        
        return StudentProfile.builder()
            .niveau(user.getRole().name())  // FIE3, CANDIDAT_VAE, ADMIN
            .parcours(user.getParcoursOrigine() != null ? user.getParcoursOrigine().name() : "Non spécifié")
            .nbSessions(nbSessions)
            .build();
    }
    
    // ══════════════════════════════════════════════════════════════════════════════════
    //  2. BUILD MODULE SCORES (Pondérés)
    // ══════════════════════════════════════════════════════════════════════════════════
    
    private List<ModuleScore> buildModuleScores(List<ScoreCompetence> scores) {
        // Group scores by module
        Map<ModuleFIE, List<ScoreCompetence>> byModule = scores.stream()
            .collect(Collectors.groupingBy(score -> score.getCompetence().getModule()));
        
        return byModule.entrySet().stream()
            .map(entry -> {
                ModuleFIE module = entry.getKey();
                List<ScoreCompetence> moduleScores = entry.getValue();
                
                // Calculate weighted score
                double totalScore = moduleScores.stream()
                    .mapToDouble(s -> 
                        s.getScoreObtenu() * s.getCompetence().getPoids()
                    )
                    .sum();
                
                double totalPonderation = moduleScores.stream()
                    .mapToDouble(s -> s.getCompetence().getPoids())
                    .sum();
                
                double moduleScore = totalPonderation > 0 
                    ? totalScore / totalPonderation 
                    : 50.0;
                
                // NORMALIZE: Convert 0-1 range to 0-100
                double normalizedScore = moduleScore * 100.0;
                String status = mapScoreToStatus(normalizedScore);
                
                return ModuleScore.builder()
                    .module(module.getNom())
                    .score((int) normalizedScore)
                    .status(status)
                    .build();
            })
            .sorted(Comparator.comparingInt(ModuleScore::getScore).reversed())
            .collect(Collectors.toList());
    }
    
    private String mapScoreToStatus(double score) {
        if (score < 50) return "LACUNE";
        if (score < 75) return "A_RENFORCER";
        if (score < 90) return "ACQUIS";
        return "MAITRISE";
    }
    
    // ══════════════════════════════════════════════════════════════════════════════════
    //  3. ANALYZE PROGRESSION (3 dernières sessions)
    // ══════════════════════════════════════════════════════════════════════════════════
    
    private Progression analyzeProgression(Utilisateur utilisateur) {
        // Fetch last 3 sessions
        List<SessionTest> lastSessions = sessionRepository
            .findTop3ByUtilisateurOrderByDateDebutDesc(utilisateur);
        
        if (lastSessions.isEmpty()) {
            return Progression.builder()
                .sessions(Collections.emptyList())
                .tendance("DONNEES_INSUFFISANTES")
                .velocite("N/A")
                .build();
        }
        
        // Sort chronologically (oldest first)
        lastSessions.sort(Comparator.comparing(SessionTest::getDateDebut));
        
        // Build progression points
        List<SessionProgressionPoint> points = new ArrayList<>();
        for (int i = 0; i < lastSessions.size(); i++) {
            SessionTest session = lastSessions.get(i);
            double globalScore = calculateGlobalScore(session);
            
            // NORMALIZE: Convert 0-1 range to 0-100
            int normalizedGlobalScore = (int) (globalScore * 100.0);
            
            points.add(SessionProgressionPoint.builder()
                .sessionNum(i + 1)
                .date(session.getDateDebut())
                .scoreGlobal(normalizedGlobalScore)
                .build());
        }
        
        // Calculate tendancy
        String tendance = calculateTendancy(points);
        double velocite = calculateVelocity(points);
        
        return Progression.builder()
            .sessions(points)
            .tendance(tendance)
            .velocite(String.format("%.1f%% par session", velocite))
            .build();
    }
    
    private double calculateGlobalScore(SessionTest session) {
        List<ScoreCompetence> scores = scoreRepository.findBySession(session);
        if (scores.isEmpty()) return 0;
        
        return scores.stream()
            .mapToDouble(ScoreCompetence::getScoreObtenu)
            .average()
            .orElse(0);
    }
    
    private String calculateTendancy(List<SessionProgressionPoint> points) {
        if (points.size() < 2) return "DONNEES_INSUFFISANTES";
        
        int firstScore = points.get(0).getScoreGlobal();
        int lastScore = points.get(points.size() - 1).getScoreGlobal();
        int diff = lastScore - firstScore;
        
        if (diff > 10) return "PROGRESSION_POSITIVE: +" + diff + "%";
        if (diff < -10) return "REGRESSION: " + diff + "%";
        return "STABLE";
    }
    
    private double calculateVelocity(List<SessionProgressionPoint> points) {
        if (points.size() < 2) return 0;
        
        int totalDiff = points.get(points.size() - 1).getScoreGlobal() 
                      - points.get(0).getScoreGlobal();
        
        return (double) totalDiff / (points.size() - 1);
    }
    
    // ══════════════════════════════════════════════════════════════════════════════════
    //  4. CALCULATE BLOCKING DEPENDENCIES
    // ══════════════════════════════════════════════════════════════════════════════════
    
    private Map<String, BlockingDependency> calculateBlockingDependencies(
        List<ScoreCompetence> scores) {
        
        Map<String, BlockingDependency> result = new HashMap<>();
        
        scores.stream()
            .filter(s -> s.getScoreObtenu() < 75)  // LACUNE or A_RENFORCER
            .forEach(score -> {
                ModuleFIE module = score.getCompetence().getModule();
                
                // Find modules that depend on this module
                List<ModuleFIE> dependents = moduleFIERepository
                    .findDependentModules(module.getId());
                
                if (!dependents.isEmpty()) {
                    String severity = score.getScoreObtenu() < 30 ? "CRITIQUE" : "HAUTE";
                    
                    result.put(module.getNom(), BlockingDependency.builder()
                        .bloque(dependents.stream()
                            .map(ModuleFIE::getNom)
                            .collect(Collectors.toList()))
                        .severite(severity)
                        .build());
                    
                    log.debug("Module {} bloque {} modules", 
                        module.getNom(), dependents.size());
                }
            });
        
        return result;
    }
    
    // ══════════════════════════════════════════════════════════════════════════════════
    //  5. EXTRACT STRENGTHS
    // ══════════════════════════════════════════════════════════════════════════════════
    
    private List<StrengthPoint> calculateStrengths(List<ScoreCompetence> scores) {
        return scores.stream()
            .filter(s -> s.getScoreObtenu() > 85)
            .map(s -> StrengthPoint.builder()
                .module(s.getCompetence().getModule().getNom())
                .score(s.getScoreObtenu())
                .build())
            .collect(Collectors.toList());
    }
    
    // ══════════════════════════════════════════════════════════════════════════════════
    //  6. IDENTIFY CRITICAL GAPS
    // ══════════════════════════════════════════════════════════════════════════════════
    
    private List<CriticalGap> calculateCriticalGaps(
        List<ScoreCompetence> scores,
        Map<String, BlockingDependency> blockingDeps) {
        
        return scores.stream()
            .filter(s -> s.getScoreObtenu() < 50)
            .map(score -> {
                ModuleFIE module = score.getCompetence().getModule();
                BlockingDependency blocking = blockingDeps.get(module.getNom());
                
                String raison = blocking != null 
                    ? String.format("Prérequis de %d modules - BLOQUANT!", 
                        blocking.getBloque().size())
                    : "Score insuffisant";
                
                return CriticalGap.builder()
                    .module(module.getNom())
                    .score(score.getScoreObtenu())
                    .raison(raison)
                    .build();
            })
            .sorted(Comparator.comparingDouble(CriticalGap::getScore))
            .limit(5)  // Top 5 critical gaps
            .collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════════════════════
    //  PHASE 2: ENRICHISSEMENT AVEC LLM GEMINI
    // ══════════════════════════════════════════════════════════════════════════════════

    /**
     * Enrichit les données structurées (PHASE 1) avec l'analyse LLM (PHASE 2)
     * 
     * Étapes:
     * 1. Prend RecommendationData calculées par algo manuel
     * 2. Les convertit en prompt naturel (PromptBuilder)
     * 3. Appelle l'API Gemini (GeminiClient)
     * 4. Récupère AnalyseLLM enrichie avec textes personnalisés
     *
     * @param data Les données structurées de la PHASE 1
     * @return AnalyseLLM avec analyse enrichie par le LLM
     */
    public AnalyseLLM.AnalyseLLMWithData enrichWithLLM(RecommendationData data) {
        log.info("PHASE 2 - Enrichissement avec LLM...");
        
        try {
            // 1. Construire le prompt
            String prompt = promptBuilder.build(data);
            
            // 2. Appeler Gemini
            AnalyseLLM analyseLLM = geminiClient.analyser(prompt);
            
            log.info("Analyse LLM reçue avec succès");
            
            // 3. Retourner l'analyse enrichie avec les données de base
            return AnalyseLLM.AnalyseLLMWithData.builder()
                .phaseStructuree(data)
                .analyseLLM(analyseLLM)
                .build();
                
        } catch (Exception e) {
            log.error("Erreur lors de l'enrichissement LLM", e);
            throw new RuntimeException("Erreur enrichissement LLM : " + e.getMessage(), e);
        }
    }
}

