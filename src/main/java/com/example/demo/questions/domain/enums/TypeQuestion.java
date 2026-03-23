package com.example.demo.questions.domain.enums;

/**
 * Types de questions disponibles dans la banque ISIS-U.
 *
 * QCM_SIMPLE   → 1 seule bonne réponse parmi 4 choix
 * QCM_MULTIPLE → plusieurs bonnes réponses parmi 4 choix
 * VRAI_FAUX    → 2 choix uniquement : Vrai ou Faux
 * APPARIEMENT  → relier colonne A à colonne B (paires)
 * ORDRE        → remettre des éléments dans le bon ordre
 * TEXTE_TROU   → compléter une phrase ou du code
 */
public enum TypeQuestion {
    QCM_SIMPLE,
    QCM_MULTIPLE,
    VRAI_FAUX,
    APPARIEMENT,
    ORDRE,
    TEXTE_TROU
}
