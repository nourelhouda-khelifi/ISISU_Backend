# ✅ API Admin Questions - Réponse avec estCorrect

Après l'implémentation des AdminChoixDTO et AdminQuestionDTO, l'API admin retourne maintenant les bonnes réponses!

## 📤 Exemple de réponse complète

```bash
GET /api/v1/admin/questions
Authorization: Bearer <admin-token>
```

### Statut 200 OK

```json
[
  {
    "id": 150,
    "enonce": "Quel est le type de données pour un nombre entier?",
    "type": "QCM_SIMPLE",
    "difficulte": "FACILE",
    "ponderation": 1.0,
    "dureeSecondes": 30,
    "actif": true,
    "dateCreation": "2024-04-14T10:00:00",
    "competenceIds": [1, 2],
    "choix": [
      {
        "id": 10,
        "contenu": "string",
        "ordre": 1,
        "estCorrect": false
      },
      {
        "id": 11,
        "contenu": "int",
        "ordre": 2,
        "estCorrect": true  ← ✅ VISIBLE POUR ADMIN
      },
      {
        "id": 12,
        "contenu": "boolean",
        "ordre": 3,
        "estCorrect": false
      }
    ]
  },
  {
    "id": 151,
    "enonce": "Qu'est-ce que l'héritage?",
    "type": "QCM_MULTIPLE",
    "difficulte": "DIFFICILE",
    "ponderation": 2.0,
    "dureeSecondes": 60,
    "actif": true,
    "dateCreation": "2024-04-15T14:30:00",
    "competenceIds": [3, 4],
    "choix": [
      {
        "id": 20,
        "contenu": "Permet de réutiliser du code",
        "ordre": 1,
        "estCorrect": true  ← ✅ CORRECT
      },
      {
        "id": 21,
        "contenu": "Est une relation entre classes",
        "ordre": 2,
        "estCorrect": true  ← ✅ CORRECT
      },
      {
        "id": 22,
        "contenu": "Crée une instance de classe",
        "ordre": 3,
        "estCorrect": false
      }
    ]
  }
]
```

## 🔒 Différences de sécurité

### 1️⃣ Endpoint Public (étudiant) - **SANS** estCorrect

```bash
GET /api/v1/questions
```

```json
{
  "choix": [
    {
      "id": 10,
      "contenu": "string",
      "ordre": 1
      // ❌ estCorrect N'EST PAS INCLUS
    }
  ]
}
```

### 2️⃣ Endpoint Admin - **AVEC** estCorrect

```bash
GET /api/v1/admin/questions
```

```json
{
  "choix": [
    {
      "id": 10,
      "contenu": "string",
      "ordre": 1,
      "estCorrect": false  ← ✅ INCLUS
    }
  ]
}
```

## 🚀 Filtrage Admin

```bash
# Filtrer par type
GET /api/v1/admin/questions?type=QCM_SIMPLE

# Filtrer par difficulté
GET /api/v1/admin/questions?difficulte=DIFFICILE

# Filtrer par état (actif/inactif)
GET /api/v1/admin/questions?actif=false

# Combiner les filtres
GET /api/v1/admin/questions?type=QCM_SIMPLE&difficulte=MOYEN&actif=true
```

## 📋 Récupérer une seule question

```bash
GET /api/v1/admin/questions/150
```

```json
{
  "id": 150,
  "enonce": "Quel est le type de données pour un nombre entier?",
  "type": "QCM_SIMPLE",
  "difficulte": "FACILE",
  "ponderation": 1.0,
  "dureeSecondes": 30,
  "actif": true,
  "dateCreation": "2024-04-14T10:00:00",
  "competenceIds": [1, 2],
  "choix": [
    {
      "id": 10,
      "contenu": "string",
      "ordre": 1,
      "estCorrect": false
    },
    {
      "id": 11,
      "contenu": "int",
      "ordre": 2,
      "estCorrect": true  ← ✅ RÉPONSE CORRECTE VISIBLE
    },
    {
      "id": 12,
      "contenu": "boolean",
      "ordre": 3,
      "estCorrect": false
    }
  ]
}
```

## 💡 Cas d'usage Frontend

```javascript
// Admin voit estCorrect et peut éditer
async function editQuestion(questionId) {
  const response = await fetch(`/api/v1/admin/questions/${questionId}`, {
    headers: { 'Authorization': `Bearer ${adminToken}` }
  });
  
  const adminQuestion = await response.json();
  
  // Admin peut voir qui est correcte
  adminQuestion.choix.forEach(choice => {
    console.log(`${choice.contenu} - Correcte: ${choice.estCorrect}`);
  });
  
  // Admin peut éditer et modifier
  showEditModal(adminQuestion);  // Vue affiche estCorrect en checkbox
}

// Étudiant voit SANS estCorrect
async function showQuestion(questionId) {
  const response = await fetch(`/api/v1/questions/${questionId}`, {
    headers: { 'Authorization': `Bearer ${studentToken}` }
  });
  
  const studentQuestion = await response.json();
  
  // Pas de estCorrect! Étudiant ne sait pas la réponse
  studentQuestion.choix.forEach(choice => {
    console.log(`${choice.contenu}`);  // Pas de "Correcte: ..."
  });
}
```

## ✅ Vérification

- ✅ AdminChoixDTO.java créé avec estCorrect
- ✅ AdminQuestionDTO.java créé avec AdminChoixDTO[]
- ✅ AdminQuestionController modifié:
  - GET /api/v1/admin/questions → AdminQuestionDTO[] (avec estCorrect)
  - GET /api/v1/admin/questions/{id} → AdminQuestionDTO (avec estCorrect)
- ✅ Compilation réussie
- ✅ Sécurité maintenue: étudiant ne voit pas estCorrect

---

**Prochaines étapes:**
1. Tester l'API avec Postman
2. Implémenter le frontend pour afficher estCorrect dans le modal d'édition
3. Tester le filtrage avec différents paramètres
