package com.example.demo.recommendation.service;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.auth.infrastructure.repository.UtilisateurRepository;
import com.example.demo.evaluation.domain.ScoreCompetence;
import com.example.demo.evaluation.domain.SessionTest;
import com.example.demo.evaluation.repository.ScoreCompetenceRepository;
import com.example.demo.evaluation.repository.SessionTestRepository;
import com.example.demo.recommendation.dto.*;
import com.example.demo.referentiel.domain.Competence;
import com.example.demo.referentiel.domain.ModuleFIE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service pour générer des recommandations cross-session
 * Agrège TOUTES les sessions d'un étudiant pour une vision holistique
 */
@Slf4j
@Service
public class CrossSessionRecommendationService {
    
    private final SessionTestRepository sessionRepository;
    private final ScoreCompetenceRepository scoreRepository;
    private final UtilisateurRepository userRepository;
    
    public CrossSessionRecommendationService(
            SessionTestRepository sessionRepository,
            ScoreCompetenceRepository scoreRepository,
            UtilisateurRepository userRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Générer recommandation cross-session pour un utilisateur par ID
     */
    public CrossSessionRecommendationDTO generateCrossSessionRecommendations(Long userId) {
        log.info("Generating cross-session recommendations for user {}", userId);
        
        // 1. Fetch user
        Utilisateur user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        // 2. Fetch ALL sessions sorted by date (ascending = oldest to newest)
        List<SessionTest> allSessions = sessionRepository.findByUtilisateurOrderByDateDebutDesc(user);
        // Reverse to get oldest first
        Collections.reverse(allSessions);
        
        // 3. Aggregate competence timeline across sessions
        Map<Competence, List<ScoreHistory>> competenceTimeline = aggregateCompetenceScores(allSessions);
        
        // 4. Calculate trends
        Map<Competence, TrendAnalysis> trendAnalysis = analyzeTrends(competenceTimeline);
        
        // 5. Build prioritized recommendations
        List<PrioritizedRecommendationDTO> recommendations = buildPrioritizedRecommendations(
            competenceTimeline,
            trendAnalysis,
            user
        );
        
        // 6. Build overall metrics
        OverallMetricsDTO metrics = calculateOverallMetrics(allSessions, competenceTimeline);
        
        // 7. Build path forward
        PathForwardDTO pathForward = buildPathForward(recommendations, allSessions);
        
        // 8. Extract key insights
        List<String> keyInsights = extractKeyInsights(competenceTimeline, trendAnalysis, metrics);
        
        return CrossSessionRecommendationDTO.builder()
            .studentProfile(buildStudentProfile(user, allSessions))
            .competenceEvolution(buildCompetenceEvolutionList(competenceTimeline, trendAnalysis))
            .recommendations(recommendations)
            .pathForward(pathForward)
            .metrics(metrics)
            .keyInsights(keyInsights)
            .build();
    }
    
    // ════════════════════════════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ════════════════════════════════════════════════════════════════════════════════
    
    private Map<Competence, List<ScoreHistory>> aggregateCompetenceScores(List<SessionTest> sessions) {
        Map<Competence, List<ScoreHistory>> timeline = new HashMap<>();
        
        for (int i = 0; i < sessions.size(); i++) {
            SessionTest session = sessions.get(i);
            List<ScoreCompetence> scores = scoreRepository.findBySession(session);
            
            for (ScoreCompetence score : scores) {
                Competence comp = score.getCompetence();
                timeline.computeIfAbsent(comp, k -> new ArrayList<>())
                    .add(new ScoreHistory(
                        i + 1,
                        score.getScoreObtenu() * 100, // Normalize to 0-100
                        session.getDateDebut(),
                        score.getStatut().name()
                    ));
            }
        }
        
        return timeline;
    }
    
    private Map<Competence, TrendAnalysis> analyzeTrends(
            Map<Competence, List<ScoreHistory>> competenceTimeline
    ) {
        Map<Competence, TrendAnalysis> analysis = new HashMap<>();
        
        for (Map.Entry<Competence, List<ScoreHistory>> entry : competenceTimeline.entrySet()) {
            Competence comp = entry.getKey();
            List<ScoreHistory> history = entry.getValue();
            
            if (history.size() < 2) {
                analysis.put(comp, TrendAnalysis.builder()
                    .trend("NOT_ENOUGH_DATA")
                    .velocity(0.0)
                    .build());
                continue;
            }
            
            // Calculate velocity (score change per session)
            double firstScore = history.get(0).score;
            double lastScore = history.get(history.size() - 1).score;
            double velocity = (lastScore - firstScore) / (history.size() - 1);
            
            // Detect trend
            String trend = detectTrend(history, velocity);
            
            // Calculate acceleration
            double acceleration = 0;
            if (history.size() >= 3) {
                double v1 = history.get(1).score - history.get(0).score;
                double v2 = history.get(2).score - history.get(1).score;
                acceleration = v2 - v1;
            }
            
            analysis.put(comp, TrendAnalysis.builder()
                .trend(trend)
                .velocity(velocity)
                .acceleration(acceleration)
                .build());
        }
        
        return analysis;
    }
    
    private String detectTrend(List<ScoreHistory> history, double velocity) {
        List<Double> scores = history.stream().map(h -> h.score).collect(Collectors.toList());
        
        if (velocity > 20) return "MOMENTUM"; // Strong growth
        if (velocity > 5) return "PROGRESSION"; // Gradual growth
        if (Math.abs(velocity) < 5) return "STAGNATION"; // Flat
        if (velocity < -20) return "REGRESSION"; // Significant drop
        return "VARIABLE";
    }
    
    private List<PrioritizedRecommendationDTO> buildPrioritizedRecommendations(
            Map<Competence, List<ScoreHistory>> timeline,
            Map<Competence, TrendAnalysis> trends,
            Utilisateur user
    ) {
        List<PrioritizedRecommendationDTO> recs = new ArrayList<>();
        
        for (Map.Entry<Competence, List<ScoreHistory>> entry : timeline.entrySet()) {
            Competence comp = entry.getKey();
            List<ScoreHistory> history = entry.getValue();
            TrendAnalysis trend = trends.get(comp);
            
            double currentScore = history.isEmpty() ? 0 : history.get(history.size() - 1).score;
            
            // Prioritize
            String priority = prioritizeCompetence(comp, currentScore, trend);
            
            // Extract blocking info (simplified - no direct relationship)
            List<String> blocking = new ArrayList<>();
            
            recs.add(PrioritizedRecommendationDTO.builder()
                .priority(priority)
                .competence(comp.getIntitule())
                .moduleName(comp.getModule() != null ? comp.getModule().getNom() : "N/A")
                .moduleId(comp.getModule() != null ? comp.getModule().getId() : null)
                .moduleCode(comp.getModule() != null ? comp.getModule().getCode() : null)
                .status(trend.trend)
                .currentScore(currentScore)
                .trend(trend.trend)
                .timelineReason(generateTimelineReason(comp, currentScore, trend, blocking))
                .actions(generateActions(comp, currentScore, trend))
                .nextSteps(generateNextSteps(comp, currentScore, trend))
                .sessionsToMastery(estimateSessionsToMastery(currentScore, trend.velocity))
                .blockingOtherModules(blocking)
                .build());
        }
        
        // Sort by priority
        recs.sort((a, b) -> comparePriority(a.getPriority(), b.getPriority()));
        return recs;
    }
    
    private String prioritizeCompetence(Competence comp, double score, TrendAnalysis trend) {
        // Simplified prioritization (no direct blocking relationship in DB)
        if (trend.trend.equals("REGRESSION") || score < 50) {
            return "P1-CRITICAL";
        }
        if (score < 70) {
            return "P2-HIGH";
        }
        if (trend.trend.equals("STAGNATION") && score < 85) {
            return "P2-HIGH";
        }
        if (trend.trend.equals("MOMENTUM")) {
            return "P3-MEDIUM";
        }
        return "P4-OPTIONAL";
    }
    
    private String generateTimelineReason(Competence comp, double score, TrendAnalysis trend, List<String> blocking) {
        if (!blocking.isEmpty() && score < 70) {
            return String.format("Compétence clé bloquant %d modules - Priorité immédiate", blocking.size());
        }
        if (trend.trend.equals("REGRESSION")) {
            double drop = Math.abs(trend.velocity);
            return String.format("Régression détectée (-%.1f%% entre sessions) - C'est un signal pour changer de stratégie", drop);
        }
        if (trend.trend.equals("STAGNATION")) {
            return "Score stable depuis plusieurs sessions - Testez une nouvelle approche (questions différentes, rythme plus intensif)";
        }
        if (trend.trend.equals("MOMENTUM")) {
            return String.format("Progression excellente (+%.1f%% par session) - Maintenez cette cadence!", trend.velocity);
        }
        return "Progression régulière - Restez constant";
    }
    
    private List<String> generateActions(Competence comp, double score, TrendAnalysis trend) {
        List<String> actions = new ArrayList<>();
        
        if (score < 50) {
            actions.add(String.format("1️⃣ Recommencer par les bases de '%s' (questions fondamentales)", comp.getIntitule()));
            actions.add("2️⃣ Revoir les concepts clés dans le module - 20 min de théorie");
            actions.add("3️⃣ Pratiquer 3-4 questions faciles d'abord (avant les difficiles)");
            actions.add("4️⃣ Noter les pièges courants pour éviter les mêmes erreurs");
        } else if (score < 75) {
            actions.add(String.format("1️⃣ Pratiquer 10 questions ciblées sur les points faibles de '%s'", comp.getIntitule()));
            actions.add("2️⃣ Revoir votre feedback de la dernière session - répéter les erreurs?");
            actions.add("3️⃣ Alterner questions faciles/dures pour renforcer la confiance");
            actions.add("4️⃣ Cherchez le 'pattern' - qu'est-ce qui vous fait échouer?");
        } else if (score < 90) {
            actions.add(String.format("1️⃣ Consolider avec les questions avancées de '%s'", comp.getIntitule()));
            actions.add("2️⃣ Tester vos connaissances sur les cas limites et exceptions");
            actions.add("3️⃣ Vous êtes proche! 10-15 points pour la maîtrise complète");
            actions.add("4️⃣ Aider les autres peut renforcer votre compréhension");
        } else {
            actions.add(String.format("✅ Vous maîtrisez '%s'! Bravo!", comp.getIntitule()));
            actions.add("🚀 Prêt pour les modules dépendants - changez de domaine maintenant");
            actions.add("⭐ Optionnel: Explorez les cas avancés pour renforcer encore plus");
        }
        
        return actions;
    }
    
    private String generateNextSteps(Competence comp, double score, TrendAnalysis trend) {
        if (score >= 90) {
            return "✅ Niveau de maîtrise atteint! Passez aux modules qui dépendent de cette compétence - nouvelle étape débloquée!";
        }
        if (trend.velocity > 10) {
            return String.format("🚀 Excellent rythme! Vous progressez de %.1f%% par session - continuez cette cadence. Maîtrise prévue dans ~%d sessions.",
                trend.velocity, estimateSessionsToMastery(score, trend.velocity) == null ? 0 : estimateSessionsToMastery(score, trend.velocity));
        }
        if (trend.velocity > 0) {
            return String.format("📈 Vous êtes sur la bonne route - progression de %.1f%% par session. Accélérez un peu pour raccourcir le chemin à la maîtrise.", trend.velocity);
        }
        if (trend.trend.equals("STAGNATION")) {
            return "🔄 Changez votre stratégie - essayez des questions différentes, un rythme plus intensif, ou une approche plus théorique d'abord.";
        }
        if (trend.trend.equals("REGRESSION")) {
            return "💡 Rebond possible! Une régression est souvent suivie d'une progression - intensifiez la pratique prochaine session.";
        }
        return "🎯 Concentrez-vous lors de la prochaine session - vous avez les capacités pour rebondir!";
    }
    
    private Integer estimateSessionsToMastery(double currentScore, double velocity) {
        if (currentScore >= 90) return 0;
        if (velocity <= 0) return null; // Unknown
        
        double remaining = 90 - currentScore;
        return (int) Math.ceil(remaining / velocity);
    }
    
    private int comparePriority(String p1, String p2) {
        int order1 = extractPriorityOrder(p1);
        int order2 = extractPriorityOrder(p2);
        return order1 - order2;
    }
    
    private int extractPriorityOrder(String priority) {
        switch (priority) {
            case "P1-CRITICAL": return 1;
            case "P2-HIGH": return 2;
            case "P3-MEDIUM": return 3;
            case "P4-OPTIONAL": return 4;
            default: return 5;
        }
    }
    
    private OverallMetricsDTO calculateOverallMetrics(
            List<SessionTest> sessions,
            Map<Competence, List<ScoreHistory>> timeline
    ) {
        double avgScore = timeline.values().stream()
            .flatMap(List::stream)
            .mapToDouble(h -> h.score)
            .average()
            .orElse(0);
        
        return OverallMetricsDTO.builder()
            .globalScore(avgScore)
            .globalTrend(avgScore > 75 ? "POSITIVE" : "NEEDS_IMPROVEMENT")
            .averageVelocity(0.0) // TODO: Calculate
            .totalSessionsDuration(sessions.size() * 5) // Estimate
            .estimatedTimeToCompletion(estimateTimeToCompletion(avgScore))
            .build();
    }
    
    private Integer estimateTimeToCompletion(double avgScore) {
        if (avgScore >= 90) return 2;
        if (avgScore >= 80) return 4;
        if (avgScore >= 70) return 8;
        return 12;
    }
    
    private PathForwardDTO buildPathForward(
            List<PrioritizedRecommendationDTO> recommendations,
            List<SessionTest> sessions
    ) {
        String nextFocus = recommendations.isEmpty() ? "General practice" :
            recommendations.stream()
                .filter(r -> "P1-CRITICAL".equals(r.getPriority()))
                .map(PrioritizedRecommendationDTO::getCompetence)
                .limit(2)
                .collect(Collectors.joining(", "));
        
        return PathForwardDTO.builder()
            .nextSessionFocus("Focus: " + nextFocus)
            .sessionNplus1Focus("Consolidation and advancement")
            .estimatedTimeline(String.format("%d more sessions recommended", Math.max(2, 5 - sessions.size())))
            .suggestedModuleSequence(Arrays.asList("Current focus", "Then advance to dependent modules"))
            .overallStrategy("Systematic progression with consolidation")
            .build();
    }
    
    private List<String> extractKeyInsights(
            Map<Competence, List<ScoreHistory>> timeline,
            Map<Competence, TrendAnalysis> trends,
            OverallMetricsDTO metrics
    ) {
        List<String> insights = new ArrayList<>();
        
        // Analyze strengths - counting from timeline since TrendAnalysis doesn't have score
        long strengths = 0;
        long weaknesses = 0;
        long criticalRegressions = 0;
        
        for (Map.Entry<Competence, List<ScoreHistory>> entry : timeline.entrySet()) {
            List<ScoreHistory> history = entry.getValue();
            TrendAnalysis trend = trends.get(entry.getKey());
            double currentScore = history.isEmpty() ? 0 : history.get(history.size() - 1).score;
            
            if (trend.trend.equals("MOMENTUM") || (trend.trend.equals("STAGNATION") && currentScore >= 85)) {
                strengths++;
            }
            if (trend.trend.equals("REGRESSION") && currentScore < 50) {
                criticalRegressions++;
            }
            if (trend.trend.equals("REGRESSION") || currentScore < 50) {
                weaknesses++;
            }
        }
        
        // Find best performer
        Optional<Map.Entry<Competence, TrendAnalysis>> best = trends.entrySet().stream()
            .filter(e -> e.getValue().velocity > 0)
            .max(Comparator.comparingDouble(e -> e.getValue().velocity));
        
        // Build motivating insights
        if (strengths > 0) {
            insights.add(String.format("✅ Points forts: %d compétence(s) maîtrisée(s) - Continuez cet élan!", strengths));
        }
        
        if (criticalRegressions > 0) {
            insights.add(String.format("⚠️  À renforcer d'urgence: %d compétence(s) en régression critique - Un rebond rapide est possible!", criticalRegressions));
        } else if (weaknesses > 0) {
            insights.add(String.format("📈 À travailler: %d compétence(s) - C'est normal, vous êtes en apprentissage!", weaknesses));
        }
        
        best.ifPresent(entry -> {
            TrendAnalysis bestTrend = entry.getValue();
            insights.add(String.format("🌟 Meilleure progression: %s (+%.1f%% par session) - C'est cet élan qu'il faut généraliser!",
                entry.getKey().getIntitule(), bestTrend.velocity));
        });
        
        // Overall motivation
        String globalMessage;
        if (metrics.getGlobalScore() >= 85) {
            globalMessage = String.format("🏆 Excellent! Note globale: %.0f%% - Vous êtes en très bonne voie!", metrics.getGlobalScore());
        } else if (metrics.getGlobalScore() >= 75) {
            globalMessage = String.format("💪 Bon rythme! Note globale: %.0f%% - Quelques ajustements et ce sera parfait!", metrics.getGlobalScore());
        } else {
            globalMessage = String.format("🎯 Note globale: %.0f%% - Intensifiez vos sessions, vous progresserez rapidement!", metrics.getGlobalScore());
        }
        insights.add(globalMessage);
        
        return insights;
    }
    
    private StudentInfoDTO buildStudentProfile(Utilisateur user, List<SessionTest> sessions) {
        return StudentInfoDTO.builder()
            .id(user.getId())
            .niveau(user.getRole().name())
            .parcours(user.getParcoursOrigine() != null ? user.getParcoursOrigine().name() : "Unknown")
            .nbSessions(sessions.size())
            .firstSessionDate(sessions.isEmpty() ? null : sessions.get(0).getDateDebut())
            .lastSessionDate(sessions.isEmpty() ? null : sessions.get(sessions.size() - 1).getDateDebut())
            .build();
    }
    
    private List<CompetenceTimelineDTO> buildCompetenceEvolutionList(
            Map<Competence, List<ScoreHistory>> timeline,
            Map<Competence, TrendAnalysis> trends
    ) {
        List<CompetenceTimelineDTO> evolution = new ArrayList<>();
        
        for (Map.Entry<Competence, List<ScoreHistory>> entry : timeline.entrySet()) {
            Competence comp = entry.getKey();
            List<ScoreHistory> history = entry.getValue();
            TrendAnalysis trend = trends.get(comp);
            
            List<SessionScorePointDTO> points = history.stream()
                .map(h -> SessionScorePointDTO.builder()
                    .sessionNum(h.sessionNum)
                    .score(h.score)
                    .date(h.date)
                    .status(h.status)
                    .build())
                .collect(Collectors.toList());
            
            long above80 = history.stream().filter(h -> h.score >= 80).count();
            long below50 = history.stream().filter(h -> h.score < 50).count();
            
            evolution.add(CompetenceTimelineDTO.builder()
                .competenceName(comp.getIntitule())
                .moduleName(comp.getModule() != null ? comp.getModule().getNom() : "N/A")
                .moduleId(comp.getModule() != null ? comp.getModule().getId() : null)
                .moduleCode(comp.getModule() != null ? comp.getModule().getCode() : null)
                .scores(points)
                .trend(trend.trend)
                .velocity(trend.velocity)
                .acceleration(trend.acceleration)
                .sessionCountAbove80((int) above80)
                .sessionCountBelow50((int) below50)
                .build());
        }
        
        return evolution;
    }
    
    private CrossSessionRecommendationDTO buildEmptyRecommendation(Utilisateur user) {
        return CrossSessionRecommendationDTO.builder()
            .studentProfile(StudentInfoDTO.builder()
                .id(user.getId())
                .niveau(user.getRole().name())
                .nbSessions(0)
                .build())
            .keyInsights(List.of("No sessions yet. Start your first assessment session!"))
            .competenceEvolution(new ArrayList<>())
            .recommendations(new ArrayList<>())
            .pathForward(PathForwardDTO.builder()
                .nextSessionFocus("Take your first assessment")
                .estimatedTimeline("2-3 sessions recommended to establish baseline")
                .build())
            .metrics(OverallMetricsDTO.builder()
                .globalScore(0.0)
                .globalTrend("STARTING")
                .build())
            .build();
    }
    
    // ════════════════════════════════════════════════════════════════════════════════
    // INTERNAL CLASSES
    // ════════════════════════════════════════════════════════════════════════════════
    
    private static class ScoreHistory {
        int sessionNum;
        double score;
        LocalDateTime date;
        String status;
        
        ScoreHistory(int sessionNum, double score, LocalDateTime date, String status) {
            this.sessionNum = sessionNum;
            this.score = score;
            this.date = date;
            this.status = status;
        }
    }
    
    @lombok.Data
    @lombok.Builder
    private static class TrendAnalysis {
        String trend;
        double velocity;
        double acceleration;
    }
}