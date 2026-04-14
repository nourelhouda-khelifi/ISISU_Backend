# ✅ VÉRIFICATION BACKEND - SPÉCIFICATIONS RÉELLES

> Ce document confirme les détails **EXACTS** du backend pour la Phase 1 du frontend admin

---

## 🔐 AUTHENTIFICATION & CREDENTIALS

### Rôle Admin
```
✅ CONFIRMÉ: Rôle = "ADMIN" (pas ROLE_ADMIN, pas ADMINISTRATEUR)
Enum: com.example.demo.auth.domain.enums.Role
Values: ETUDIANT_FIE3, CANDIDAT_VAE, ADMIN
```

### Credentials de Test
```bash
✅ CONFIRMÉ via application.properties:
username: admin
password: ${SECURITY_USER_PASSWORD}  # À récupérer des variables d'env locales

# Docker compose default:
SPRING_SECURITY_USER_NAME: admin
```

### Login Endpoint
```bash
POST /api/v1/auth/login
Content-Type: application/json

Request Body:
{
  "username": "admin",
  "password": "admin123"
}

Response Format:
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Connexion reussie",
  "data": {
    "token": "eyJhbGc...",
    "refreshToken": "...",
    "userId": 1,
    "email": "admin@example.com",
    "role": "ADMIN"
  }
}

⚠️ À UTILISER: Authorization: Bearer <token>
```

---

## 📱 ENDPOINTS CONFIRMÉS

### Format de réponse STANDARD
```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Texte de confirmation",
  "data": {
    // Données réelles ici
  }
}
```
**Classe**: `com.example.demo.common.response.ApiResponse<T>`

---

## 1️⃣ DASHBOARD ADMIN

### Endpoint
```
GET /api/v1/dashboard/admin
Authorization: Bearer <token>
Rôle requis: ADMIN
```

### Response EXACT (ApiResponse wrapper)
```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Dashboard récupéré",
  "data": {
    "totalUtilisateurs": 150,
    "totalEtudiantsFIE3": 100,
    "totalCandidatsVAE": 50,
    "scoreMoyenGlobal": 0.75,
    "sessionsEnCours": 8,
    "tauxReussite": 65.3,
    "competencesTopPerformance": [
      {
        "id": 1,
        "nom": "Programmation Python",
        "scoreMoyen": 85.2,
        "nombreApprenants": 120,
        "tauxAcquisition": 0.852,
        "evolution": "MOMENTUM"
      }
    ],
    "competencesLacunes": [
      {
        "id": 2,
        "nom": "Algorithmique",
        "scoreMoyen": 45.6,
        "nombreApprenants": 115,
        "tauxAcquisition": 0.456,
        "evolution": "REGRESSION"
      }
    ]
  }
}
```

**Types DTO utilisés:**
- `AdminDashboardDTO`
- `CompetenceStatsDTO` (Pour topPerformance et lacunes)

---

## 2️⃣ GESTION UTILISATEURS

### Endpoint Utilisateurs
```
GET /api/v1/dashboard/admin/users?role=ETUDIANT_FIE3&statut=ACTIF
Authorization: Bearer <token>
Rôle requis: ADMIN

Query Parameters:
- role (optional): ETUDIANT_FIE3 | CANDIDAT_VAE | ADMIN
- statut (optional): ACTIF | EN_ATTENTE_OTP | SUSPENDU

Format: Query params, PAS de body !
```

### Response EXACT
```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Liste des utilisateurs",
  "data": [
    {
      "id": 1,
      "nom": "Jean",
      "prenom": "Dupont",
      "email": "jean@example.com",
      "role": "ETUDIANT_FIE3",
      "statut": "ACTIF",
      "nombreSessions": 5,
      "scoreMoyen": 72.5,
      "derniereConnexion": "2024-04-12T14:22:00"
    },
    {
      "id": 2,
      "nom": "Marie",
      "prenom": "Martin",
      "email": "marie@example.com",
      "role": "CANDIDAT_VAE",
      "statut": "EN_ATTENTE_OTP",
      "nombreSessions": 2,
      "scoreMoyen": 58.3,
      "derniereConnexion": "2024-04-10T09:15:00"
    }
  ]
}
```

**Types DTO utilisés:**
- `UtilisateurDashboardDTO` (dans un array)

**Enums valides:**
```
Role: ETUDIANT_FIE3, CANDIDAT_VAE, ADMIN
StatutCompte: ACTIF, EN_ATTENTE_OTP, SUSPENDU
```

### ⚠️ PAGINATION
```
❌ PAS DE PAGINATION IMPLÉMENTÉE dans getUtilisateurs()
Le service récupère TOUS les utilisateurs (findAll)
La pagination doit être faite CÔTÉ FRONTEND si besoin
```

---

## 3️⃣ GESTION QUESTIONS

### 3a. GET Toutes les Questions
```
GET /api/v1/questions
Authorization: Bearer <token>
Rôle requis: Aucun (tous les utilisateurs)
```

### Response
```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Liste des questions",
  "data": [
    {
      "id": 1,
      "enonce": "Quelle est la capitale de la France ?",
      "type": "QCM_SIMPLE",
      "difficulte": "FACILE",
      "ponderation": 1.0,
      "dureeSecondes": 30,
      "actif": true,
      "dateCreation": "2024-03-01T10:00:00",
      "competenceIds": [1, 2, 5],
      "choix": [
        {
          "id": 10,
          "contenu": "Paris",
          "ordre": 1
          // estCorrect intentionnellement masqué pour sécurité
        },
        {
          "id": 11,
          "contenu": "Lyon",
          "ordre": 2
        }
      ]
    }
  ]
}
```

**Annotations admin:**
```
⚠️ choix[].estCorrect est MASQUÉ dans le DTO
Le toDTO() intentionnellement ne le retourne pas
```

### 3b. CRÉER une Question (Admin)
```
POST /api/v1/admin/questions
Authorization: Bearer <token>
Rôle requis: ADMIN
Content-Type: application/json
```

### Request Body
```json
{
  "enonce": "Quelle est la meilleure pratique en Python ?",
  "type": "QCM_MULTIPLE",
  "difficulte": "MOYEN",
  "dureeSecondes": 45,
  "competenceIds": [3, 7],
  "choix": [
    {
      "contenu": "Utiliser des listes partout",
      "estCorrect": false,
      "ordre": 1
    },
    {
      "contenu": "Utiliser les bonnes structures",
      "estCorrect": true,
      "ordre": 2
    },
    {
      "contenu": "Ignorer la documentation",
      "estCorrect": false,
      "ordre": 3
    }
  ]
}
```

**Classe DTO**: `CreateQuestionDTO`

### Response (201 Created)
```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 201,
  "success": true,
  "message": "Question créée",
  "data": {
    "id": 150,
    "enonce": "...",
    "type": "QCM_MULTIPLE",
    "difficulte": "MOYEN",
    "ponderation": 1.5,
    "dureeSecondes": 45,
    "actif": true,
    "dateCreation": "2024-04-14T10:30:00",
    "competenceIds": [3, 7],
    "choix": [...]
  }
}
```

### 3c. MODIFIER une Question (Admin)
```
PUT /api/v1/admin/questions/{id}
Authorization: Bearer <token>
Rôle requis: ADMIN
```

**Request Body**: Identique à la création (mais inclut aussi `actif`)

**Classe DTO**: `UpdateQuestionDTO`

### 3d. SUPPRIMER une Question (Admin)
```
DELETE /api/v1/admin/questions/{id}
Authorization: Bearer <token>
Rôle requis: ADMIN
Response: 204 No Content
```

### 3e. ACTIVER une Question
```
POST /api/v1/admin/questions/{id}/activer
Authorization: Bearer <token>
Rôle requis: ADMIN
Response: QuestionDTO (actif=true)
```

### 3f. DÉSACTIVER une Question
```
POST /api/v1/admin/questions/{id}/desactiver
Authorization: Bearer <token>
Rôle requis: ADMIN
Response: QuestionDTO (actif=false)
```

**Enums pour Questions:**
```
TypeQuestion: QCM_SIMPLE, QCM_MULTIPLE, VRAI_FAUX, REPONSE_LIBRE

NiveauDifficulte: FACILE, MOYEN, DIFFICILE
  - FACILE → ponderation: 1.0
  - MOYEN → ponderation: 1.5
  - DIFFICILE → ponderation: 2.0
```

---

## 4️⃣ STATISTIQUES DES SESSIONS

### Endpoint
```
GET /api/v1/dashboard/admin/statistiques/sessions
Authorization: Bearer <token>
Rôle requis: ADMIN
```

### Response EXACT
```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Statistiques récupérées",
  "data": {
    "totalSessions": 450,
    "sessionsTerminees": 442,
    "sessionsAbandonnes": 8,
    "dureeParMoyenne": 28,
    "scoreParMoyenne": 67.5,
    "sessionsParJour": [
      {
        "date": "2024-04-01",
        "count": 45,
        "scoreAverage": 62.1
      },
      {
        "date": "2024-04-02",
        "count": 48,
        "scoreAverage": 64.3
      }
    ]
  }
}
```

**Types DTO utilisés:**
- `SessionStatisticsDTO`
- `DailySessionCountDTO` (dans l'array sessionsParJour)

---

## ❌ CE QUI N'EXISTE PAS / À IMPLÉMENTER

### Page uniquement, PAS d'API backend:

1. **Détail d'un utilisateur**
   - Pas d'endpoint pour voir les détails complets d'un utilisateur
   - À créer ou utiliser les données de la liste

2. **Pagination globale**
   - Les réponses retournent TOUS les résultats
   - Pagination doit être côté frontend
   - Pas de `?page=0&size=25`
   - Implémenter: `slice()` / `paginate()` en JS côté frontend

3. **Recherche globale**
   - Pas d'API de recherche
   - Filtrer côté frontend avec `filter()` sur les arrays

4. **Graphiques des statistiques**
   - Les données existent mais pas besoin d'API supplémentaire
   - Utiliser Chart.js / Recharts avec `sessionsParJour`

---

## 📊 RÉSUMÉ DES ENDPOINTS

| Endpoint | Méthode | Rôle | URL | Response |
|----------|---------|------|-----|----------|
| Dashboard | GET | ADMIN | `/api/v1/dashboard/admin` | AdminDashboardDTO |
| Utilisateurs | GET | ADMIN | `/api/v1/dashboard/admin/users` | UtilisateurDashboardDTO[] |
| Statistiques | GET | ADMIN | `/api/v1/dashboard/admin/statistiques/sessions` | SessionStatisticsDTO |
| Questions (list) | GET | Any | `/api/v1/questions` | QuestionDTO[] |
| Question (create) | POST | ADMIN | `/api/v1/admin/questions` | QuestionDTO |
| Question (update) | PUT | ADMIN | `/api/v1/admin/questions/{id}` | QuestionDTO |
| Question (delete) | DELETE | ADMIN | `/api/v1/admin/questions/{id}` | 204 |
| Question (activer) | POST | ADMIN | `/api/v1/admin/questions/{id}/activer` | QuestionDTO |
| Question (désactiver) | POST | ADMIN | `/api/v1/admin/questions/{id}/desactiver` | QuestionDTO |

---

## 🎯 CHECKLIST POUR LE PROMPT FRONTEND

- ✅ Rôle exacte: **ADMIN** (confirmé)
- ✅ Format header: **Authorization: Bearer <token>** (standard)
- ✅ Endpoints 1,2,3,4 existent et sont testés
- ✅ Structure de réponse: **ApiResponse<T>** wrapper
- ✅ Pagination: **À implémenter côté frontend** (pas d'API)
- ✅ Filtres: **Query params pour Users** (role, statut)
- ❌ Questions: **Pas de GET avec filtres** (à implémenter côté frontend)
- ✅ Post/Put/Delete Questions: **Tous implémentés**
- ✅ Enums: **Confirmés et documentés**

---

## 🧪 TESTER AVEC CURL (local)

```bash
# 1. Login et récupérer token
TOKEN=$(curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  -s | jq -r '.data.token')

echo "Token: $TOKEN"

# 2. Dashboard
curl -X GET "http://localhost:8080/api/v1/dashboard/admin" \
  -H "Authorization: Bearer $TOKEN" \
  -s | jq '.data'

# 3. Users
curl -X GET "http://localhost:8080/api/v1/dashboard/admin/users?role=ETUDIANT_FIE3" \
  -H "Authorization: Bearer $TOKEN" \
  -s | jq '.data'

# 4. Statistiques
curl -X GET "http://localhost:8080/api/v1/dashboard/admin/statistiques/sessions" \
  -H "Authorization: Bearer $TOKEN" \
  -s | jq '.data'

# 5. Questions
curl -X GET "http://localhost:8080/api/v1/questions" \
  -H "Authorization: Bearer $TOKEN" \
  -s | jq '.data'
```

---

## 📝 MODIFICATION DU PROMPT INITIAL

Les points à corriger dans le prompt initial:

1. ✅ **Response wrapper**: Ajouter `timestamp` et structure `ApiResponse`
2. ✅ **Pagination**: Clarifier qu'elle n'existe pas au backend
3. ✅ **Filtres Questions**: Pas d'API, à implementer côté frontend
4. ⚠️ **Message d'erreur d'authentification**: À prévoir

---

**Prêt pour Phase 1 ! ✅**
