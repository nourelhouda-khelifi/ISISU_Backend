# 📚 API QUESTIONS ADMIN - GUIDE DÉTAILLÉ

> **Tout ce que le frontend admin doit savoir sur les endpoints questions**
> Version: 1.0 | Axios + Fetch

---

## 🎯 Vue d'ensemble

L'admin peut faire **7 opérations** sur les questions via ces endpoints:

1. ✅ **GET** `/api/v1/admin/questions` - Charger **TOUTES** les questions (actives + inactives)
2. ✅ **GET** `/api/v1/admin/questions/{id}` - Charger 1 question spécifique
3. ✅ **POST** `/api/v1/admin/questions` - Créer une nouvelle question
4. ✅ **PUT** `/api/v1/admin/questions/{id}` - Modifier une question
5. ✅ **DELETE** `/api/v1/admin/questions/{id}` - Supprimer une question
6. ✅ **POST** `/api/v1/admin/questions/{id}/activer` - Activer une question
7. ✅ **POST** `/api/v1/admin/questions/{id}/desactiver` - Désactiver une question

---

## 📍 Base = `http://localhost:8080/api/v1`

Tous les exemples utilisent cette base URL.

---

## 🔒 Authentification

**Toujours ajouter le header:**
```javascript
'Authorization': 'Bearer ' + localStorage.getItem('adminToken')
```

---

## 1️⃣ GET /admin/questions - Charger TOUTES les questions

### Cas d'usage
- Au chargement de la page questions
- Quand admin demande "rafraîchir"
- Pour créer des filtres

### Endpoint
```
GET /api/v1/admin/questions
Authorization: Bearer <token>
```

### ⚙️ Query Parameters (OPTIONNELS)

| Param | Valeur | Exemple |
|-------|--------|---------|
| `type` | `QCM_SIMPLE` \| `QCM_MULTIPLE` \| `VRAI_FAUX` \| `REPONSE_LIBRE` | `?type=QCM_SIMPLE` |
| `difficulte` | `FACILE` \| `MOYEN` \| `DIFFICILE` | `?difficulte=DIFFICILE` |
| `actif` | `true` \| `false` | `?actif=false` |

### 📤 Response: 200 OK

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
      "actif": true,
      "dateCreation": "2024-04-14T10:00:00",
      "competenceIds": [1, 2, 3],
      "choix": [
        {
          "id": 10,
          "contenu": "Réponse A",
          "ordre": 1
        },
        {
          "id": 11,
          "contenu": "Réponse B",
          "ordre": 2
        }
      ]
    },
    {
      "id": 151,
      "enonce": "Question inactive exemple",
      "type": "VRAI_FAUX",
      "difficulte": "FACILE",
      "ponderation": 1.0,
      "dureeSecondes": 30,
      "actif": false,                    ← INACTIVE!
      "dateCreation": "2024-04-13T09:00:00",
      "competenceIds": [5],
      "choix": [...]
    }
  ]
}
```

### 💻 Code Frontend

#### Option 1: Fetch standard
```javascript
async function loadAllQuestions(token) {
  try {
    const response = await fetch(
      'http://localhost:8080/api/v1/admin/questions',
      {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      }
    );
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }
    
    const result = await response.json();
    
    if (result.success) {
      return result.data;  // ← Array de questions
    }
    throw new Error(result.message);
    
  } catch (error) {
    console.error('Erreur chargement questions:', error);
    return [];
  }
}

// UTILISATION
const questions = await loadAllQuestions(token);
console.log(`${questions.length} questions chargées`);
```

#### Option 2: Fetch avec filtres backend

```javascript
async function loadQuestionsFiltered(token, filters = {}) {
  let url = 'http://localhost:8080/api/v1/admin/questions';
  
  // Construire query parameters
  const params = new URLSearchParams();
  if (filters.type) params.append('type', filters.type);
  if (filters.difficulte) params.append('difficulte', filters.difficulte);
  if (filters.actif !== undefined) params.append('actif', filters.actif);
  
  if (params.toString()) {
    url += '?' + params.toString();
  }
  
  const response = await fetch(url, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  
  const result = await response.json();
  return result.success ? result.data : [];
}

// UTILISATIONS
// Charger tout
const all = await loadQuestionsFiltered(token);

// Charger seulement les INACTIVES
const inactives = await loadQuestionsFiltered(token, { actif: false });

// Charger seulement les QCM_SIMPLE
const qcmSimples = await loadQuestionsFiltered(token, { type: 'QCM_SIMPLE' });

// Charger les DIFFICILES
const difficiles = await loadQuestionsFiltered(token, { difficulte: 'DIFFICILE' });

// Charger QCM_SIMPLE DIFFICILES ACTIFS
const qcmDifficActifs = await loadQuestionsFiltered(token, {
  type: 'QCM_SIMPLE',
  difficulte: 'DIFFICILE',
  actif: true
});
```

#### Option 3: Axios

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1'
});

async function loadAllQuestionsAxios(token) {
  try {
    const response = await api.get('/admin/questions', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    return response.data.data;
  } catch (error) {
    console.error('Erreur:', error);
    return [];
  }
}

async function loadQuestionsFilteredAxios(token, filters = {}) {
  try {
    const response = await api.get('/admin/questions', {
      headers: { 'Authorization': `Bearer ${token}` },
      params: filters  // ← Axios gère URLSearchParams automatiquement
    });
    return response.data.data;
  } catch (error) {
    console.error('Erreur:', error);
    return [];
  }
}

// UTILISATION
const inactive = await loadQuestionsFilteredAxios(token, { actif: false });
```

### 🎯 Cas d'usage réels

#### Cas 1: Tableau initial au chargement de page
```javascript
export default function QuestionsPage() {
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    async function init() {
      const token = localStorage.getItem('adminToken');
      const data = await loadAllQuestions(token);
      setQuestions(data);
      setLoading(false);
    }
    init();
  }, []);
  
  return (
    <div>
      {loading ? '⏳ Chargement...' : <QuestionsTable data={questions} />}
    </div>
  );
}
```

#### Cas 2: Filtre "Afficher seulement les INACTIVES"
```javascript
function handleShowInactiveOnly() {
  const token = localStorage.getItem('adminToken');
  loadQuestionsFiltered(token, { actif: false })
    .then(data => setQuestions(data));
}
```

#### Cas 3: Filtre combiné "QCM DIFFICILES ACTIFS"
```javascript
function handleAdvancedFilter() {
  const token = localStorage.getItem('adminToken');
  loadQuestionsFiltered(token, {
    type: 'QCM_SIMPLE',
    difficulte: 'DIFFICILE',
    actif: true
  }).then(data => setQuestions(data));
}
```

---

## 2️⃣ GET /admin/questions/{id} - Charger UNE question

### Cas d'usage
- Quand admin clique sur une question pour voir les détails
- Avant d'éditer (pré-remplir le formulaire)

### Endpoint
```
GET /api/v1/admin/questions/{id}
Authorization: Bearer <token>
```

**Exemple:** `/api/v1/admin/questions/150`

### 📤 Response: 200 OK

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
    "choix": [
      {
        "id": 10,
        "contenu": "Réponse A",
        "ordre": 1
      },
      {
        "id": 11,
        "contenu": "Réponse B",
        "ordre": 2
      }
    ]
  }
}
```

### 💻 Code Frontend

```javascript
async function getQuestionById(token, questionId) {
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/admin/questions/${questionId}`,
      {
        headers: { 'Authorization': `Bearer ${token}` }
      }
    );
    
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    
    const result = await response.json();
    return result.success ? result.data : null;
    
  } catch (error) {
    console.error('Erreur chargement question:', error);
    return null;
  }
}

// UTILISATION - Modal détail
async function handleViewQuestion(questionId) {
  const token = localStorage.getItem('adminToken');
  const question = await getQuestionById(token, questionId);
  if (question) {
    openDetailModal(question);
  }
}

// UTILISATION - Pré-remplir formulaire édition
async function handleEditQuestion(questionId) {
  const token = localStorage.getItem('adminToken');
  const question = await getQuestionById(token, questionId);
  if (question) {
    populateEditForm(question);
    openEditModal();
  }
}
```

---

## 3️⃣ POST /admin/questions - CRÉER une question

### Cas d'usage
- Admin clique "Nouvelle question"
- Remplit le formulaire
- Clique "Ajouter"

### Endpoint
```
POST /api/v1/admin/questions
Authorization: Bearer <token>
Content-Type: application/json
```

### 📥 Request Body

**Structure complète:**
```json
{
  "enonce": "Quel est le type de données pour un nombre entier en Java?",
  "type": "QCM_SIMPLE",
  "difficulte": "FACILE",
  "dureeSecondes": 30,
  "competenceIds": [1, 2],
  "choix": [
    {
      "contenu": "int",
      "estCorrect": true,
      "ordre": 1
    },
    {
      "contenu": "string",
      "estCorrect": false,
      "ordre": 2
    },
    {
      "contenu": "boolean",
      "estCorrect": false,
      "ordre": 3
    }
  ]
}
```

**Explication:**

| Champ | Type | Requis | Notes |
|-------|------|--------|-------|
| `enonce` | String | ✅ | L'énoncé de la question |
| `type` | Enum | ✅ | QCM_SIMPLE, QCM_MULTIPLE, VRAI_FAUX, REPONSE_LIBRE |
| `difficulte` | Enum | ✅ | FACILE, MOYEN, DIFFICILE |
| `dureeSecondes` | Int | ✅ | Temps limite (ex: 30, 60) |
| `competenceIds` | Array[Int] | ✅ | IDs des compétences liées |
| `choix` | Array | ✅ | Les réponses possibles |
| `choix[].contenu` | String | ✅ | Texte de la réponse |
| `choix[].estCorrect` | Boolean | ✅ | true = bonne réponse (≥1 pour QCM_MULTIPLE) |
| `choix[].ordre` | Int | ✅ | Ordre d'affichage (1, 2, 3...) |

### 📤 Response: 201 Created

```json
{
  "timestamp": "2024-04-14T10:35:00Z",
  "status": 201,
  "success": true,
  "message": "Question created successfully",
  "data": {
    "id": 200,  ← Nouvel ID généré
    "enonce": "Quel est le type de données pour un nombre entier en Java?",
    "type": "QCM_SIMPLE",
    "difficulte": "FACILE",
    "ponderation": 1.0,
    "dureeSecondes": 30,
    "actif": true,  ← Par défaut ACTIF à la création
    "dateCreation": "2024-04-14T10:35:00",
    "competenceIds": [1, 2],
    "choix": [...]
  }
}
```

### 💻 Code Frontend

#### Option 1: Fetch

```javascript
async function createQuestion(token, questionData) {
  try {
    const response = await fetch(
      'http://localhost:8080/api/v1/admin/questions',
      {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(questionData)
      }
    );
    
    if (response.status === 201) {
      const result = await response.json();
      return result.data;  // ← Nouvelle question créée
    }
    
    if (response.status === 400) {
      const error = await response.json();
      throw new Error(`Erreur validation: ${error.message}`);
    }
    
    throw new Error(`HTTP ${response.status}`);
    
  } catch (error) {
    console.error('Erreur création:', error);
    throw error;
  }
}

// UTILISATION dans formulaire
async function handleCreateQuestion(formData) {
  const token = localStorage.getItem('adminToken');
  
  try {
    const newQuestion = await createQuestion(token, {
      enonce: formData.enonce,
      type: formData.type,
      difficulte: formData.difficulte,
      dureeSecondes: parseInt(formData.dureeSecondes),
      competenceIds: formData.selectedCompetencies,  // Array d'IDs
      choix: formData.answers.map((ans, idx) => ({
        contenu: ans.texte,
        estCorrect: ans.isCorrect,
        ordre: idx + 1
      }))
    });
    
    console.log('✅ Question créée:', newQuestion.id);
    closeModal();
    refreshQuestionsList();  // Recharger la liste
    
  } catch (error) {
    showError(`Erreur: ${error.message}`);
  }
}
```

#### Option 2: Axios

```javascript
async function createQuestionAxios(token, questionData) {
  try {
    const response = await api.post('/admin/questions', questionData, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    return response.data.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || error.message);
  }
}
```

### ✅ Checklist création

- [x] Remplir l'énoncé
- [x] Sélectionner le type (QCM_SIMPLE, etc.)
- [x] Sélectionner la difficulté
- [x] Définir le temps
- [x] Ajouter au moins 1 compétence
- [x] Ajouter au moins 2 réponses
- [x] Marquer 1+ réponse comme correcte (2+ pour QCM_MULTIPLE)
- [x] Vérifier l'ordre des réponses

---

## 4️⃣ PUT /admin/questions/{id} - MODIFIER une question

### Cas d'usage
- Admin clique "Éditer" sur une question
- Modifie les champs
- Clique "Sauvegarder"

### Endpoint
```
PUT /api/v1/admin/questions/{id}
Authorization: Bearer <token>
Content-Type: application/json
```

**Exemple:** `/api/v1/admin/questions/150`

### 📥 Request Body

**Même structure que création** (envoyer les champs modifiés):
```json
{
  "enonce": "VERSION MODIFIÉE de la question",
  "type": "QCM_SIMPLE",
  "difficulte": "MOYEN",
  "dureeSecondes": 45,
  "competenceIds": [1, 2, 5],
  "choix": [
    {
      "contenu": "Réponse modifiée A",
      "estCorrect": true,
      "ordre": 1
    },
    {
      "contenu": "Nouvelle réponse B",
      "estCorrect": false,
      "ordre": 2
    }
  ]
}
```

### 📤 Response: 200 OK

```json
{
  "status": 200,
  "success": true,
  "message": "Question updated successfully",
  "data": {
    "id": 150,
    "enonce": "VERSION MODIFIÉE de la question",
    "type": "QCM_SIMPLE",
    "difficulte": "MOYEN",
    "ponderation": 1.5,
    "dureeSecondes": 45,
    "actif": true,
    "dateCreation": "2024-04-14T10:00:00",
    "dateModification": "2024-04-14T10:40:00",  ← Champ date modif
    "competenceIds": [1, 2, 5],
    "choix": [...]
  }
}
```

### 💻 Code Frontend

```javascript
async function updateQuestion(token, questionId, questionData) {
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/admin/questions/${questionId}`,
      {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(questionData)
      }
    );
    
    if (response.status === 200) {
      const result = await response.json();
      return result.data;
    }
    
    throw new Error(`HTTP ${response.status}`);
    
  } catch (error) {
    console.error('Erreur mise à jour:', error);
    throw error;
  }
}

// UTILISATION
async function handleSaveQuestion(questionId, formData) {
  const token = localStorage.getItem('adminToken');
  
  try {
    const updated = await updateQuestion(token, questionId, {
      enonce: formData.enonce,
      type: formData.type,
      difficulte: formData.difficulte,
      dureeSecondes: parseInt(formData.dureeSecondes),
      competenceIds: formData.selectedCompetencies,
      choix: formData.answers.map((ans, idx) => ({
        contenu: ans.texte,
        estCorrect: ans.isCorrect,
        ordre: idx + 1
      }))
    });
    
    console.log('✅ Question mise à jour:', updated.id);
    closeEditModal();
    refreshQuestionsList();
    
  } catch (error) {
    showError(`Erreur: ${error.message}`);
  }
}
```

---

## 5️⃣ DELETE /admin/questions/{id} - SUPPRIMER une question

### Cas d'usage
- Admin clique sur le bouton 🗑️ (poubelle)
- Confirmation "Voulez-vous vraiment supprimer?"
- La question est supprimée

### Endpoint
```
DELETE /api/v1/admin/questions/{id}
Authorization: Bearer <token>
```

**Exemple:** `/api/v1/admin/questions/150`

### 📤 Response: 204 No Content

⚠️ **IMPORTANT:** Pas de body JSON! Juste 204 = succès.

### 💻 Code Frontend

```javascript
async function deleteQuestion(token, questionId) {
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/admin/questions/${questionId}`,
      {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      }
    );
    
    if (response.status === 204) {
      return true;  // ← Succès (pas de JSON à parser)
    }
    
    throw new Error(`HTTP ${response.status}`);
    
  } catch (error) {
    console.error('Erreur suppression:', error);
    throw error;
  }
}

// UTILISATION avec confirmation
async function handleDeleteQuestion(questionId) {
  // 1. Demander confirmation
  if (!window.confirm('Êtes-vous sûr? Cette action est irréversible.')) {
    return;
  }
  
  // 2. Supprimer
  const token = localStorage.getItem('adminToken');
  try {
    const success = await deleteQuestion(token, questionId);
    if (success) {
      console.log('✅ Question supprimée');
      refreshQuestionsList();  // Recharger
    }
  } catch (error) {
    showError(`Erreur: ${error.message}`);
  }
}
```

⚠️ **Gestion du 204:**
```javascript
// ❌ MAUVAIS - Ça va planter
const result = await response.json();  // 204 n'a pas de body!

// ✅ BON - Vérifier le status
if (response.status === 204) {
  return true;  // Succès confirmé par le code HTTP
}
```

---

## 6️⃣ POST /admin/questions/{id}/activer - ACTIVER une question

### Cas d'usage
- Admin voit une question INACTIVE
- Clique sur le bouton "Activer" 
- La question devient active et visible aux utilisateurs

### Endpoint
```
POST /api/v1/admin/questions/{id}/activer
Authorization: Bearer <token>
```

**Exemple:** `/api/v1/admin/questions/150/activer`

### 📤 Response: 200 OK

```json
{
  "status": 200,
  "success": true,
  "message": "Question activated successfully",
  "data": {
    "id": 150,
    "enonce": "...",
    "actif": true,  ← Maintenant ACTIF
    "dateModification": "2024-04-14T10:45:00"
  }
}
```

### 💻 Code Frontend

```javascript
async function activateQuestion(token, questionId) {
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/admin/questions/${questionId}/activer`,
      {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      }
    );
    
    if (response.status === 200) {
      const result = await response.json();
      return result.data;
    }
    
    throw new Error(`HTTP ${response.status}`);
    
  } catch (error) {
    console.error('Erreur activation:', error);
    throw error;
  }
}

// UTILISATION dans tableau
function handleActivateClick(questionId) {
  const token = localStorage.getItem('adminToken');
  activateQuestion(token, questionId)
    .then(() => {
      showSuccess('✅ Question activée');
      refreshQuestionsList();
    })
    .catch(err => showError(err.message));
}

// Exemple React
function QuestionRow({ question, onStateChange }) {
  return (
    <tr>
      <td>{question.enonce}</td>
      <td>{question.type}</td>
      <td>
        {question.actif ? (
          <span style={{color: 'green'}}>✅ Actif</span>
        ) : (
          <button onClick={() => {
            const token = localStorage.getItem('adminToken');
            activateQuestion(token, question.id).then(onStateChange);
          }}>
            🔄 Activer
          </button>
        )}
      </td>
    </tr>
  );
}
```

---

## 7️⃣ POST /admin/questions/{id}/desactiver - DÉSACTIVER une question

### Cas d'usage
- Admin voit une question ACTIVE
- Clique sur le bouton "Désactiver"
- La question devient inactive et cachée des utilisateurs
- Les données existantes restent

### Endpoint
```
POST /api/v1/admin/questions/{id}/desactiver
Authorization: Bearer <token>
```

**Exemple:** `/api/v1/admin/questions/150/desactiver`

### 📤 Response: 200 OK

```json
{
  "status": 200,
  "success": true,
  "message": "Question deactivated successfully",
  "data": {
    "id": 150,
    "enonce": "...",
    "actif": false,  ← Maintenant INACTIF
    "dateModification": "2024-04-14T10:45:00"
  }
}
```

### 💻 Code Frontend

```javascript
async function deactivateQuestion(token, questionId) {
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/admin/questions/${questionId}/desactiver`,
      {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      }
    );
    
    if (response.status === 200) {
      const result = await response.json();
      return result.data;
    }
    
    throw new Error(`HTTP ${response.status}`);
    
  } catch (error) {
    console.error('Erreur désactivation:', error);
    throw error;
  }
}

// UTILISATION
function handleDeactivateClick(questionId) {
  if (!window.confirm('Désactiver cette question? Elle ne sera plus accessible.')) {
    return;
  }
  
  const token = localStorage.getItem('adminToken');
  deactivateQuestion(token, questionId)
    .then(() => {
      showSuccess('✅ Question désactivée');
      refreshQuestionsList();
    })
    .catch(err => showError(err.message));
}
```

---

## 🛠️ HELPER UTILS

```javascript
// src/utils/questionApi.js

const BASE_URL = 'http://localhost:8080/api/v1';
const ENDPOINTS = {
  LIST: '/admin/questions',
  DETAIL: (id) => `/admin/questions/${id}`,
  CREATE: '/admin/questions',
  UPDATE: (id) => `/admin/questions/${id}`,
  DELETE: (id) => `/admin/questions/${id}`,
  ACTIVATE: (id) => `/admin/questions/${id}/activer`,
  DEACTIVATE: (id) => `/admin/questions/${id}/desactiver`
};

class QuestionService {
  constructor(token) {
    this.token = token;
    this.headers = {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    };
  }

  async list(filters = {}) {
    const params = new URLSearchParams(filters).toString();
    const url = params ? `${ENDPOINTS.LIST}?${params}` : ENDPOINTS.LIST;
    
    const res = await fetch(`${BASE_URL}${url}`, {
      headers: this.headers
    });
    
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const result = await res.json();
    return result.data;
  }

  async getById(id) {
    const res = await fetch(`${BASE_URL}${ENDPOINTS.DETAIL(id)}`, {
      headers: this.headers
    });
    
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const result = await res.json();
    return result.data;
  }

  async create(data) {
    const res = await fetch(`${BASE_URL}${ENDPOINTS.CREATE}`, {
      method: 'POST',
      headers: this.headers,
      body: JSON.stringify(data)
    });
    
    if (res.status !== 201) throw new Error(`HTTP ${res.status}`);
    const result = await res.json();
    return result.data;
  }

  async update(id, data) {
    const res = await fetch(`${BASE_URL}${ENDPOINTS.UPDATE(id)}`, {
      method: 'PUT',
      headers: this.headers,
      body: JSON.stringify(data)
    });
    
    if (res.status !== 200) throw new Error(`HTTP ${res.status}`);
    const result = await res.json();
    return result.data;
  }

  async delete(id) {
    const res = await fetch(`${BASE_URL}${ENDPOINTS.DELETE(id)}`, {
      method: 'DELETE',
      headers: this.headers
    });
    
    if (res.status !== 204) throw new Error(`HTTP ${res.status}`);
    return true;
  }

  async activate(id) {
    const res = await fetch(`${BASE_URL}${ENDPOINTS.ACTIVATE(id)}`, {
      method: 'POST',
      headers: this.headers
    });
    
    if (res.status !== 200) throw new Error(`HTTP ${res.status}`);
    const result = await res.json();
    return result.data;
  }

  async deactivate(id) {
    const res = await fetch(`${BASE_URL}${ENDPOINTS.DEACTIVATE(id)}`, {
      method: 'POST',
      headers: this.headers
    });
    
    if (res.status !== 200) throw new Error(`HTTP ${res.status}`);
    const result = await res.json();
    return result.data;
  }
}

export default QuestionService;
```

**Utilisation:**
```javascript
const questionService = new QuestionService(token);

// Charger tout
const all = await questionService.list();

// Charger avec filtres
const inactives = await questionService.list({ actif: false });
const qcmSimples = await questionService.list({ type: 'QCM_SIMPLE' });

// CRUD
const newQ = await questionService.create(data);
await questionService.update(150, data);
await questionService.delete(150);
await questionService.activate(150);
await questionService.deactivate(150);
```

---

## 📊 RÉSUMÉ TABLEAU

| Opération | Méthode | URL | Status | Utilisation |
|-----------|---------|-----|--------|-----------|
| Charger TOUTES | GET | `/admin/questions` | 200 | Afficher tableau initial |
| Avec filtres | GET | `/admin/questions?actif=false` | 200 | Filtrer questions |
| Détail 1 | GET | `/admin/questions/{id}` | 200 | Voir  détails/éditer |
| Créer | POST | `/admin/questions` | 201 | Nouvelle question |
| Modifier | PUT | `/admin/questions/{id}` | 200 | Éditer question |
| Supprimer | DELETE | `/admin/questions/{id}` | 204 | Poubelle 🗑️ |
| Activer | POST | `/admin/questions/{id}/activer` | 200 | Rendre visible |
| Désactiver | POST | `/admin/questions/{id}/desactiver` | 200 | Cacher question |

---

## ⚠️ ERREURS COURANTES

### ❌ Erreur 1: Headers manquants

```javascript
// MAUVAIS
fetch('http://localhost:8080/api/v1/admin/questions');

// BON
fetch('http://localhost:8080/api/v1/admin/questions', {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

### ❌ Erreur 2: 204 No Content

```javascript
// MAUVAIS - Ça plante
const result = await response.json();  // 204 n'a pas de body

// BON
if (response.status === 204) {
  return true;
}
```

### ❌ Erreur 3: Oublier Content-Type

```javascript
// MAUVAIS
fetch('url', {
  method: 'POST',
  body: JSON.stringify(data)
});

// BON
fetch('url', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(data)
});
```

### ❌ Erreur 4: Token expiré

```javascript
if (response.status === 401) {
  localStorage.removeItem('adminToken');
  window.location.href = '/login';
  return;
}
```

---

## 🎯 TUTORIEL COMPLET - Une page Questions

```javascript
// src/pages/Questions.jsx
import { useState, useEffect } from 'react';
import QuestionService from '../utils/questionApi';

export default function QuestionsPage() {
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [filters, setFilters] = useState({ type: '', difficulte: '', actif: '' });

  const token = localStorage.getItem('adminToken');
  const service = new QuestionService(token);

  // 1️⃣ CHARGEMENT INITIAL
  useEffect(() => {
    loadQuestions();
  }, []);

  // 2️⃣ CHARGER LES QUESTIONS
  async function loadQuestions() {
    setLoading(true);
    try {
      const data = await service.list(
        Object.fromEntries(Object.entries(filters).filter(([, v]) => v))
      );
      setQuestions(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  // 3️⃣ CRÉER
  async function handleCreate(data) {
    try {
      await service.create(data);
      setShowModal(false);
      loadQuestions();
    } catch (err) {
      setError(err.message);
    }
  }

  // 4️⃣ MODIFIER
  async function handleUpdate(id, data) {
    try {
      await service.update(id, data);
      loadQuestions();
    } catch (err) {
      setError(err.message);
    }
  }

  // 5️⃣ SUPPRIMER
  async function handleDelete(id) {
    if (!window.confirm('Êtes-vous sûr?')) return;
    try {
      await service.delete(id);
      loadQuestions();
    } catch (err) {
      setError(err.message);
    }
  }

  // 6️⃣ ACTIVER
  async function handleActivate(id) {
    try {
      await service.activate(id);
      loadQuestions();
    } catch (err) {
      setError(err.message);
    }
  }

  // 7️⃣ DÉSACTIVER
  async function handleDeactivate(id) {
    try {
      await service.deactivate(id);
      loadQuestions();
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="questions-page">
      <h1>Gestion des Questions</h1>

      {error && <div className="error">{error}</div>}

      {/* FILTRES */}
      <div className="filters">
        <select
          value={filters.type}
          onChange={(e) => {
            setFilters({ ...filters, type: e.target.value });
            loadQuestions();
          }}
        >
          <option value="">Tous les types</option>
          <option value="QCM_SIMPLE">QCM Simple</option>
          <option value="QCM_MULTIPLE">QCM Multiple</option>
          <option value="VRAI_FAUX">Vrai/Faux</option>
          <option value="REPONSE_LIBRE">Réponse libre</option>
        </select>

        <select
          value={filters.difficulte}
          onChange={(e) => {
            setFilters({ ...filters, difficulte: e.target.value });
            loadQuestions();
          }}
        >
          <option value="">Toutes difficultés</option>
          <option value="FACILE">Facile</option>
          <option value="MOYEN">Moyen</option>
          <option value="DIFFICILE">Difficile</option>
        </select>

        <select
          value={filters.actif}
          onChange={(e) => {
            setFilters({ ...filters, actif: e.target.value });
            loadQuestions();
          }}
        >
          <option value="">Tous états</option>
          <option value="true">Actif</option>
          <option value="false">Inactif</option>
        </select>
      </div>

      {/* BOUTON CRÉER */}
      <button onClick={() => setShowModal(true)}>+ Nouvelle question</button>

      {/* TABLEAU */}
      {loading ? (
        <p>Chargement...</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Énoncé</th>
              <th>Type</th>
              <th>Difficulté</th>
              <th>État</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {questions.map((q) => (
              <tr key={q.id}>
                <td>{q.enonce}</td>
                <td>{q.type}</td>
                <td>{q.difficulte}</td>
                <td>{q.actif ? '✅ Actif' : '❌ Inactif'}</td>
                <td>
                  <button onClick={() => handleUpdate(q.id, { ...q })}>✏️</button>
                  <button onClick={() => handleDelete(q.id)}>🗑️</button>
                  {q.actif ? (
                    <button onClick={() => handleDeactivate(q.id)}>🔴 Désactiver</button>
                  ) : (
                    <button onClick={() => handleActivate(q.id)}>🟢 Activer</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/* MODAL CRÉATION */}
      {showModal && (
        <div className="modal">
          <QuestionForm onSubmit={handleCreate} onClose={() => setShowModal(false)} />
        </div>
      )}
    </div>
  );
}
```

---

**C'est tout ce que le frontend doit savoir sur les APIs questions! 🚀**

