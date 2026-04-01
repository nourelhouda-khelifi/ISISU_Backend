package com.example.demo.evaluation.domain.enums;

/**
 * Statut d'une compétence après évaluation
 * 
 * Basé sur le niveau atteint, pas uniquement sur le score en %
 */
public enum StatutCompetence {
    /**
     * LACUNE: 2 échecs FACILE confirmés
     * Score: 0%
     * Indication: Besoin d'aide fondamentale
     */
    LACUNE("Lacune - Besoin d'aide fondamentale", 0.0),
    
    /**
     * A_RENFORCER: Réussi FACILE mais échoué MOYEN
     * Score: 20% à 50%
     * Indication: Renforcer les bases
     */
    A_RENFORCER("À renforcer - Bases fragiles", 0.5),
    
    /**
     * ACQUIS: Réussi MOYEN mais échoué DIFFICILE
     * Score: 50% à 80%
     * Indication: Compétence acquise mais sans maîtrise complète
     */
    ACQUIS("Acquis - Compétence validée", 0.75),
    
    /**
     * MAITRISE: Réussi FACILE, MOYEN et DIFFICILE
     * Score: 80% à 100%
     * Indication: Compétence maîtrisée
     */
    MAITRISE("Maîtrise - Compétence entièrement validée", 1.0);
    
    private final String description;
    private final double scoreMinimum;
    
    StatutCompetence(String description, double scoreMinimum) {
        this.description = description;
        this.scoreMinimum = scoreMinimum;
    }
    
    public String getDescription() {
        return description;
    }
    
    public double getScoreMinimum() {
        return scoreMinimum;
    }
}
