# 📊 Guide des APIs de Statistiques Dashboard Admin

## 🎯 Vue d'ensemble

Les APIs suivantes ont été créées pour fournir des statistiques compréhensives pour le dashboard administrateur :

- **Statistiques des compétences** - Top compétences et tendances
- **Statistiques des questions** - Répartition par type et difficulté
- **Statistiques des performances** - Distribution des scores et par type d'apprenant
- **Statistiques d'activité** - Sessions et apprenants actifs
- **Heatmap** - Performance compétence/difficulté

---

## 1️⃣ GET /api/v1/dashboard/admin/statistiques/competences

### 📝 Description
Récupère les statistiques des compétences avec les **top 10 compétences** et les **tendances sur 3 semaines**.

### 🔗 URL
```
GET /api/v1/dashboard/admin/statistiques/competences
```

### 🛡️ Authentification
- **Requis** : Bearer Token
- **Rôle** : ADMIN

### 📤 Réponse (200 OK)
```json
{
  "code": 200,
  "message": "Statistiques des compétences récupérées",
  "data": {
    "topCompetences": [
      {
        "id": 1,
        "nom": "SQL Avancé",
        "scoreMoyen": 85.50,
        "nombreApprenants": 15,
        "tauxReussite": 0.93
      },
      {
        "id": 2,
        "nom": "Programmation Java",
        "scoreMoyen": 72.30,
        "nombreApprenants": 12,
        "tauxReussite": 0.78
      }
    ],
    "competencesTendances": [
      {
        "id": 1,
        "nom": "SQL Avancé",
        "semaines": {
          "semaine1": {
            "score": 80.00,
            "nombreTests": 45
          },
          "semaine2": {
            "score": 85.00,
            "nombreTests": 52
          },
          "semaine3": {
            "score": 85.50,
            "nombreTests": 48
          }
        }
      }
    ]
  }
}
```

### 💡 Utilisation
```bash
curl -X GET "http://localhost:8080/api/v1/dashboard/admin/statistiques/competences" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 2️⃣ GET /api/v1/dashboard/admin/statistiques/questions

### 📝 Description
Récupère les statistiques des questions **par type** et **par niveau de difficulté**.

### 🔗 URL
```
GET /api/v1/dashboard/admin/statistiques/questions
```

### 🛡️ Authentification
- **Requis** : Bearer Token
- **Rôle** : ADMIN

### 📤 Réponse (200 OK)
```json
{
  "code": 200,
  "message": "Statistiques des questions récupérées",
  "data": {
    "parType": {
      "QCM_SIMPLE": {
        "nombre": 45,
        "tauxReussite": 0.82,
        "nombreUtilisations": 1250
      },
      "QCM_MULTIPLE": {
        "nombre": 38,
        "tauxReussite": 0.65,
        "nombreUtilisations": 890
      },
      "VRAI_FAUX": {
        "nombre": 22,
        "tauxReussite": 0.88,
        "nombreUtilisations": 560
      },
      "APPARIEMENT": {
        "nombre": 15,
        "tauxReussite": 0.75,
        "nombreUtilisations": 320
      },
      "ORDRE": {
        "nombre": 18,
        "tauxReussite": 0.68,
        "nombreUtilisations": 410
      },
      "TEXTE_TROU": {
        "nombre": 12,
        "tauxReussite": 0.45,
        "nombreUtilisations": 280
      }
    },
    "parDifficulte": {
      "FACILE": {
        "nombre": 45,
        "tauxReussite": 0.92,
        "nombreUtilisations": 2100
      },
      "MOYEN": {
        "nombre": 50,
        "tauxReussite": 0.78,
        "nombreUtilisations": 1800
      },
      "DIFFICILE": {
        "nombre": 25,
        "tauxReussite": 0.55,
        "nombreUtilisations": 810
      }
    }
  }
}
```

### 💡 Utilisation
```bash
curl -X GET "http://localhost:8080/api/v1/dashboard/admin/statistiques/questions" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 3️⃣ GET /api/v1/dashboard/admin/statistiques/performances

### 📝 Description
Récupère les statistiques de **distribution des scores** et les **performances par type d'apprenant**.

### 🔗 URL
```
GET /api/v1/dashboard/admin/statistiques/performances
```

### 🛡️ Authentification
- **Requis** : Bearer Token
- **Rôle** : ADMIN

### 📤 Réponse (200 OK)
```json
{
  "code": 200,
  "message": "Statistiques des performances récupérées",
  "data": {
    "distributionScore": [
      {
        "range": "0-20%",
        "nombre": 2,
        "pourcentage": 0.03
      },
      {
        "range": "20-40%",
        "nombre": 5,
        "pourcentage": 0.07
      },
      {
        "range": "40-60%",
        "nombre": 8,
        "pourcentage": 0.11
      },
      {
        "range": "60-80%",
        "nombre": 18,
        "pourcentage": 0.26
      },
      {
        "range": "80-100%",
        "nombre": 45,
        "pourcentage": 0.65
      }
    ],
    "parTypApprenant": {
      "ETUDIANT_FIE3": {
        "scoreMoyen": 82.50,
        "nombreApprenants": 30,
        "tauxReussite": 0.85
      },
      "CANDIDAT_VAE": {
        "scoreMoyen": 75.20,
        "nombreApprenants": 15,
        "tauxReussite": 0.72
      }
    }
  }
}
```

### 💡 Utilisation
```bash
curl -X GET "http://localhost:8080/api/v1/dashboard/admin/statistiques/performances" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 4️⃣ GET /api/v1/dashboard/admin/statistiques/activite

### 📝 Description
Récupère les statistiques d'**activité** sur une période donnée (sessions et apprenants actifs par jour).

### 🔗 URL
```
GET /api/v1/dashboard/admin/statistiques/activite?periode=7j
```

### 📋 Paramètres de requête
| Paramètre | Type | Défaut | Description |
|-----------|------|--------|-------------|
| periode | string | 7j | Période d'analyse : `7j`, `14j`, `30j`, etc. |

### 🛡️ Authentification
- **Requis** : Bearer Token
- **Rôle** : ADMIN

### 📤 Réponse (200 OK)
```json
{
  "code": 200,
  "message": "Statistiques d'activité récupérées",
  "data": {
    "sessionsParJour": [
      {
        "date": "2026-04-08",
        "nombre": 5,
        "dureeParMoyenne": 45.00,
        "completionRate": 0.80
      },
      {
        "date": "2026-04-09",
        "nombre": 8,
        "dureeParMoyenne": 50.25,
        "completionRate": 0.75
      },
      {
        "date": "2026-04-10",
        "nombre": 6,
        "dureeParMoyenne": 48.50,
        "completionRate": 0.83
      }
    ],
    "apprenantsActifs": [
      {
        "date": "2026-04-08",
        "nombre": 4
      },
      {
        "date": "2026-04-09",
        "nombre": 6
      },
      {
        "date": "2026-04-10",
        "nombre": 5
      }
    ],
    "totalSessions": 19,
    "totalApprenantsActifs": 15
  }
}
```

### 💡 Utilisation
```bash
# Dernières 7 jours (défaut)
curl -X GET "http://localhost:8080/api/v1/dashboard/admin/statistiques/activite" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 30 derniers jours
curl -X GET "http://localhost:8080/api/v1/dashboard/admin/statistiques/activite?periode=30j" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 14 derniers jours
curl -X GET "http://localhost:8080/api/v1/dashboard/admin/statistiques/activite?periode=14j" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 5️⃣ GET /api/v1/dashboard/admin/statistiques/heatmap

### 📝 Description
Récupère une **heatmap** montrant la performance pour chaque compétence selon le niveau de difficulté.

### 🔗 URL
```
GET /api/v1/dashboard/admin/statistiques/heatmap
```

### 🛡️ Authentification
- **Requis** : Bearer Token
- **Rôle** : ADMIN

### 📤 Réponse (200 OK)
```json
{
  "code": 200,
  "message": "Heatmap des statistiques récupérée",
  "data": {
    "competenceParDifficulte": [
      {
        "competence": "SQL Avancé",
        "competenceId": 1,
        "performanceParDifficulte": {
          "FACILE": 0.95,
          "MOYEN": 0.82,
          "DIFFICILE": 0.65
        }
      },
      {
        "competence": "Programmation Java",
        "competenceId": 2,
        "performanceParDifficulte": {
          "FACILE": 0.88,
          "MOYEN": 0.71,
          "DIFFICILE": 0.50
        }
      },
      {
        "competence": "Gestion de Projet",
        "competenceId": 3,
        "performanceParDifficulte": {
          "FACILE": 0.92,
          "MOYEN": 0.78,
          "DIFFICILE": 0.62
        }
      }
    ]
  }
}
```

### 💡 Utilisation
```bash
curl -X GET "http://localhost:8080/api/v1/dashboard/admin/statistiques/heatmap" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🔐 Authentification

### Format du Bearer Token
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Obtenir un token
```bash
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d {
    "email": "admin@isisu.fr",
    "motDePasse": "password123"
  }
```

---

## ✅ Codes de Réponse

| Code | Description |
|------|-------------|
| 200 | Statistiques récupérées avec succès |
| 400 | Paramètres invalides |
| 401 | Non authentifié |
| 403 | Accès refusé (non ADMIN) |
| 500 | Erreur serveur |

---

## 📊 Architecture Implémentée

### 1. **DTOs** (Data Transfer Objects)
- `StatistiquesCompetencesDTO.java` - Container pour stats compétences
- `StatistiquesQuestionsDTO.java` - Container pour stats questions
- `StatistiquesPerformancesDTO.java` - Container pour stats performances
- `StatistiquesActiviteDTO.java` - Container pour stats activité
- `StatistiquesHeatmapDTO.java` - Container pour la heatmap

### 2. **Service**
- `StatistiquesService.java` - Logique métier pour calculer les statistiques

### 3. **Controller**
- Endpoints ajoutés à `DashboardAdminController.java`

---

## 🚀 Exemple d'intégration Frontend

### JavaScript/Fetch API
```javascript
// Récupérer les statistiques des compétences
async function getCompetenceStats() {
  const token = localStorage.getItem('authToken');
  
  const response = await fetch(
    'http://localhost:8080/api/v1/dashboard/admin/statistiques/competences',
    {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    }
  );
  
  const data = await response.json();
  console.log(data.data.topCompetences);
  return data.data;
}

// Récupérer l'activité de la dernière semaine
async function getActivityStats() {
  const token = localStorage.getItem('authToken');
  
  const response = await fetch(
    'http://localhost:8080/api/v1/dashboard/admin/statistiques/activite?periode=7j',
    {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    }
  );
  
  const data = await response.json();
  return data.data;
}
```

### TypeScript/Angular
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StatistiquesService {
  private apiUrl = 'http://localhost:8080/api/v1/dashboard/admin/statistiques';

  constructor(private http: HttpClient) {}

  getCompetenceStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/competences`);
  }

  getQuestionStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/questions`);
  }

  getPerformanceStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/performances`);
  }

  getActivityStats(periode: string = '7j'): Observable<any> {
    return this.http.get(`${this.apiUrl}/activite?periode=${periode}`);
  }

  getHeatmapStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/heatmap`);
  }
}
```

---

## 🔧 Configuration Required

Assurez-vous que les repository suivants sont injectés dans le service :
- `SessionTestRepository` ✅
- `ReponseEtudiantRepository` ✅
- `QuestionRepository` ✅
- `UtilisateurRepository` ✅
- `CompetenceRepository` ✅

---

## 📌 Notes importantes

1. **Performance** : Pour les statistiques complètes, évitez de limiter les résultats en base de données
2. **Calcul** : Les calculs de pourcentage sont arrondis à 2 décimales
3. **Periode** : Format accepté : `7j`, `14j`, `30j`, etc.
4. **Top Competences** : Seules les 10 meilleures compétences sont retournées
5. **Heatmap** : Seules les 10 premiers compétences sont affichées

---

## 🐛 Troubleshooting

### Erreur 403 - Accès refusé
```
Solution: Assurez-vous que votre utilisateur a le rôle ADMIN
```

### Erreur 401 - Non authentifié
```
Solution: Vérifiez le format du Bearer Token
Attendu: Authorization: Bearer {token}
```

### Aucune donnée retournée
```
Solution: Vérifiez que la base de données contient des données
- Sessions de test
- Réponses d'étudiants
- Questions
- Compétences
```

---

## 📞 Support

Pour toute question ou problème, consultez :
- [README.md](README.md)
- [API_REFERENCE_COMPLETE.md](API_REFERENCE_COMPLETE.md)

