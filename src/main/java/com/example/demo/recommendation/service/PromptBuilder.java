package com.example.demo.recommendation.service;

import com.example.demo.recommendation.dto.BlockingDependency;
import com.example.demo.recommendation.dto.CriticalGap;
import com.example.demo.recommendation.dto.ModuleScore;
import com.example.demo.recommendation.dto.RecommendationData;
import com.example.demo.recommendation.dto.StrengthPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Construit les prompts structurés pour l'API Gemini
 * Transforme les RecommendationData (algo manuel, PHASE 1)
 * en prompt naturel pour le LLM (PHASE 2)
 */
@Component
@Slf4j
public class PromptBuilder {

    /**
     * Construit le prompt complet pour Gemini
     * Entrée: RecommendationData (résultat algorithme manuel)
     * Sortie: String prompt pour Gemini
     *
     * @param data Les données structurées de la PHASE 1
     * @return Le prompt formaté pour Gemini
     */
    public String build(RecommendationData data) {
        log.debug("Construction du prompt pour Gemini");

        String prompt = """
            Tu es un conseiller pédagogique expert pour ISIS,
            école d'ingénieurs en santé numérique à Castres.

            Voici les résultats d'évaluation d'un étudiant :

            PROFIL :
              Niveau : %s
              Parcours d'origine : %s
              Nombre de sessions : %d

            SCORES PAR MODULE :
            %s

            PROGRESSION :
              Tendance : %s
              Vélocité : %s

            LACUNES CRITIQUES (modules < 50%%) :
            %s

            DÉPENDANCES BLOQUANTES :
            %s

            POINTS FORTS (modules > 85%%) :
            %s

            %s

            RÈGLES STRICTES :
            - Répondre UNIQUEMENT en JSON valide
            - Ne pas inventer de ressources externes
            - Baser l'analyse UNIQUEMENT sur les données fournies
            - Rester factuel, bienveillant et encourageant
            - Maximum 3 priorités dans la liste
            - Utiliser le français

            Retourner ce JSON exactement :
            {
              "messagePersonnalise": "message d'accueil personnalisé 2-3 phrases",
              "analysePrincipale": "analyse globale du profil 3-4 phrases",
              "priorites": [
                {
                  "module": "nom du module",
                  "urgence": "CRITIQUE|HAUTE|MOYENNE",
                  "raison": "pourquoi ce module est prioritaire",
                  "conseil": "conseil concret et actionnable"
                }
              ],
              "parcourRecommande": "nom du parcours FIE4 ou null si VAE",
              "raisonParcours": "pourquoi ce parcours lui correspond",
              "messageMotivation": "message de motivation personnalisé"
            }
            """.formatted(
                data.getStudentProfile().getNiveau(),
                data.getStudentProfile().getParcours(),
                data.getStudentProfile().getNbSessions(),
                formatModuleScores(data.getScoresByModule()),
                data.getProgression().getTendance(),
                data.getProgression().getVelocite(),
                formatCriticalGaps(data.getCriticalGaps()),
                formatBlockingDeps(data.getBlockingDependencies()),
                formatStrengths(data.getStrengths()),
                buildParcoursSection(data)
            );

        log.debug("Prompt construit, longueur: {} caractères", prompt.length());
        return prompt;
    }

    /**
     * Construit la section parcours FIE4 selon le niveau de l'étudiant
     */
    private String buildParcoursSection(RecommendationData data) {
        // Vérifier si l'étudiant est en FIE3 (pour proposer FIE4)
        String niveau = data.getStudentProfile().getNiveau();

        if (niveau != null && niveau.contains("FIE3")) {
            return """
                SCORES PARCOURS FIE4 :
                  Calculés à partir des scores par module
                  Approches: Développement, IA & Big Data, Management
                """;
        }

        // Pour les VAE ou autres niveaux
        return "NOTE : Vérifier le statut d'admissibilité en FIE4 — ne pas recommander automatiquement";
    }

    /**
     * Formate les scores par module pour lisibilité
     * Ex: "BDD : 45% (LACUNE)"
     */
    private String formatModuleScores(List<ModuleScore> scores) {
        if (scores == null || scores.isEmpty()) {
            return "  Aucun score disponible";
        }

        return scores.stream()
            .map(s -> "  %s : %d%% (%s)".formatted(
                s.getModule(),
                s.getScore(),
                s.getStatus()))
            .collect(Collectors.joining("\n"));
    }

    /**
     * Formate les lacunes critiques pour lisibilité
     * Ex: "BDD : 45% — Prérequis de 8 modules"
     */
    private String formatCriticalGaps(List<CriticalGap> gaps) {
        if (gaps == null || gaps.isEmpty()) {
            return "  ✓ Aucune lacune critique détectée";
        }

        return gaps.stream()
            .map(g -> "  ⚠ %s : %.0f%% — %s".formatted(
                g.getModule(),
                g.getScore(),
                g.getRaison()))
            .collect(Collectors.joining("\n"));
    }

    /**
     * Formate les dépendances bloquantes pour lisibilité
     * Ex: "OOP (30%) bloque : Génie Logiciel, Gestion Projet (CRITIQUE)"
     */
    private String formatBlockingDeps(Map<String, BlockingDependency> deps) {
        if (deps == null || deps.isEmpty()) {
            return "  ✓ Aucune dépendance bloquante";
        }

        return deps.entrySet().stream()
            .map(e -> "  🔴 %s bloque : %s (sévérité: %s)".formatted(
                e.getKey(),
                String.join(", ", e.getValue().getBloque()),
                e.getValue().getSeverite()))
            .collect(Collectors.joining("\n"));
    }

    /**
     * Formate les points de force pour lisibilité
     * Ex: "Web : 92%"
     */
    private String formatStrengths(List<StrengthPoint> strengths) {
        if (strengths == null || strengths.isEmpty()) {
            return "  Aucun point fort identifié (> 85%)";
        }

        return strengths.stream()
            .map(s -> "  ✓ %s : %.0f%%".formatted(
                s.getModule(),
                s.getScore()))
            .collect(Collectors.joining("\n"));
    }
}
