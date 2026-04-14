# 📋 PROMPT - INTERFACE ADMIN FRONTEND

> **Utilise ce prompt pour développer l'interface d'administration du système ISISU**

---

## 🎯 CONTEXTE GÉNÉRAL

- **Plateforme** : ISISU Platform - Évaluation adaptive pour FIE3 et VAE
- **Role Accès** : ADMIN uniquement (authentification requise)
- **Authentification** : JWT Bearer Token
- **Base API** : `http://localhost:8080/api/v1`
- **Swagger Docs** : `http://localhost:8080/swagger-ui.html`

---

## 📱 PAGES ADMIN À DÉVELOPPER

### 1️⃣ **Dashboard Admin** - Vue d'ensemble générale
**Route** : `/admin`

**API utilisée:**
```
GET /api/v1/dashboard/admin
Response:
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

**Affichage requis:**
- 📊 Cartes KPI : total utilisateurs, étudiants FIE3, candidats VAE
- 📈 Score moyen global (jauge ou graphique)
- ⏱️ Sessions en cours
- 📉 Taux de réussite
- 🎯 Top 5 compétences (meilleure performance)
- ⚠️ Top 5 compétences (lacunes détectées)

---

### 2️⃣ **Gestion des Utilisateurs**
**Route** : `/admin/users`

**API utilisée:**
```
GET /api/v1/dashboard/admin/users?role=ETUDIANT_FIE3&statut=ACTIF
Query Parameters (optionnels):
  - role: ETUDIANT_FIE3 | CANDIDAT_VAE | ADMIN
  - statut: ACTIF | EN_ATTENTE_OTP | SUSPENDU

Response:
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

**⚠️ IMPORTANT - Pagination:**
- Backend retourne TOUS les utilisateurs (pas de pagination d'API)
- PAGINER côté frontend avec JavaScript: `array.slice((page-1)*size, page*size)`
- Afficher 25-50-100 par page (frontend seul)

**Affichage requis:**
- 📋 Tableau avec filtres :
  - Filtre par `role` (ETUDIANT_FIE3, CANDIDAT_VAE, ADMIN)
  - Filtre par `statut` (ACTIF, EN_ATTENTE_OTP, SUSPENDU)
  - Recherche par nom/email
  - Pagination (25-50-100 par page)
- 👤 Colonnes : ID, Nom, Email, Role, Statut, Date Inscription, Dernière Connexion, Sessions, Score Moyen, Taux Réussite
- 🔍 Action : voir le détail utilisateur
- 🚀 Actions rapides : suspendre, activer, résilier

---

### 3️⃣ **Gestion des Questions**
**Route** : `/admin/questions`

#### 📄 Liste des Questions

**API utilisée:**
```
GET /api/v1/questions
Response:
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

**⚠️ IMPORTANT - Sécurité:**
- `choix[].estCorrect` est INTENTIONNELLEMENT masqué (pas retourné)
- Les réponses correctes ne sont jamais visibles au frontend
- Filtres (type, difficulte, actif) à implémenter CÔTÉ FRONTEND sur les données reçues

**Affichage requis:**
- 📋 Tableau avec filtres :
  - Filtre par `type` (QCM_SIMPLE, QCM_MULTIPLE, VRAI_FAUX, REPONSE_LIBRE)
  - Filtre par `difficulte` (FACILE, MOYEN, DIFFICILE)
  - Filtre par statut `actif` (Actif/Inactif)
  - Recherche dans l'énoncé
  - Pagination
- 🟢/⛔ Indicateur actif/inactif (badge)
- ⏱️ Durée en secondes
- 📊 Pondération (1.0, 1.5, 2.0)
- 🏷️ Compétences associées (tags)
- 🔧 Actions :
  - ✏️ Éditer
  - 🗑️ Supprimer
  - 🟢 Activer / ⛔ Désactiver

#### ➕ Créer une Question

**API utilisée:**
```
POST /api/v1/admin/questions
Content-Type: application/json
Authorization: Bearer <token>

Request Body:
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
      "contenu": "Utiliser les bonnes structures de données",
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

Response (201 Created):
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

**Formulaire requis:**
- 📝 Champ `enonce` (textarea large)
- 📌 Select `type` (QCM_SIMPLE, QCM_MULTIPLE, VRAI_FAUX, REPONSE_LIBRE)
- 📌 Select `difficulte` (FACILE, MOYEN, DIFFICILE)
- ⏱️ Input `dureeSecondes` (nombre)
- 🏷️ Multi-select `competenceIds` (liste des compétences disponibles)
- ✅ **Section Choix** :
  - Pour chaque choix : contenu (text), estCorrect (checkbox), ordre (nombre)
  - Bouton "+ Ajouter un choix"
  - Bouton "- Supprimer" pour chaque choix
  - Glisser-déposer pour réordonner les choix (optionnel)
- 💾 Boutons : Créer, Annuler

#### ✏️ Éditer une Question

**API utilisée:**
```
PUT /api/v1/admin/questions/{id}
Request Body: (identique à la création)
Response: (identique à la création)
```

**Identique au formulaire de création** mais avec les données pré-remplies

#### Activer / Désactiver une Question

**API utilisée:**
```
POST /api/v1/admin/questions/{id}/activer
POST /api/v1/admin/questions/{id}/desactiver
```

#### Supprimer une Question

**API utilisée:**
```
DELETE /api/v1/admin/questions/{id}
Response: 204 No Content
```

---

### 4️⃣ **Statistiques des Sessions**
**Route** : `/admin/statistiques`

**API utilisée:**
```
GET /api/v1/dashboard/admin/statistiques/sessions
Response:
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

**Affichage requis:**
- 📊 Cartes KPI : Total sessions, Sessions terminées, Sessions abandonnées
- ⏱️ Temps d'exécution moyen (en minutes)
- 📈 Score moyen global
- 📅 Graphique d'évolution (courbe) : nombre de sessions + score moyen par jour (utiliser `sessionsParJour`)

---

## 🔐 AUTHENTIFICATION & SÉCURITÉ

### Bearer Token
```
Headers:
Authorization: Bearer <JWT_TOKEN>

Example Response Wrapper:
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "Dashboard récupéré",
  "data": { ... }  // Les données réelles ici
}
```

### Credentials de Test
```
Username: admin
Password: admin123
```

### POST /api/v1/auth/login
```
{
  "username": "admin",
  "password": "admin123"
}
Response includes: token, refreshToken, userId, email, role
```

### Gestion des erreurs
```
401 Unauthorized   → Rediriger vers /login
403 Forbidden      → Afficher "Accès refusé - rôle ADMIN requis"
404 Not Found      → Afficher "Ressource introuvable"
500 Server Error   → Afficher "Erreur serveur"
```

### Intercepteur HTTP
- Ajouter automatiquement `Authorization: Bearer <token>` à chaque requête
- Récupérer le token lors du login
- Stocker dans localStorage (ou sessionStorage)
- Rafraîchir le token si expiré
- Rediriger vers login si 401

---

## 📐 ENUMS & TYPES

### TypeQuestion
```
QCM_SIMPLE,
QCM_MULTIPLE,
VRAI_FAUX,
REPONSE_LIBRE
```

### NiveauDifficulte
```
FACILE,
MOYEN,
DIFFICILE
```

### Role
```
ETUDIANT_FIE3,
CANDIDAT_VAE,
ADMIN
```

### StatutCompte
```
ACTIF,
EN_ATTENTE_OTP,
SUSPENDU
```

---

## 🎨 DESIGN & UX

### Header
- Logo ISISU
- Navigation principale (Dashboard, Utilisateurs, Questions, Statistiques)
- Profil utilisateur (role visible) + Déconnexion

### Layout
- Sidebar de navigation (optionnel)
- Breadcrumbs pour la navigation
- Contenu principal responsive

### Composants à utiliser
- **Tableaux** : colonnes triables, filtres, pagination
- **Formulaires** : validation, messages d'erreur
- **Graphiques** : barres, courbes, donut (Chart.js, Recharts ou similaire)
- **Modales** : confirmation avant suppression
- **Toasts** : notifications de succès/erreur

### Couleurs
- 🟦 Primaire : Bleu professionnel
- 🟩 Succès : Vert
- 🟥 Danger : Rouge
- 🟨 Warning : Jaune/Orange

---

## 📦 DÉPENDANCES RECOMMANDÉES

**Framework** : React / Vue / Angular (selon votre choix)

**Librairies essentielles:**
- `axios` ou `fetch-api` : Requêtes HTTP
- `react-router-dom` : Routage (React)
- `chart.js` ou `recharts` : Graphiques
- `react-table` ou `tanstack-table` : Tableau avancé
- `react-hook-form` : Gestion des formulaires
- `zod` ou `yup` : Validation
- `tailwindcss` ou `bootstrap` : Styling

---

## ✅ CHECKLIST D'IMPLÉMENTATION

### Phase 1 : Authentification & Layout
- [ ] Connexion JWT
- [ ] Gardes de route (Admin uniquement)
- [ ] Header avec navigation
- [ ] Persistance du token

### Phase 2 : Dashboard Admin
- [ ] Récupérer et afficher les statistiques générales
- [ ] Graphiques de performance des compétences
- [ ] Section des lacunes

### Phase 3 : Gestion Utilisateurs
- [ ] Tableau avec pagination
- [ ] Filtres (role, statut)
- [ ] Recherche
- [ ] Actions rapides

### Phase 4 : Gestion Questions
- [ ] Liste avec filtres
- [ ] Créer/Éditer/Supprimer
- [ ] Gestion des choix
- [ ] Activer/Désactiver

### Phase 5 : Statistiques
- [ ] Graphiques d'évolution
- [ ] KPI des sessions
- [ ] Taux de réussite/abandon

### Phase 6 : Polish & Optimisation
- [ ] Gestion des erreurs robuste
- [ ] Messages de confirmation/success
- [ ] Responsive design
- [ ] Tests

---

## 🚀 COMMANDES UTILES

```bash
# Swagger Documentation
http://localhost:8080/swagger-ui.html

# Test API (curl example)
curl -X GET "http://localhost:8080/api/v1/dashboard/admin" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Credentials de test
Username: admin
Password: admin123
```

---

## 📞 NOTES IMPORTANTES

1. ✅ **Toutes les requêtes** doivent inclure le header `Authorization: Bearer <TOKEN>`
2. ✅ **Gestion d'erreurs** : Afficher des messages clairs pour l'utilisateur
3. ✅ **Validation** : Valider les données côté client avant envoi au serveur
4. ✅ **Responsive** : L'interface doit fonctionner sur mobile, tablette, desktop
5. ✅ **Accessibilité** : Utiliser les bonnes pratiques ARIA
6. ✅ **Performance** : Lazy loading, pagination, optimalisation des requêtes
7. ✅ **UX** : Loading states, confirmation modales pour actions critiques, toasts notifications

---

**Bon développement! 🚀**
