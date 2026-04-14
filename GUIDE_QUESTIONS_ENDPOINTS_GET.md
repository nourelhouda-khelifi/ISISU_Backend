# 📖 GUIDE - ENDPOINTS GET QUESTIONS POUR ADMIN

> **Les endpoints GET existent déjà dans QuestionController**
> Accessibles avec Bearer token (ADMIN, ETUDIANT_FIE3, CANDIDAT_VAE)

---

## 🎯 Endpoints GET Disponibles

### 1️⃣ Récupérer TOUTES les questions (actives)

```
GET /api/v1/questions
Authorization: Bearer <token>
```

**Response: 200 OK**
```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Questions retrieved",
  "data": [
    {
      "id": 1,
      "enonce": "Quelle est la meilleure pratique Java?",
      "type": "QCM_SIMPLE",
      "difficulte": "MOYEN",
      "ponderation": 1.5,
      "dureeSecondes": 60,
      "actif": true,  // ← État visible pour admin
      "dateCreation": "2024-04-14T10:00:00",
      "competenceIds": [1, 2, 3],
      "choix": [
        {
          "id": 10,
          "contenu": "Réponse A",
          "ordre": 1
          // ATTENTION: estCorrect JAMAIS retourné
        }
      ]
    },
    // ... autres questions
  ]
}
```

**Code Frontend:**
```javascript
async function loadAllQuestions() {
  const token = localStorage.getItem('adminToken');
  
  try {
    const response = await fetch(
      'http://localhost:8080/api/v1/questions',
      {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );

    if (!response.ok) throw new Error('Failed to load questions');

    const result = await response.json();
    const questions = result.data;  // ← Array de QuestionDTO
    
    console.log(`${questions.length} questions actives chargées`);
    return questions;

  } catch (error) {
    console.error('Erreur:', error.message);
    return [];
  }
}
```

---

### 2️⃣ Récupérer une question par ID

```
GET /api/v1/questions/{id}
Authorization: Bearer <token>
```

**Example:**
```
GET http://localhost:8080/api/v1/questions/150
```

**Response: 200 OK**
```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Question retrieved",
  "data": {
    "id": 150,
    "enonce": "Quelle est la meilleure pratique Java?",
    "type": "QCM_SIMPLE",
    "difficulte": "MOYEN",
    "ponderation": 1.5,
    "dureeSecondes": 60,
    "actif": true,
    "dateCreation": "2024-04-14T10:00:00",
    "competenceIds": [1, 2, 3],
    "choix": [...]
  }
}
```

**Code Frontend:**
```javascript
async function getQuestionById(questionId) {
  const token = localStorage.getItem('adminToken');
  
  const response = await fetch(
    `http://localhost:8080/api/v1/questions/${questionId}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );

  const result = await response.json();
  return result.data;  // ← QuestionDTO unique
}
```

---

### 3️⃣ Récupérer questions filtrées par TYPE

```
GET /api/v1/questions/type/{type}
Authorization: Bearer <token>
```

**Types disponibles:**
- `QCM_SIMPLE` - QCM avec 1 seule bonne réponse
- `QCM_MULTIPLE` - QCM avec plusieurs bonnes réponses
- `VRAI_FAUX` - Vrai ou Faux
- `REPONSE_LIBRE` - Réponse libre (texte)

**Exemple:**
```
GET http://localhost:8080/api/v1/questions/type/QCM_SIMPLE
GET http://localhost:8080/api/v1/questions/type/VRAI_FAUX
```

**Response: 200 OK - Array de questions du type spécifié**
```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Questions of type QCM_SIMPLE",
  "data": [
    { /* Question 1 */ },
    { /* Question 2 */ },
    // ... autres QCM_SIMPLE
  ]
}
```

**Code Frontend:**
```javascript
async function getQuestionsByType(typeQuestion) {
  const token = localStorage.getItem('adminToken');
  
  const validTypes = [
    'QCM_SIMPLE',
    'QCM_MULTIPLE', 
    'VRAI_FAUX',
    'REPONSE_LIBRE'
  ];

  if (!validTypes.includes(typeQuestion)) {
    throw new Error(`Type invalide: ${typeQuestion}`);
  }

  const response = await fetch(
    `http://localhost:8080/api/v1/questions/type/${typeQuestion}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );

  const result = await response.json();
  return result.data;  // ← Questions du type spécifié
}

// Utilisation
const qcmSimples = await getQuestionsByType('QCM_SIMPLE');
const vraiFaux = await getQuestionsByType('VRAI_FAUX');
```

---

### 4️⃣ Récupérer questions filtrées par DIFFICULTÉ

```
GET /api/v1/questions/difficulte/{difficulte}
Authorization: Bearer <token>
```

**Difficultés disponibles:**
- `FACILE` - Pondération: 1.0
- `MOYEN` - Pondération: 1.5
- `DIFFICILE` - Pondération: 2.0

**Exemple:**
```
GET http://localhost:8080/api/v1/questions/difficulte/FACILE
GET http://localhost:8080/api/v1/questions/difficulte/DIFFICILE
```

**Response: 200 OK - Array de questions du niveau spécifié**

**Code Frontend:**
```javascript
async function getQuestionsByDifficulty(difficulty) {
  const token = localStorage.getItem('adminToken');
  
  const validDifficulties = ['FACILE', 'MOYEN', 'DIFFICILE'];

  if (!validDifficulties.includes(difficulty)) {
    throw new Error(`Difficulté invalide: ${difficulty}`);
  }

  const response = await fetch(
    `http://localhost:8080/api/v1/questions/difficulte/${difficulty}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );

  const result = await response.json();
  return result.data;  // ← Questions du niveau spécifié
}

// Utilisation
const faciles = await getQuestionsByDifficulty('FACILE');
const difficiles = await getQuestionsByDifficulty('DIFFICILE');
```

---

### 5️⃣ Récupérer questions d'une compétence spécifique

```
GET /api/v1/questions/competence/{competenceId}
Authorization: Bearer <token>
```

**Exemple:**
```
GET http://localhost:8080/api/v1/questions/competence/5
```

**Response: 200 OK - Questions liées à cette compétence**

---

## 🎭 Utilisation pour Interface Admin

### Scénario 1: Afficher le tableau complet de questions

```javascript
// 1. Charger toutes les questions
const allQuestions = await loadAllQuestions();

// 2. Afficher dans tableau
displayQuestionsTable(allQuestions);

// 3. État: "78 questions chargées"
```

### Scénario 2: Filtrer par Type (côté Frontend)

```javascript
// Utilisateur sélectionne: "QCM_SIMPLE" dans select
const selectedType = 'QCM_SIMPLE';

// Option A: Filtrage côté frontend (rapide)
const filtered = allQuestions.filter(q => q.type === selectedType);
displayQuestionsTable(filtered);

// Option B: Appel API (plus lent mais fresher data)
const fromApi = await getQuestionsByType(selectedType);
displayQuestionsTable(fromApi);
```

### Scénario 3: Filtrer par Difficulté

```javascript
// Utilisateur sélectionne: "DIFFICILE"
const selectedDifficulty = 'DIFFICILE';

// Option A: Filtrage côté frontend
const filtered = allQuestions.filter(q => q.difficulte === selectedDifficulty);

// Option B: Appel API
const fromApi = await getQuestionsByDifficulty(selectedDifficulty);
```

### Scénario 4: Filtrer par État (Actif/Inactif)

```javascript
// ⚠️ IMPORTANT: Les endpoints retournent le champ "actif"
// Mais getQestions() retourne que les QUESTIONS ACTIVES!

// Les questions inactives ne sont pas retournées par GET /api/v1/questions

// Filtrer actives/inactives côté frontend:
const activeQuestions = allQuestions.filter(q => q.actif === true);
const inactiveQuestions = allQuestions.filter(q => q.actif === false);

// ⚠️ PROBLÈME: inactiveQuestions sera VIDE car API retourne que actives!
```

---

## ⚠️ Limitation Importante

### Issue: Backend retourne UNIQUEMENT questions ACTIVES

```
GET /api/v1/questions
    ↓
questionService.getAllQuestionsActives()  // ← Filtre: actif = true
    ↓
Retourne SEULEMENT les questions avec actif: true
```

### Conséquence pour Admin:
- ✅ Peut voir les questions actives
- ❌ **NE PEUT PAS** voir les questions inactives
- ❌ Tableau filtre "État" ne montrera que les questions actives

### Solution:
Il faut ajouter un endpoint ADMIN spécifique qui retourne les questions actives ET inactives:

```java
// À ajouter dans AdminQuestionController:
@GetMapping
public ResponseEntity<List<QuestionDTO>> getAllQuestionsForAdmin(
    @RequestParam(required = false) TypeQuestion type,
    @RequestParam(required = false) NiveauDifficulte difficulte,
    @RequestParam(required = false) Boolean actif
) {
    // Retourner ALL questions (actif ET inactif)
    // Filtrer selon les paramètres optionnels
}
```

---

## 📊 Tableau Récapitulatif GET Endpoints

| Endpoint | Méthode | Path | Retourne | Rôles |
|----------|---------|------|----------|-------|
| Toutes (user) | GET | `/api/v1/questions` | Questions ACTIVES uniquement | Tous |
| Par ID (user) | GET | `/api/v1/questions/{id}` | 1 question (si active) | Tous |
| Par Type (user) | GET | `/api/v1/questions/type/{type}` | Questions du type (actives) | Tous |
| Par Difficulté (user) | GET | `/api/v1/questions/difficulte/{difficulte}` | Questions du niveau (actives) | Tous |
| Par Compétence (user) | GET | `/api/v1/questions/competence/{competenceId}` | Questions de la compétence (actives) | Tous |
| **Toutes (admin)** | **GET** | **/api/v1/admin/questions** | **Questions ACTIVES + INACTIVES** | **ADMIN ONLY** |
| **Par ID (admin)** | **GET** | **/api/v1/admin/questions/{id}** | **1 question (active ou inactive)** | **ADMIN ONLY** |

---

## 🆕 NOUVEL ENDPOINT ADMIN: GET /api/v1/admin/questions

### Récupérer TOUTES les questions (Admin)

```
GET /api/v1/admin/questions
Authorization: Bearer <token>
(Rôle: ADMIN)
```

**Retourne: Toutes les questions, actives ET inactives** ✅

**Sans paramètres:**
```
GET http://localhost:8080/api/v1/admin/questions
```

**Avec filtres optionnels:**
```
GET /api/v1/admin/questions?type=QCM_SIMPLE
GET /api/v1/admin/questions?difficulte=DIFFICILE
GET /api/v1/admin/questions?actif=false          ← Seulement inactives!
GET /api/v1/admin/questions?type=QCM_SIMPLE&difficulte=MOYEN&actif=true
```

### Response: 200 OK

```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Questions retrieved",
  "data": [
    {
      "id": 150,
      "enonce": "Quelle est la meilleure pratique Java?",
      "type": "QCM_SIMPLE",
      "difficulte": "MOYEN",
      "ponderation": 1.5,
      "dureeSecondes": 60,
      "actif": true,           // ← Peut être true OU false
      "dateCreation": "2024-04-14T10:00:00",
      "competenceIds": [1, 2, 3],
      "choix": [...]
    },
    {
      "id": 151,
      "enonce": "Question inactive",
      "type": "VRAI_FAUX",
      "difficulte": "FACILE",
      "ponderation": 1.0,
      "dureeSecondes": 30,
      "actif": false,          // ← Voilà les inactives!
      "dateCreation": "2024-04-13T09:00:00",
      "competenceIds": [5],
      "choix": [...]
    }
  ]
}
```

### Code Frontend

```javascript
async function getAllQuestionsForAdmin() {
  const token = localStorage.getItem('adminToken');
  
  try {
    const response = await fetch(
      'http://localhost:8080/api/v1/admin/questions',
      {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );

    if (!response.ok) throw new Error('Failed to load questions');

    const result = await response.json();
    const allQuestions = result.data;  // ← Toutes les questions!
    
    console.log(`${allQuestions.length} questions chargées (actives + inactives)`);
    return allQuestions;

  } catch (error) {
    console.error('Erreur:', error.message);
    return [];
  }
}

// Variations avec filtres
async function getInactiveQuestions() {
  const token = localStorage.getItem('adminToken');
  
  const response = await fetch(
    'http://localhost:8080/api/v1/admin/questions?actif=false',
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );

  const result = await response.json();
  return result.data;  // ← Seulement les inactives
}

async function getQCMByDifficulty() {
  const token = localStorage.getItem('adminToken');
  
  const response = await fetch(
    'http://localhost:8080/api/v1/admin/questions?type=QCM_MULTIPLE&difficulte=DIFFICILE',
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );

  const result = await response.json();
  return result.data;  // ← QCM Multiples difficiles
}
```

---

## ✅ Checklist Frontend MISE À JOUR - Questions GET

- [x] **Endpoint admin `/api/v1/admin/questions` MAINTENANT DISPONIBLE** ✨
- [ ] Charger toutes les questions avec `GET /api/v1/admin/questions`
- [ ] Afficher questions ACTIVES et INACTIVES dans le tableau
- [ ] Filtrer "État" montre maintenant les questions inactives
- [ ] Filtrer Type côté backend via query param `?type=`
- [ ] Filtrer Difficulté côté backend via query param `?difficulte=`
- [ ] Filtrer Actif/Inactif via query param `?actif=true/false`
- [ ] Pagination côté frontend après récupération
- [ ] Recherche par énoncé côté frontend
- [ ] Gestion erreur 401 Unauthorized
- [ ] Gestion erreur 403 Forbidden (pas ADMIN)

---

## 🔑 Points Clés - CHANGEMENT IMPORTANT

**AVANT (endpoint public /api/v1/questions):**
- ❌ Retourna SEULEMENT les questions actives
- ❌ Pas de questions inactives visibles
- ❌ Admin ne pouvait pas voir ses questions désactivées

**APRÈS (nouvel endpoint admin /api/v1/admin/questions):**
- ✅ Retourne toutes les questions (actives + inactives)
- ✅ Filtres optionnels: type, difficulté, état actif
- ✅ Support filtrage côté backend (plus efficace)
- ✅ Admin peut maintenant voir ET gérer les questions inactives

### Architecture Frontend

```javascript
// 1. Au chargement de la page Questions
async function initQuestionsPage() {
  const allQuestions = await loadAllQuestions();
  setQuestions(allQuestions);  // State React
  applyFrontendFilters();
}

// 2. Quand utilisateur change le filtre Type
function handleTypeFilterChange(type) {
  if (type === '') {
    // Afficher toutes
    setFiltered(questions);
  } else {
    // Filtrer côté frontend
    setFiltered(questions.filter(q => q.type === type));
  }
}

// 3. Quand utilisateur change le filtre Difficulté  
function handleDifficultyChange(difficulty) {
  setFiltered(questions.filter(q => q.difficulte === difficulty));
}

// 4. Quand utilisateur change le filtre État
function handleStateChange(state) {
  if (state === 'ACTIVE') {
    setFiltered(questions.filter(q => q.actif === true));
  } else if (state === 'INACTIVE') {
    // ❌ PROBLÈME: Aucune ne sera inactif car backend retourne que actives!
    setFiltered(questions.filter(q => q.actif === false));
  } else {
    setFiltered(questions);
  }
}

// 5. Recherche par énoncé
function handleSearch(text) {
  const searched = questions.filter(q => 
    q.enonce.toLowerCase().includes(text.toLowerCase())
  );
  setFiltered(searched);
}

// 6. Pagination côté frontend
function getPaginatedData(pageNumber, pageSize) {
  const start = (pageNumber - 1) * pageSize;
  const end = start + pageSize;
  return filtered.slice(start, end);
}
```

---

## ✅ Checklist Frontend - Questions GET

- [ ] Utiliser `GET /api/v1/questions` pour charger au démarrage
- [ ] Ajouter token Bearer à TOUS les headers
- [ ] Afficher `q.actif` dans colonne "État" du tableau
- [ ] Filtrer Type côté frontend après chargement
- [ ] Filtrer Difficulté côté frontend après chargement
- [ ] Paginer le tableau côté frontend (10-50 par page)
- [ ] Recherche par énoncé côté frontend
- [ ] **Note** : État "Inactif" vide tant que endpoint unique n'existe pas
- [ ] Gestion erreur 401 Unauthorized
- [ ] Gestion erreur 404 Not Found (question supprimée)

---

## 🔑 Points Clés à Retenir

1. **Tous les GET retournent les questions ACTIVES uniquement**
   - `getAllQuestionsActives()` au backend

2. **Le champ `actif` est inclu dans la réponse**
   - Visible au frontend pour info
   - Mais toutes les questions retournées ont `actif: true`

3. **Filtrage fait CÔTÉ FRONTEND**
   - Backend ne supporte pas `?type=` ni `?difficulte=`
   - Charger toutes, filtrer après

4. **Pagination CÔTÉ FRONTEND**
   - Backend retourne TOUT
   - Frontend découpe avec `.slice()`

5. **Bearer token REQUIS partout**
   - `Authorization: Bearer ${token}`

6. **Pour voir les questions INACTIVES**
   - Besoin d'un endpoint `/api/v1/admin/questions` spécifique
   - Qui retournerait toutes les questions indépendamment de l'état

---

**Prêt à intégrer dans le frontend! 🚀**
