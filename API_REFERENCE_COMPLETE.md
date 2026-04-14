# 📡 RÉFÉRENCE COMPLÈTE DES APIS - FRONTEND

> **Tous les endpoints disponibles pour développer l'interface admin**
> Version: 1.0 | Date: Avril 2024

---

## 🔐 Authentification (Étape 1)

### Login

```
POST /api/v1/auth/login
Content-Type: application/json
```

**Request:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response: 200 OK**
```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Code Frontend:**
```javascript
async function login(username, password) {
  const response = await fetch('http://localhost:8080/api/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  
  const result = await response.json();
  if (result.success) {
    localStorage.setItem('adminToken', result.data.token);
    return result.data.token;
  }
  throw new Error(result.message);
}

// Utilisation
const token = await login('admin', 'admin123');
```

---

## 📊 DASHBOARD - Vue d'ensemble KPIs

### Récupérer dashboard

```
GET /api/v1/dashboard/admin
Authorization: Bearer <token>
```

**Response: 200 OK**
```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Dashboard data retrieved",
  "data": {
    "totalUtilisateurs": 250,
    "totalEtudiantsFIE3": 180,
    "totalCandidatsVAE": 45,
    "scoreMoyenGlobal": 12.5,
    "sessionsEnCours": 42,
    "tauxReussite": 76.4,
    "competencesTopPerformance": [
      {
        "competenceId": 1,
        "competenceName": "Java Core",
        "tauxReussite": 85.3,
        "nombreTest": 245
      },
      {
        "competenceId": 2,
        "competenceName": "Spring Boot",
        "tauxReussite": 82.1,
        "nombreTest": 198
      }
    ],
    "competencesLacunes": [
      {
        "competenceId": 10,
        "competenceName": "Kubernetes",
        "tauxReussite": 32.5,
        "nombreTest": 80
      }
    ]
  }
}
```

**Code Frontend:**
```javascript
async function getDashboard(token) {
  const response = await fetch('http://localhost:8080/api/v1/dashboard/admin', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  
  const result = await response.json();
  return result.data;
}

// Afficher les KPIs
const dashboard = await getDashboard(token);
console.log(`Total utilisateurs: ${dashboard.totalUtilisateurs}`);
console.log(`Taux réussite: ${dashboard.tauxReussite}%`);
```

---

## 👥 UTILISATEURS - Gestion utilisateurs

### Récupérer liste utilisateurs

```
GET /api/v1/dashboard/admin/users
Authorization: Bearer <token>

Parameters (optionnels):
  ?role=ETUDIANT_FIE3      (Filtrer par rôle)
  ?statut=ACTIF            (Filtrer par statut)
```

**Response: 200 OK**
```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Users retrieved",
  "data": [
    {
      "id": 125,
      "nom": "Dupont",
      "prenom": "Jean",
      "email": "jean.dupont@univ.fr",
      "role": "ETUDIANT_FIE3",
      "statut": "ACTIF",
      "nombreSessions": 12,
      "scoreMoyen": 14.5,
      "derniereConnexion": "2024-04-14T10:30:00"
    },
    {
      "id": 126,
      "nom": "Martin",
      "prenom": "Sophie",
      "email": "sophie.martin@univ.fr",
      "role": "CANDIDAT_VAE",
      "statut": "EN_ATTENTE_OTP",
      "nombreSessions": 3,
      "scoreMoyen": 11.2,
      "derniereConnexion": "2024-04-13T15:45:00"
    }
  ]
}
```

**Code Frontend:**
```javascript
async function getUsers(token, role = null, statut = null) {
  let url = 'http://localhost:8080/api/v1/dashboard/admin/users';
  const params = new URLSearchParams();
  
  if (role) params.append('role', role);
  if (statut) params.append('statut', statut);
  
  if (params.toString()) {
    url += '?' + params.toString();
  }
  
  const response = await fetch(url, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  
  const result = await response.json();
  return result.data;
}

// Utilisation
const allUsers = await getUsers(token);
const etudiants = await getUsers(token, 'ETUDIANT_FIE3');
const actifs = await getUsers(token, null, 'ACTIF');
```

**Rôles disponibles:**
- `ETUDIANT_FIE3`
- `CANDIDAT_VAE`
- `ADMIN`

**Statuts disponibles:**
- `ACTIF`
- `EN_ATTENTE_OTP`
- `SUSPENDU`

---

## ❓ QUESTIONS - Gestion des questions

### 1️⃣ Récupérer TOUTES les questions (Admin)

```
GET /api/v1/admin/questions
Authorization: Bearer <token>

Parameters (optionnels - filtres backend):
  ?type=QCM_SIMPLE         (QCM_SIMPLE | QCM_MULTIPLE | VRAI_FAUX | REPONSE_LIBRE)
  ?difficulte=DIFFICILE    (FACILE | MOYEN | DIFFICILE)
  ?actif=false             (true | false)
```

**Response: 200 OK - Toutes les questions**
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
        }
      ]
    }
  ]
}
```

**Code Frontend:**
```javascript
async function getAllQuestions(token, filters = {}) {
  let url = 'http://localhost:8080/api/v1/admin/questions';
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
  return result.data;
}

// Utilisation
const allQuestions = await getAllQuestions(token);
const inactives = await getAllQuestions(token, { actif: false });
const qcmDifficiles = await getAllQuestions(token, { 
  type: 'QCM_SIMPLE', 
  difficulte: 'DIFFICILE' 
});
```

### 2️⃣ Récupérer une question

```
GET /api/v1/admin/questions/{id}
Authorization: Bearer <token>
```

**Code Frontend:**
```javascript
async function getQuestion(token, questionId) {
  const response = await fetch(
    `http://localhost:8080/api/v1/admin/questions/${questionId}`,
    { headers: { 'Authorization': `Bearer ${token}` } }
  );
  
  const result = await response.json();
  return result.data;
}
```

### 3️⃣ Créer une question

```
POST /api/v1/admin/questions
Authorization: Bearer <token>
Content-Type: application/json
```

**Request:**
```json
{
  "enonce": "Quelle est la capitale du France?",
  "type": "QCM_SIMPLE",
  "difficulte": "FACILE",
  "dureeSecondes": 30,
  "competenceIds": [1, 2],
  "choix": [
    {
      "contenu": "Paris",
      "estCorrect": true,
      "ordre": 1
    },
    {
      "contenu": "Lyon",
      "estCorrect": false,
      "ordre": 2
    }
  ]
}
```

**Response: 201 Created**

**Code Frontend:**
```javascript
async function createQuestion(token, questionData) {
  const response = await fetch('http://localhost:8080/api/v1/admin/questions', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(questionData)
  });
  
  if (response.status === 201) {
    const result = await response.json();
    return result.data;
  }
  throw new Error('Failed to create question');
}
```

### 4️⃣ Modifier une question

```
PUT /api/v1/admin/questions/{id}
Authorization: Bearer <token>
Content-Type: application/json
```

**Request:** (Même structure que création)

**Response: 200 OK**

**Code Frontend:**
```javascript
async function updateQuestion(token, questionId, questionData) {
  const response = await fetch(
    `http://localhost:8080/api/v1/admin/questions/${questionId}`,
    {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(questionData)
    }
  );
  
  const result = await response.json();
  return result.data;
}
```

### 5️⃣ Supprimer une question

```
DELETE /api/v1/admin/questions/{id}
Authorization: Bearer <token>
```

**Response: 204 No Content** (pas de body!)

**Code Frontend:**
```javascript
async function deleteQuestion(token, questionId) {
  const response = await fetch(
    `http://localhost:8080/api/v1/admin/questions/${questionId}`,
    {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  
  if (response.status === 204) {
    return true;  // Succès
  }
  throw new Error('Failed to delete question');
}
```

### 6️⃣ Activer une question

```
POST /api/v1/admin/questions/{id}/activer
Authorization: Bearer <token>
```

**Response: 200 OK** (retourne la question avec actif: true)

**Code Frontend:**
```javascript
async function activateQuestion(token, questionId) {
  const response = await fetch(
    `http://localhost:8080/api/v1/admin/questions/${questionId}/activer`,
    {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  
  const result = await response.json();
  return result.data;
}
```

### 7️⃣ Désactiver une question

```
POST /api/v1/admin/questions/{id}/desactiver
Authorization: Bearer <token>
```

**Response: 200 OK** (retourne la question avec actif: false)

**Code Frontend:**
```javascript
async function deactivateQuestion(token, questionId) {
  const response = await fetch(
    `http://localhost:8080/api/v1/admin/questions/${questionId}/desactiver`,
    {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  
  const result = await response.json();
  return result.data;
}
```

---

## 📈 STATISTIQUES - Données sessions

### Récupérer statistiques sessions

```
GET /api/v1/dashboard/admin/statistiques/sessions
Authorization: Bearer <token>
```

**Response: 200 OK**
```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Statistics retrieved",
  "data": {
    "totalSessions": 5200,
    "sessionsTerminees": 4100,
    "sessionsAbandonnes": 350,
    "dureeParMoyenne": 45.30,
    "scoreParMoyenne": 13.80,
    "sessionsParJour": [
      {
        "date": "2024-04-14",
        "count": 25,
        "scoreAverage": 14.15
      },
      {
        "date": "2024-04-13",
        "count": 28,
        "scoreAverage": 13.45
      }
    ]
  }
}
```

**Code Frontend:**
```javascript
async function getStatistics(token) {
  const response = await fetch(
    'http://localhost:8080/api/v1/dashboard/admin/statistiques/sessions',
    { headers: { 'Authorization': `Bearer ${token}` } }
  );
  
  const result = await response.json();
  return result.data;
}

// Utilisation pour graphiques
const stats = await getStatistics(token);
console.log(`Sessions totales: ${stats.totalSessions}`);
console.log(`Taux compltion: ${(stats.sessionsTerminees / stats.totalSessions * 100).toFixed(1)}%`);
```

---

## 🔑 ENUMS - Valeurs fixes

### Types de Questions
```javascript
const TypeQuestion = {
  QCM_SIMPLE: 'QCM_SIMPLE',       // 1 bonne réponse
  QCM_MULTIPLE: 'QCM_MULTIPLE',   // Plusieurs bonnes réponses
  VRAI_FAUX: 'VRAI_FAUX',         // Vrai ou Faux
  REPONSE_LIBRE: 'REPONSE_LIBRE'  // Texte libre
};
```

### Niveaux de Difficulté
```javascript
const NiveauDifficulte = {
  FACILE: 'FACILE',         // Pondération: 1.0
  MOYEN: 'MOYEN',           // Pondération: 1.5
  DIFFICILE: 'DIFFICILE'    // Pondération: 2.0
};
```

### Rôles
```javascript
const Role = {
  ETUDIANT_FIE3: 'ETUDIANT_FIE3',
  CANDIDAT_VAE: 'CANDIDAT_VAE',
  ADMIN: 'ADMIN'
};
```

### Statuts Compte
```javascript
const StatutCompte = {
  ACTIF: 'ACTIF',
  EN_ATTENTE_OTP: 'EN_ATTENTE_OTP',
  SUSPENDU: 'SUSPENDU'
};
```

---

## 📊 TABLEAU RÉCAPITULATIF - Tous les endpoints

| Domaine | Méthode | URL | Authentification | Rôle |
|---------|---------|-----|------------------|------|
| **Auth** | POST | `/api/v1/auth/login` | ❌ Non | - |
| **Dashboard** | GET | `/api/v1/dashboard/admin` | ✅ Token | ADMIN |
| **Utilisateurs** | GET | `/api/v1/dashboard/admin/users` | ✅ Token | ADMIN |
| **Statistiques** | GET | `/api/v1/dashboard/admin/statistiques/sessions` | ✅ Token | ADMIN |
| **Questions - Liste** | GET | `/api/v1/admin/questions` | ✅ Token | ADMIN |
| **Questions - Détail** | GET | `/api/v1/admin/questions/{id}` | ✅ Token | ADMIN |
| **Questions - Créer** | POST | `/api/v1/admin/questions` | ✅ Token | ADMIN |
| **Questions - Modifier** | PUT | `/api/v1/admin/questions/{id}` | ✅ Token | ADMIN |
| **Questions - Supprimer** | DELETE | `/api/v1/admin/questions/{id}` | ✅ Token | ADMIN |
| **Questions - Activer** | POST | `/api/v1/admin/questions/{id}/activer` | ✅ Token | ADMIN |
| **Questions - Désactiver** | POST | `/api/v1/admin/questions/{id}/desactiver` | ✅ Token | ADMIN |

---

## ⚡ Utility - Helper HTTP Client

```javascript
// src/api/client.js
class ApiClient {
  constructor(baseURL, token) {
    this.baseURL = baseURL;
    this.token = token;
  }

  async request(method, endpoint, data = null) {
    const url = `${this.baseURL}${endpoint}`;
    const options = {
      method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.token}`
      }
    };

    if (data) {
      options.body = JSON.stringify(data);
    }

    const response = await fetch(url, options);

    // Gestion du 204 No Content
    if (response.status === 204) {
      return null;
    }

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    const result = await response.json();
    return result.data;
  }

  get(endpoint) {
    return this.request('GET', endpoint);
  }

  post(endpoint, data) {
    return this.request('POST', endpoint, data);
  }

  put(endpoint, data) {
    return this.request('PUT', endpoint, data);
  }

  delete(endpoint) {
    return this.request('DELETE', endpoint);
  }
}

export default ApiClient;
```

**Utilisation:**
```javascript
const client = new ApiClient('http://localhost:8080/api/v1', token);

// GET
const questions = await client.get('/admin/questions');

// POST
const newQuestion = await client.post('/admin/questions', questionData);

// PUT
const updated = await client.put(`/admin/questions/${id}`, updateData);

// DELETE
await client.delete(`/admin/questions/${id}`);
```

---

## 🌐 Base URL

```
Développement: http://localhost:8080
Production:    https://isisu-backend.herokuapp.com (ou votre URL)
```

---

## ⚠️ Gestion des Erreurs

**Tous les endpoints retournent:**
```json
{
  "timestamp": "ISO8601",
  "status": 200|201|400|401|403|404|500,
  "success": true|false,
  "message": "Description de l'erreur ou succès",
  "data": null|objet
}
```

**Codes HTTP principaux:**
- **200 OK**: Succès GET
- **201 Created**: Question créée
- **204 No Content**: Suppression/Désactivation réussie (pas de body)
- **400 Bad Request**: Données invalides
- **401 Unauthorized**: Token manquant/expiré
- **403 Forbidden**: Pas assez de permissions (pas ADMIN)
- **404 Not Found**: Ressource inexistante
- **500 Internal Server Error**: Erreur serveur

**Exemple gestion erreur:**
```javascript
async function safeApiCall(apiFunction) {
  try {
    return await apiFunction();
  } catch (error) {
    if (error.message === 'HTTP 401') {
      // Token expiré
      localStorage.removeItem('adminToken');
      window.location.href = '/login';
    } else if (error.message === 'HTTP 403') {
      // Pas ADMIN
      showError('Accès refusé: Vous n\'êtes pas administrateur');
    } else if (error.message === 'HTTP 404') {
      // Ressource supprimée
      showError('Ressource introuvable');
    } else {
      showError('Erreur: ' + error.message);
    }
    return null;
  }
}
```

---

## 📋 Checklist Frontend

### Phase 1: Authentification
- [ ] Page login avec username/password
- [ ] Appel POST `/api/v1/auth/login`
- [ ] Stockage du token en localStorage
- [ ] Redirection dashboard si succès
- [ ] Gestion erreur 401

### Phase 2: Dashboard
- [ ] Component dashboard avec KPIs
- [ ] GET `/api/v1/dashboard/admin`
- [ ] Afficher 6 cartes KPI
- [ ] Graphiques compétences top/lacunes

### Phase 3: Utilisateurs
- [ ] Tableau des utilisateurs paginé
- [ ] GET `/api/v1/dashboard/admin/users`
- [ ] Filtres: rôle, statut (frontend)
- [ ] Colonnes: nom, email, rôle, sessions, score, dernière connexion

### Phase 4: Questions (Complexe)
- [ ] Tableau questions avec pagination
- [ ] GET `/api/v1/admin/questions`
- [ ] Filtres: type, difficulté, état actif (backend query params)
- [ ] Recherche énoncé (frontend)
- [ ] Modal créer: POST `/api/v1/admin/questions`
- [ ] Modal éditer: PUT `/api/v1/admin/questions/{id}`
- [ ] Button supprimer: DELETE `/api/v1/admin/questions/{id}` (confirmation)
- [ ] Button activer/désactiver: POST `/api/v1/admin/questions/{id}/activer|desactiver`
- [ ] Gestion 204 No Content

### Phase 5: Statistiques
- [ ] KPIs: sessions total, complétées, abandonnées
- [ ] GET `/api/v1/dashboard/admin/statistiques/sessions`
- [ ] Graphique "Sessions par jour" (line chart)
- [ ] Graphique "Score moyen par jour" (bar chart)

---

## 🎯 Priorité développement

1. **Auth + Dashboard** (Quick win - 2-3 jours)
2. **Utilisateurs** (Facile - 1-2 jours)
3. **Statistiques** (Graphiques - 2-3 jours)
4. **Questions CRUD** (Complexe - 5-7 jours)

---

**Base URL de test: `http://localhost:8080`**
**Credentials: `admin` / `admin123`**
**Documentation API: `http://localhost:8080/swagger-ui.html`**

Bon développement! 🚀
