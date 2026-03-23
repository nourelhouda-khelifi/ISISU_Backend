package com.example.demo.questions.domain.enums;

/**
 * Niveau de difficulté d'une question.
 *
 * FACILE    → concepts de base, mémorisation
 *             Pondération score : 1.0
 *
 * MOYEN     → compréhension et application
 *             Pondération score : 1.5
 *
 * DIFFICILE → analyse, synthèse, cas complexes
 *             Pondération score : 2.0
 *
 * La pondération est utilisée par l'algorithme d'évaluation :
 * une bonne réponse à une question DIFFICILE vaut plus
 * qu'une bonne réponse à une question FACILE.
 */
public enum NiveauDifficulte {
    FACILE,
    MOYEN,
    DIFFICILE;

    /**
     * Pondération utilisée dans le calcul du score.
     */
    public double getPonderation() {
        return switch (this) {
            case FACILE    -> 1.0;
            case MOYEN     -> 1.5;
            case DIFFICILE -> 2.0;
        };
    }
}
