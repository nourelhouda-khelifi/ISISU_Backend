# 🔍 Prompt d'Analyse Complète du Code - ISISU Platform

## CONTEXTE
Tu es un expert senior en code review et architecture logicielle. Ton objectif est de faire une analyse **exhaustive et structurée** du code ISISU (système d'évaluation adaptatif avec recommandations).

---

## 📋 PHASE 1: COMPRÉHENSION GLOBALE

### 1.1 Architecture générale
- [ ] Identifier tous les modules/packages et leurs responsabilités
- [ ] Mapper les dépendances entre services
- [ ] Clarifier le flux de données global (création session → réponse → score → recommandation)
- [ ] Documenter le cycle de vie d'une SessionTest

### 1.2 Entités et domaines
- [ ] Lister toutes les entités principales (SessionTest, ReponseEtudiant, ScoreCompetence, etc.)
- [ ] Vérifier les relations (one-to-many, many-to-many)
- [ ] Identifier les enums (StatutSession, NiveauDifficulte, TypeQuestion, etc.)
- [ ] Confirmer que chaque enum est utilisé correctement partout

### 1.3 Flux métier principal
Pour chaque flux, documenter **TOUS** les cas possibles:

```
FLUX 1: Création Session
├─ Cas normal: utilisateur crée session
├─ Cas edge: utilisateur a déjà une session EN_COURS
├─ Cas timeout: session précédente expirée → auto-close?
└─ Cas erreur: pas de questions disponibles

FLUX 2: Soumettre Réponse
├─ Cas correct answer:
│  ├─ FACILE correct → proposer MOYEN
│  ├─ MOYEN correct → proposer DIFFICILE
│  └─ DIFFICILE correct → prochaine compétence
├─ Cas incorrect answer:
│  ├─ FACILE incorrect → proposer CONFIRMATION FACILE
│  ├─ Confirmation FACILE réussit → continuer MOYEN
│  ├─ Confirmation FACILE échoue → LACUNE confirmée, next
│  ├─ MOYEN/DIFFICILE incorrect → proposer CONFIRMATION mesmo niveau
│  └─ Confirmation échoue → next compétence
├─ Cas aucune question suivante:
│  ├─ Marquer session TERMINEE
│  ├─ Calculer tous les scores
│  └─ Répondre avec sessionTerminated=true
└─ Cas erreur: exception → ?

FLUX 3: Calculer Scores
├─ Cas session TERMINEE:
│  ├─ Grouper réponses par compétence
│  ├─ Pondérer par niveau de difficulté
│  ├─ Calculer score moyen
│  └─ Créer ScoreCompetence records
├─ Cas session pas TERMINEE:
│  └─ Skip score calculation
└─ Cas 0 réponses:
   └─ Scores = 0 ou null?

FLUX 4: Recommandations
├─ Cas scores calculés:
│  ├─ Score > 85 → force, pas action
│  ├─ 50 < score < 85 → "à renforcer"
│  ├─ Score < 50 → critical gap, priorité
│  └─ Détecter dépendances bloquantes (si comp X bloque Y)
├─ Cas 0 scores:
│  └─ Recommandations = [] ou message error?
└─ Cas 1ère session:
   └─ Pas de progression → tendance = N/A
```

---

## 🔧 PHASE 2: ANALYSE DÉTAILLÉE DES SERVICES

### 2.1 SessionTestService

**Responsabilités déclarées:**
1. Créer nouvelle session (ordre modules + pré-sélection questions)
2. Récupérer question courante
3. Traiter réponse + calculer prochaine question
4. Terminer session et calculer scores
5. Gérer timeouts et abandons

**Vérifications:**
- [ ] Chaque méthode a UNE responsabilité claire?
- [ ] `createNewSession()`: Le nombre de questions initial est-il suffisant?
- [ ] `getCurrentSession()`: Timeout checking fonctionne-t-il?
- [ ] `submitAnswer()`: Quand exactement est appelée `terminateSession()`?
- [ ] `terminateSession()`: Calcule-t-elle TOUJOURS scores si statut=TERMINEE?
- [ ] Transaction boundaries correctes? (EntityManager.flush() nécessaire?)

**Cas non couverts:**
- Que se passe-t-il si `algorithmService.analyzeResponseAndGetNextQuestion()` lance exception?
- Que se passe-t-il si `scoringService.calculateAllScores()` lance exception?
- Y a-t-il un rollback automatique?

### 2.2 AlgorithmeAdaptatifService

**Logique déclarée:**
- FACILE ✅ → MOYEN
- MOYEN ✅ → DIFFICILE
- DIFFICILE ✅ → next compétence
- FACILE ❌ → CONFIRMATION FACILE

**Vérifications:**
- [ ] Quelle est la définition exacte de "next Question"?
- [ ] `getNextUnansweredQuestion()` : retourne null si toutes les questions répondues?
- [ ] CONFIRMATION questions: comment sont-elles créées? Sont-elles ajoutées à `session.questionSessions`?
- [ ] Après ajout CONFIRMATION question, est-elle immédiatement retournée ou cherche-t-on la prochaine?
- [ ] Pondération par niveau: un DIFFICILE correct compte-t-il plus qu'un FACILE incorrect?

**Cas edge à tester:**
```
Scénario 1: Étudiant échoue TOUS les FACILE
├─ Q1 FACILE correct? NON
├─ Proposer Q1 CONFIRMATION FACILE
├─ Q1 CONFIRMATION correct? NON
├─ Ajouter à "confirmed lacunes"
└─ Next question de quelle compétence?

Scénario 2: Étudiant réussit FACILE, échoue MOYEN, réussit CONFIRMATION MOYEN
├─ Q1 FACILE ✅ → proposer Q2 MOYEN
├─ Q2 MOYEN ❌ → proposer Q2 CONFIRMATION
├─ Q2 CONFIRMATION ✅ → ???? (pas couverts dans le code)

Scénario 3: Une seule question par compétence
├─ Q1 FACILE ✅ → proposer MOYEN
├─ Mais pas de Q MOYEN disponible
├─ `findQuestionForCompetenceAtLevel()` retourne null
├─ `getNextUnansweredQuestion()` est appelée
└─ Quel est le comportement?
```

### 2.3 ScoringService

**Contrats:**
- Input: SessionTest avec ReponseEtudiant
- Output: List<ScoreCompetence>

**Vérifications:**
- [ ] Formule exacte de calcul du score?
- [ ] Pondération par difficulté: coefficients utilisés?
- [ ] Nombre de tentatives: compris dans le score?
- [ ] Si 0 réponses: score = 0 ou score = null?
- [ ] Si sessionn'est pas TERMINEE: retourne [] ou exception?

### 2.4 RecommendationService

**Données structurées calculées:**
- StudentProfile
- ModuleScores (par module)
- Progression (3 dernières sessions)
- BlockingDependencies
- Strengths (score > 85)
- CriticalGaps (score < 50)

**Vérifications:**
- [ ] CriticalGaps: sont-elles triées par score (celles avec le plus bas score en premier)?
- [ ] BlockingDependencies: comment sont calculées? Quelle est la source de truth pour les dépendances?
- [ ] Progression: si 1ère session, tendance = "DATA_INSUFFISANTES"?
- [ ] Si 0 scores: quelles données doivent sortir?

---

## 🚨 PHASE 3: DÉTECTION D'INCOHÉRENCES

### 3.1 Incohérences Logiques

**À vérifier:**
- [ ] Session peut-elle avoir statut EN_COURS + dateFin (not null)?
- [ ] Un ScoreCompetence peut-il exister sans Session?
- [ ] Peut-on avoir score sans réponses?
- [ ] Si 2 sessions simultanées pour même user: comportement?
- [ ] Une ReponseEtudiant peut-elle avoir statut sans session?

### 3.2 Chemins de code non couverts

```
QUESTION: Que se passe-t-il dans submitAnswer() si:

1. analyzeResponseAndGetNextQuestion() retourne null
   ├─ terminateSession() est appelée
   └─ ✓ Couvert

2. terminateSession(TERMINEE) lance exception
   ├─ Session reste-t-elle EN_COURS?
   ├─ ScoreCompetence records créés partiellement?
   └─ ❓ Non couvert?

3. QuestionSession.estRepondue ne peut pas être mise à jour
   ├─ ReponseEtudiant sauvegardée mais QuestionSession non?
   └─ ❓ Data inconsistency possible

4. calculateAllScores() retourne [] (0 scores)
   ├─ getRecommendations() reçoit session avec 0 scores
   ├─ scoreRepository.findBySession() retourne []
   ├─ computeStructuredData() retourne quoi?
   └─ ❓ DTOs vides ou erreur?
```

### 3.3 Timing et concurrence

```
Scénario: Deux réponses submitées très rapidement

Request 1: submitAnswer(Q1, choix=[1])
Request 2: submitAnswer(Q2, choix=[2])

Les deux peuvent-elles être traitées en parallèle?
├─ Session fetch simultané?
├─ AlgorithmService retourner question différentes?
├─ EntityManager flush correctement?
└─ ❓ Race conditions?
```

### 3.4 Données métier

```
Questions:
- [ ] Une Question peut appartenir à 0 compétence?
- [ ] Une Question peut belong à 3+ compétences?
- [ ] Tous les Choix ont-ils un "bonChoix" flag?
- [ ] Un Choix peut-il être dans 2+ Questions?

Compétences:
- [ ] Une compétence peut avoir 0 questions FACILE?
- [ ] Une compétence peut avoir des lacunes détectées sans questions FACILE?
- [ ] Les dépendances de compétences: table exists?
```

---

## ✅ PHASE 4: CHECKLIST DE VALIDATION

### 4.1 Transactions et Persistence

- [ ] Toutes les écritures en base sont dans des méthodes @Transactional?
- [ ] EntityManager.flush() utilisé aux bons endroits?
- [ ] Quid des rollbacks en cas d'erreur?
- [ ] LazyLoading issues possibles?

### 4.2 Null Safety

- [ ] Tous les .get().orElse() correctement gérés?
- [ ] .stream().findFirst() retirer null ou objet?
- [ ] Question/Competence parfois null?

### 4.3 Validations Input

- [ ] ReponseEtudiant: choixIds peut-il être null ou []?
- [ ] ValidationAnswer: suffisant?
- [ ] EvaluateAnswer: tous les types de questions gérés?

### 4.4 Logging & Debugging

- [ ] LogLevel suffisant pour tracer un bug?
- [ ] sessionId/userId loggés partout?
- [ ] Erreurs silencieusement catch?

---

## 📊 PHASE 5: RAPPORT D'INCOHÉRENCES

**Format du rapport:**

```
CRITIQUE (BLOCKING):
- [ID] Description
  Fichier: path
  Ligne: N
  Impact: Quel est le risque?
  Fix: Comment corriger?

MAJEURE (DOIT être fixé):
- [ID] Description
  ...

MINEURE (Amélioration):
- [ID] Description
  ...

NON-ISSUE (OK):
- [ID] Description (pourquoi c'est OK?)
  ...
```

---

## 🎯 SORTIE ATTENDUE

Fournir:
1. **Cartographie complète**: Tous les flows possible (tableau)
2. **Incohérences détectées**: Classées par sévérité
3. **Cas non couverts**: Tests manquants
4. **Recommendations**: Améliorations suggérées
5. **Code snippets**: Exemples des problèmes
