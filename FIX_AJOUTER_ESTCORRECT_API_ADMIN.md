# ✅ FIX - Ajouter estCorrect aux réponses de l'API Admin

## 🔴 Problème actuel

```json
{
  "data": {
    "id": 150,
    "choix": [
      {
        "id": 10,
        "contenu": "Réponse A",
        "ordre": 1
        // ❌ estCorrect MANQUE!
      }
    ]
  }
}
```

**Pourquoi c'est un problème:**
- Admin ne peut pas voir avec réponse est correcte
- Admin ne peut pas éditer la question
- Impossible de faire des modifications

---

## ✅ Solution: Créer AdminChoixDTO

### 1️⃣ Créer AdminChoixDTO.java

```java
// src/main/java/com/example/demo/questions/presentation/dto/AdminChoixDTO.java

package com.example.demo.questions.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour admin - Inclut estCorrect (non retourné aux users normaux)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminChoixDTO {
    private Long id;
    private String contenu;
    private Integer ordre;
    private boolean estCorrect;  ← AJOUTÉ pour admin
}
```

### 2️⃣ Créer AdminQuestionDTO.java

```java
// src/main/java/com/example/demo/questions/presentation/dto/AdminQuestionDTO.java

package com.example.demo.questions.presentation.dto;

import com.example.demo.questions.domain.enums.NiveauDifficulte;
import com.example.demo.questions.domain.enums.TypeQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour admin questions (Inclut estCorrect des choix)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminQuestionDTO {
    private Long id;
    private String enonce;
    private TypeQuestion type;
    private NiveauDifficulte difficulte;
    private double ponderation;
    private Integer dureeSecondes;
    private boolean actif;
    private LocalDateTime dateCreation;
    private List<Long> competenceIds;
    private List<AdminChoixDTO> choix;  ← Avec estCorrect
}
```

### 3️⃣ Modifier AdminQuestionController - Mapper les DTOs

```java
// Dans AdminQuestionController.java - AJOUTER cette méthode:

private AdminQuestionDTO convertToAdminDTO(Question question) {
    List<AdminChoixDTO> adminChoix = question.getChoix().stream()
        .map(choix -> AdminChoixDTO.builder()
            .id(choix.getId())
            .contenu(choix.getContenu())
            .ordre(choix.getOrdre())
            .estCorrect(choix.isEstCorrect())  ← ICI!
            .build())
        .collect(Collectors.toList());

    return AdminQuestionDTO.builder()
        .id(question.getId())
        .enonce(question.getEnonce())
        .type(question.getType())
        .difficulte(question.getDifficulte())
        .ponderation(question.getPonderation())
        .dureeSecondes(question.getDureeSecondes())
        .actif(question.isActif())
        .dateCreation(question.getDateCreation())
        .competenceIds(question.getCompetences().stream()
            .map(Competence::getId)
            .collect(Collectors.toList()))
        .choix(adminChoix)
        .build();
}

// Puis modifier les endpoints admin:

@GetMapping
public ResponseEntity<ApiResponse<List<AdminQuestionDTO>>> getAllQuestions(
    @RequestParam(required = false) TypeQuestion type,
    @RequestParam(required = false) NiveauDifficulte difficulte,
    @RequestParam(required = false) Boolean actif) {
    
    List<Question> questions = questionService.getAllQuestions();
    // ... filtres ...
    
    List<AdminQuestionDTO> adminDTOs = questions.stream()
        .map(this::convertToAdminDTO)  ← Utiliser le mapper
        .collect(Collectors.toList());
    
    return ResponseEntity.ok(new ApiResponse<>("Questions retrieved", adminDTOs));
}

@GetMapping("/{id}")
public ResponseEntity<ApiResponse<AdminQuestionDTO>> getQuestionById(@PathVariable Long id) {
    Question question = questionService.getQuestionById(id)
        .orElseThrow(() -> new RuntimeException("Question not found"));
    
    AdminQuestionDTO dto = convertToAdminDTO(question);  ← Utiliser le mapper
    return ResponseEntity.ok(new ApiResponse<>("Question retrieved", dto));
}
```

---

## 📤 Retour API après fix

```json
{
  "status": 200,
  "success": true,
  "message": "Questions retrieved",
  "data": [
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
          "estCorrect": false  ← ✅ MAINTENANT INCLUS
        },
        {
          "id": 11,
          "contenu": "int",
          "ordre": 2,
          "estCorrect": true   ← ✅ BONNE RÉPONSE
        },
        {
          "id": 12,
          "contenu": "boolean",
          "ordre": 3,
          "estCorrect": false
        }
      ]
    }
  ]
}
```

---

## 🔐 Sécurité

**Attention!** Ne jamais retourner `estCorrect` aux utilisateurs normaux via:
```
GET /api/v1/questions  ← Retourne ChoixDTO (SANS estCorrect)
GET /api/v1/admin/questions  ← Retourne AdminQuestionDTO (AVEC estCorrect)
```

**Le contrôleur GET /api/v1/questions reste inchangé:**
```java
@GetMapping
@PreAuthorize("hasAnyRole('ETUDIANT_FIE3', 'CANDIDAT_VAE')")
public ResponseEntity<ApiResponse<List<QuestionDTO>>> getActiveQuestions() {
    // Retourne TOUJOURS ChoixDTO sans estCorrect
}
```

---

## 💻 Frontend - Utiliser AdminQuestionDTO

```javascript
// Dans AdminQuestionsPage.vue

async function loadQuestions() {
  const response = await fetch('/api/v1/admin/questions', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  
  const result = await response.json();
  const adminQuestions = result.data;  // AdminQuestionDTO avec estCorrect
  
  // Maintenant on peut voir les bonnes réponses!
  adminQuestions.forEach(q => {
    q.choix.forEach(choice => {
      console.log(`${choice.contenu} - Correcte: ${choice.estCorrect}`);
    });
  });
}
```

---

## 📋 Résumé des changements

| Avant | Après |
|-------|-------|
| ChoixDTO (SANS estCorrect) | AdminChoixDTO (AVEC estCorrect) ✅ |
| QuestionDTO retourné partout | AdminQuestionDTO pour admin uniquement ✅ |
| Admin ne voit pas bonnes réponses ❌ | Admin voit estCorrect ✅ |
| GET /api/v1/admin/questions manquait data | GET /api/v1/admin/questions complet ✅ |

---

**À implémenter! 🚀**
