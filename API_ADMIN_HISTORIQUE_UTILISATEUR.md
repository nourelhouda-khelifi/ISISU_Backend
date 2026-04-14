# 🔍 API HISTORIQUE UTILISATEUR - Guide Complet

> **Endpoint Admin: Récupérer tous les sessions + résultats d'un utilisateur**
> Inclut: Backend + Frontend + UI

---

## 📍 Endpoint

```
GET /api/v1/admin/users/{userId}/historique
Authorization: Bearer <token>
(Rôle: ADMIN)
```

**Exemple:**
```
GET http://localhost:8080/api/v1/admin/users/125/historique
```

---

## 📤 Response: 200 OK

```json
{
  "timestamp": "2024-04-14T10:30:00Z",
  "status": 200,
  "success": true,
  "message": "User history retrieved",
  "data": {
    "userId": 125,
    "nom": "Dupont",
    "prenom": "Jean",
    "email": "jean.dupont@univ.fr",
    "role": "ETUDIANT_FIE3",
    "totalSessions": 12,
    "scoreMoyen": 14.5,
    "tauxReussite": 78.5,
    "sessions": [
      {
        "sessionId": 1001,
        "dateDebut": "2024-04-14T10:00:00",
        "dateFin": "2024-04-14T10:45:00",
        "dureeTotalSecondes": 2700,
        "statut": "TERMINEE",
        "scoreTotal": 16.5,
        "tauxReussite": 85.0,
        "nombreQuestionsRepondues": 10,
        "nombreQuestionsJustes": 8,
        "competences": [
          {
            "competenceId": 1,
            "nom": "Java Core",
            "scorePartiel": 5.0,
            "nombreQuestions": 3,
            "nombreJustes": 3
          },
          {
            "competenceId": 2,
            "nom": "Spring Boot",
            "scorePartiel": 11.5,
            "nombreQuestions": 7,
            "nombreJustes": 5
          }
        ],
        "reponses": [
          {
            "questionId": 150,
            "enonce": "Quel est le type de données pour un nombre entier en Java?",
            "type": "QCM_SIMPLE",
            "difficulte": "FACILE",
            "competenceId": 1,
            "reponseUtilisateur": "int",
            "estCorrect": true,
            "tempsReponse": 15,
            "pointsGagnes": 1.0
          },
          {
            "questionId": 151,
            "enonce": "Quels sont les avantages de Spring Boot?",
            "type": "QCM_MULTIPLE",
            "difficulte": "MOYEN",
            "competenceId": 2,
            "reponseUtilisateur": ["Configuration auto", "Embedded server"],
            "reponseAttenue": ["Configuration auto", "Embedded server", "Maven management"],
            "estCorrect": false,
            "tempsReponse": 45,
            "pointsGagnes": 0.0
          },
          {
            "questionId": 152,
            "enonce": "Java est un langage compilé",
            "type": "VRAI_FAUX",
            "difficulte": "FACILE",
            "competenceId": 1,
            "reponseUtilisateur": "VRAI",
            "estCorrect": true,
            "tempsReponse": 10,
            "pointsGagnes": 1.0
          },
          {
            "questionId": 153,
            "enonce": "Expliquez le polymorphisme",
            "type": "REPONSE_LIBRE",
            "difficulte": "DIFFICILE",
            "competenceId": 1,
            "reponseUtilisateur": "Le polymorphisme est...",
            "statutCorrection": "EN_ATTENTE",
            "pointsGagnes": 0.0
          }
        ]
      },
      {
        "sessionId": 1000,
        "dateDebut": "2024-04-13T14:00:00",
        "dateFin": "2024-04-13T14:30:00",
        "dureeTotalSecondes": 1800,
        "statut": "TERMINEE",
        "scoreTotal": 12.5,
        "tauxReussite": 71.0,
        "nombreQuestionsRepondues": 14,
        "nombreQuestionsJustes": 10,
        "competences": [...],
        "reponses": [...]
      }
    ]
  }
}
```

---

## 🔧 Backend - Implémentation

### 1️⃣ CreerController - Endpoint

```java
// src/main/java/com/example/demo/admin/AdminUserController.java

package com.example.demo.admin;

import com.example.demo.common.ApiResponse;
import com.example.demo.dashboard.DashboardService;
import com.example.demo.dashboard.dto.UserHistoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

  @Autowired
  private DashboardService dashboardService;

  /**
   * Récupérer l'historique complet d'un utilisateur
   * - Toutes les sessions
   * - Tous les résultats/réponses
   * - Score par compétence
   * - Détails de correction (si applicable)
   */
  @GetMapping("/{userId}/historique")
  public ResponseEntity<ApiResponse<UserHistoryDTO>> getUserHistory(
      @PathVariable Long userId) {
    
    try {
      UserHistoryDTO history = dashboardService.getUserHistory(userId);
      
      return ResponseEntity.ok(new ApiResponse<>(
          "User history retrieved successfully",
          history
      ));
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(new ApiResponse<>(
          false,
          404,
          "User not found: " + e.getMessage(),
          null
      ));
    }
  }
}
```

### 2️⃣ DashboardService - Logique métier

```java
// src/main/java/com/example/demo/dashboard/DashboardService.java

package com.example.demo.dashboard;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.dashboard.dto.*;
import com.example.demo.session.entity.Session;
import com.example.demo.session.entity.SessionResponse;
import com.example.demo.session.repository.SessionRepository;
import com.example.demo.questions.entity.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SessionRepository sessionRepository;

  /**
   * Récupérer l'historique complètement d'un utilisateur
   */
  public UserHistoryDTO getUserHistory(Long userId) {
    // 1. Vérifier que l'utilisateur existe
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

    // 2. Charger toutes ses sessions
    List<Session> sessions = sessionRepository.findByUserId(userId);

    // 3. Calculer les statistiques globales
    double scoreMoyen = sessions.stream()
        .mapToDouble(Session::getScoreFinal)
        .average()
        .orElse(0.0);

    long sessionsTerminees = sessions.stream()
        .filter(s -> s.getStatut().equals("TERMINEE"))
        .count();

    double tauxReussite = sessions.isEmpty() ? 0 :
        (sessionsTerminees * 100.0 / sessions.size());

    // 4. Construire le DTO historique
    UserHistoryDTO history = new UserHistoryDTO();
    history.setUserId(user.getId());
    history.setNom(user.getNom());
    history.setPrenom(user.getPrenom());
    history.setEmail(user.getEmail());
    history.setRole(user.getRole().toString());
    history.setTotalSessions(sessions.size());
    history.setScoreMoyen(scoreMoyen);
    history.setTauxReussite(tauxReussite);

    // 5. Converter les sessions avec détails
    List<SessionHistoryDTO> sessionsDTO = sessions.stream()
        .sorted(Comparator.comparing(Session::getDateFin).reversed())  ← Plus récent d'abord
        .map(this::convertSessionToHistoryDTO)
        .collect(Collectors.toList());

    history.setSessions(sessionsDTO);
    return history;
  }

  /**
   * Convertir une Session en SessionHistoryDTO avec tous les détails
   */
  private SessionHistoryDTO convertSessionToHistoryDTO(Session session) {
    SessionHistoryDTO dto = new SessionHistoryDTO();
    
    dto.setSessionId(session.getId());
    dto.setDateDebut(session.getDateDebut());
    dto.setDateFin(session.getDateFin());
    
    // Durée en secondes
    long durationSeconds = (session.getDateFin().getTime() - 
                           session.getDateDebut().getTime()) / 1000;
    dto.setDureeTotalSecondes(durationSeconds);
    
    dto.setStatut(session.getStatut());
    dto.setScoreTotal(session.getScoreFinal());
    
    // Taux de réussite
    List<SessionResponse> responses = session.getResponses();
    long correctCount = responses.stream()
        .filter(SessionResponse::isCorrect)
        .count();
    
    if (responses.isEmpty()) {
      dto.setTauxReussite(0.0);
      dto.setNombreQuestionsRepondues(0);
      dto.setNombreQuestionsJustes(0);
    } else {
      dto.setTauxReussite((correctCount * 100.0) / responses.size());
      dto.setNombreQuestionsRepondues(responses.size());
      dto.setNombreQuestionsJustes((int) correctCount);
    }

    // Grouper par compétence
    Map<Long, List<SessionResponse>> responsesByCompetence = responses.stream()
        .collect(Collectors.groupingBy(r -> r.getQuestion().getCompetence().getId()));

    List<CompetenceScoreDTO> competences = responsesByCompetence.entrySet()
        .stream()
        .map(entry -> {
          Long competenceId = entry.getKey();
          List<SessionResponse> compResponses = entry.getValue();
          
          CompetenceScoreDTO comp = new CompetenceScoreDTO();
          comp.setCompetenceId(competenceId);
          comp.setNom(compResponses.get(0).getQuestion().getCompetence().getNom());
          
          double compScore = compResponses.stream()
              .mapToDouble(SessionResponse::getPointsGagnes)
              .sum();
          comp.setScorePartiel(compScore);
          comp.setNombreQuestions(compResponses.size());
          
          long justesComp = compResponses.stream()
              .filter(SessionResponse::isCorrect)
              .count();
          comp.setNombreJustes((int) justesComp);
          
          return comp;
        })
        .collect(Collectors.toList());

    dto.setCompetences(competences);

    // Détail de chaque réponse
    List<ResponseDetailDTO> responsesDetails = responses.stream()
        .map(this::convertResponseToDetailDTO)
        .collect(Collectors.toList());

    dto.setReponses(responsesDetails);
    return dto;
  }

  /**
   * Convertir une SessionResponse en ResponseDetailDTO
   */
  private ResponseDetailDTO convertResponseToDetailDTO(SessionResponse response) {
    ResponseDetailDTO dto = new ResponseDetailDTO();
    
    Question question = response.getQuestion();
    
    dto.setQuestionId(question.getId());
    dto.setEnonce(question.getEnonce());
    dto.setType(question.getType().toString());
    dto.setDifficulte(question.getDifficulte().toString());
    dto.setCompetenceId(question.getCompetence().getId());
    
    // Réponse utilisateur (format selon le type)
    dto.setReponseUtilisateur(response.getReponseUtilisateur());
    
    // Type de question
    String typeQuestion = question.getType().toString();
    
    if (typeQuestion.equals("QCM_MULTIPLE")) {
      // Récupérer les bonnes réponses aussi
      List<String> bonnesReponses = question.getChoix().stream()
          .filter(c -> c.isEstCorrect())
          .map(c -> c.getContenu())
          .collect(Collectors.toList());
      dto.setReponseAttenue(bonnesReponses);
    }
    
    if (typeQuestion.equals("REPONSE_LIBRE")) {
      // Montrer le statut de correction
      dto.setStatutCorrection(response.getStatutCorrection());
    }
    
    dto.setEstCorrect(response.isCorrect());
    dto.setTempsReponse(response.getTempsReponse());
    dto.setPointsGagnes(response.getPointsGagnes());
    
    return dto;
  }
}
```

### 3️⃣ DTOs

```java
// src/main/java/com/example/demo/dashboard/dto/UserHistoryDTO.java

package com.example.demo.dashboard.dto;

import java.util.List;

public class UserHistoryDTO {
  private Long userId;
  private String nom;
  private String prenom;
  private String email;
  private String role;
  private int totalSessions;
  private double scoreMoyen;
  private double tauxReussite;
  private List<SessionHistoryDTO> sessions;

  // Getters et setters
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  
  public String getNom() { return nom; }
  public void setNom(String nom) { this.nom = nom; }
  
  public String getPrenom() { return prenom; }
  public void setPrenom(String prenom) { this.prenom = prenom; }
  
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  
  public int getTotalSessions() { return totalSessions; }
  public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
  
  public double getScoreMoyen() { return scoreMoyen; }
  public void setScoreMoyen(double scoreMoyen) { this.scoreMoyen = scoreMoyen; }
  
  public double getTauxReussite() { return tauxReussite; }
  public void setTauxReussite(double tauxReussite) { this.tauxReussite = tauxReussite; }
  
  public List<SessionHistoryDTO> getSessions() { return sessions; }
  public void setSessions(List<SessionHistoryDTO> sessions) { this.sessions = sessions; }
}
```

```java
// src/main/java/com/example/demo/dashboard/dto/SessionHistoryDTO.java

package com.example.demo.dashboard.dto;

import java.util.Date;
import java.util.List;

public class SessionHistoryDTO {
  private Long sessionId;
  private Date dateDebut;
  private Date dateFin;
  private long dureeTotalSecondes;
  private String statut;
  private double scoreTotal;
  private double tauxReussite;
  private int nombreQuestionsRepondues;
  private int nombreQuestionsJustes;
  private List<CompetenceScoreDTO> competences;
  private List<ResponseDetailDTO> reponses;

  // Getters et setters
  public Long getSessionId() { return sessionId; }
  public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
  
  public Date getDateDebut() { return dateDebut; }
  public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
  
  public Date getDateFin() { return dateFin; }
  public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
  
  public long getDureeTotalSecondes() { return dureeTotalSecondes; }
  public void setDureeTotalSecondes(long dureeTotalSecondes) { 
    this.dureeTotalSecondes = dureeTotalSecondes; 
  }
  
  public String getStatut() { return statut; }
  public void setStatut(String statut) { this.statut = statut; }
  
  public double getScoreTotal() { return scoreTotal; }
  public void setScoreTotal(double scoreTotal) { this.scoreTotal = scoreTotal; }
  
  public double getTauxReussite() { return tauxReussite; }
  public void setTauxReussite(double tauxReussite) { this.tauxReussite = tauxReussite; }
  
  public int getNombreQuestionsRepondues() { return nombreQuestionsRepondues; }
  public void setNombreQuestionsRepondues(int nombreQuestionsRepondues) { 
    this.nombreQuestionsRepondues = nombreQuestionsRepondues; 
  }
  
  public int getNombreQuestionsJustes() { return nombreQuestionsJustes; }
  public void setNombreQuestionsJustes(int nombreQuestionsJustes) { 
    this.nombreQuestionsJustes = nombreQuestionsJustes; 
  }
  
  public List<CompetenceScoreDTO> getCompetences() { return competences; }
  public void setCompetences(List<CompetenceScoreDTO> competences) { 
    this.competences = competences; 
  }
  
  public List<ResponseDetailDTO> getReponses() { return reponses; }
  public void setReponses(List<ResponseDetailDTO> reponses) { this.reponses = reponses; }
}
```

```java
// src/main/java/com/example/demo/dashboard/dto/CompetenceScoreDTO.java

package com.example.demo.dashboard.dto;

public class CompetenceScoreDTO {
  private Long competenceId;
  private String nom;
  private double scorePartiel;
  private int nombreQuestions;
  private int nombreJustes;

  // Getters et setters
  public Long getCompetenceId() { return competenceId; }
  public void setCompetenceId(Long competenceId) { this.competenceId = competenceId; }
  
  public String getNom() { return nom; }
  public void setNom(String nom) { this.nom = nom; }
  
  public double getScorePartiel() { return scorePartiel; }
  public void setScorePartiel(double scorePartiel) { this.scorePartiel = scorePartiel; }
  
  public int getNombreQuestions() { return nombreQuestions; }
  public void setNombreQuestions(int nombreQuestions) { 
    this.nombreQuestions = nombreQuestions; 
  }
  
  public int getNombreJustes() { return nombreJustes; }
  public void setNombreJustes(int nombreJustes) { this.nombreJustes = nombreJustes; }
}
```

```java
// src/main/java/com/example/demo/dashboard/dto/ResponseDetailDTO.java

package com.example.demo.dashboard.dto;

import java.util.List;

public class ResponseDetailDTO {
  private Long questionId;
  private String enonce;
  private String type;
  private String difficulte;
  private Long competenceId;
  private Object reponseUtilisateur;
  private List<String> reponseAttenue;  ← Pour QCM_MULTIPLE
  private String statutCorrection;      ← Pour REPONSE_LIBRE
  private boolean estCorrect;
  private int tempsReponse;
  private double pointsGagnes;

  // Getters et setters
  public Long getQuestionId() { return questionId; }
  public void setQuestionId(Long questionId) { this.questionId = questionId; }
  
  public String getEnonce() { return enonce; }
  public void setEnonce(String enonce) { this.enonce = enonce; }
  
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  
  public String getDifficulte() { return difficulte; }
  public void setDifficulte(String difficulte) { this.difficulte = difficulte; }
  
  public Long getCompetenceId() { return competenceId; }
  public void setCompetenceId(Long competenceId) { this.competenceId = competenceId; }
  
  public Object getReponseUtilisateur() { return reponseUtilisateur; }
  public void setReponseUtilisateur(Object reponseUtilisateur) { 
    this.reponseUtilisateur = reponseUtilisateur; 
  }
  
  public List<String> getReponseAttenue() { return reponseAttenue; }
  public void setReponseAttenue(List<String> reponseAttenue) { 
    this.reponseAttenue = reponseAttenue; 
  }
  
  public String getStatutCorrection() { return statutCorrection; }
  public void setStatutCorrection(String statutCorrection) { 
    this.statutCorrection = statutCorrection; 
  }
  
  public boolean isEstCorrect() { return estCorrect; }
  public void setEstCorrect(boolean estCorrect) { this.estCorrect = estCorrect; }
  
  public int getTempsReponse() { return tempsReponse; }
  public void setTempsReponse(int tempsReponse) { this.tempsReponse = tempsReponse; }
  
  public double getPointsGagnes() { return pointsGagnes; }
  public void setPointsGagnes(double pointsGagnes) { this.pointsGagnes = pointsGagnes; }
}
```

---

## 💻 Frontend - Appel API

```javascript
// src/utils/adminUserApi.js

class AdminUserService {
  constructor(token) {
    this.token = token;
    this.baseURL = 'http://localhost:8080/api/v1';
  }

  /**
   * Récupérer l'historique complet d'un utilisateur
   */
  async getUserHistory(userId) {
    try {
      const response = await fetch(
        `${this.baseURL}/admin/users/${userId}/historique`,
        {
          headers: { 'Authorization': `Bearer ${this.token}` }
        }
      );

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      const result = await response.json();
      return result.data;  // ← RetournUserHistoryDTO complète

    } catch (error) {
      console.error('Erreur chargement historique:', error);
      throw error;
    }
  }
}

export default AdminUserService;
```

---

## 🎨 Frontend - Vue Historique

```vue
<!-- src/components/admin/UserHistoryModal.vue -->

<template>
  <div class="user-history-modal">
    <!-- Header -->
    <div class="modal-header">
      <h2>Historique de {{ history.prenom }} {{ history.nom }}</h2>
      <button @click="closeModal" class="btn-close">✕</button>
    </div>

    <!-- Info utilisateur -->
    <div class="user-info">
      <div class="info-card">
        <span class="label">Email:</span>
        <span class="value">{{ history.email }}</span>
      </div>
      <div class="info-card">
        <span class="label">Rôle:</span>
        <span class="value">{{ history.role }}</span>
      </div>
    </div>

    <!-- Stats globales -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-value">{{ history.totalSessions }}</div>
        <div class="stat-label">Sessions totales</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ history.scoreMoyen.toFixed(2) }}/20</div>
        <div class="stat-label">Score moyen</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ history.tauxReussite.toFixed(1) }}%</div>
        <div class="stat-label">Taux réussite</div>
      </div>
    </div>

    <!-- Sessions -->
    <div class="sessions-section">
      <h3>📋 Sessions ({{ history.sessions.length }})</h3>
      
      <div 
        v-for="session in history.sessions" 
        :key="session.sessionId"
        class="session-card"
      >
        <!-- Résumé session -->
        <div class="session-header" @click="toggleSession(session.sessionId)">
          <div class="session-title">
            <span class="session-date">{{ formatDate(session.dateDebut) }}</span>
            <span class="session-statut" :class="'statut-' + session.statut.toLowerCase()">
              {{ session.statut }}
            </span>
          </div>
          <div class="session-stats">
            <span>{{ session.nombreQuestionsJustes }}/{{ session.nombreQuestionsRepondues }}</span>
            <span class="score">{{ session.scoreTotal.toFixed(2) }}/20</span>
            <span class="duration">{{ formatDuration(session.dureeTotalSecondes) }}</span>
          </div>
          <span class="toggle-icon">{{ expandedSessions.has(session.sessionId) ? '▼' : '▶' }}</span>
        </div>

        <!-- Détails session (collapsible) -->
        <div v-if="expandedSessions.has(session.sessionId)" class="session-details">
          <!-- Compétences -->
          <div class="competences-section">
            <h4>Compétences</h4>
            <div class="competence-list">
              <div 
                v-for="comp in session.competences" 
                :key="comp.competenceId"
                class="competence-item"
              >
                <div class="comp-name">{{ comp.nom }}</div>
                <div class="comp-stats">
                  <span class="score">{{ comp.scorePartiel.toFixed(2) }}</span>
                  <span class="ratio">{{ comp.nombreJustes }}/{{ comp.nombreQuestions }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Réponses détaillées -->
          <div class="responses-section">
            <h4>Réponses détaillées</h4>
            <div class="responses-list">
              <div 
                v-for="(resp, idx) in session.reponses" 
                :key="idx"
                class="response-item"
                :class="{ 'correct': resp.estCorrect, 'incorrect': !resp.estCorrect }"
              >
                <!-- Question -->
                <div class="response-question">
                  <span class="question-text">{{ resp.enonce }}</span>
                  <span class="question-meta">
                    <span class="type">{{ resp.type }}</span>
                    <span class="difficulty">{{ resp.difficulte }}</span>
                    <span class="points">{{ resp.pointsGagnes }} pts</span>
                  </span>
                </div>

                <!-- Réponse utilisateur -->
                <div class="response-answer">
                  <div class="answer-label">📝 Réponse utilisateur:</div>
                  <div class="answer-content">
                    <span v-if="Array.isArray(resp.reponseUtilisateur)">
                      {{ resp.reponseUtilisateur.join(", ") }}
                    </span>
                    <span v-else>{{ resp.reponseUtilisateur }}</span>
                  </div>
                </div>

                <!-- Attentes (si QCM_MULTIPLE) -->
                <div v-if="resp.reponseAttenue" class="response-expected">
                  <div class="expected-label">✓ Bonnes réponses attendues:</div>
                  <div class="expected-content">
                    {{ resp.reponseAttenue.join(", ") }}
                  </div>
                </div>

                <!-- Statut correction (si REPONSE_LIBRE) -->
                <div v-if="resp.statutCorrection" class="response-status">
                  <span class="status-badge" :class="'status-' + resp.statutCorrection.toLowerCase()">
                    {{ resp.statutCorrection }}
                  </span>
                </div>

                <!-- Temps -->
                <div class="response-time">
                  ⏱️ Temps: {{ resp.tempsReponse }}s
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <div class="modal-footer">
      <button @click="closeModal" class="btn-primary">Fermer</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import AdminUserService from '@/utils/adminUserApi';

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['close']);

const history = ref(null);
const loading = ref(false);
const error = ref(null);
const expandedSessions = ref(new Set());

watch(() => props.userId, () => {
  loadHistory();
});

async function loadHistory() {
  loading.value = true;
  error.value = null;
  
  try {
    const token = localStorage.getItem('adminToken');
    const service = new AdminUserService(token);
    history.value = await service.getUserHistory(props.userId);
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
}

function toggleSession(sessionId) {
  if (expandedSessions.value.has(sessionId)) {
    expandedSessions.value.delete(sessionId);
  } else {
    expandedSessions.value.add(sessionId);
  }
}

function formatDate(date) {
  return new Date(date).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

function formatDuration(seconds) {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins}m ${secs}s`;
}

function closeModal() {
  emit('close');
}

// Charger à la montée
watch(() => props.userId, loadHistory, { immediate: true });
</script>

<style scoped>
.user-history-modal {
  max-height: 90vh;
  overflow-y: auto;
  padding: 20px;
  border-radius: 8px;
  background-color: #fff;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 2px solid #eee;
  padding-bottom: 12px;
}

.user-info {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.info-card {
  display: flex;
  gap: 8px;
  font-size: 14px;
}

.info-card .label {
  font-weight: bold;
  color: #666;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.stat-card {
  padding: 16px;
  background-color: #f5f5f5;
  border-radius: 6px;
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #2196f3;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.session-card {
  border: 1px solid #ddd;
  border-radius: 6px;
  margin-bottom: 12px;
  overflow: hidden;
}

.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background-color: #f9f9f9;
  cursor: pointer;
  transition: background-color 0.3s;
}

.session-header:hover {
  background-color: #f0f0f0;
}

.session-title {
  display: flex;
  gap: 12px;
  align-items: center;
  font-weight: bold;
}

.session-statut {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  color: white;
}

.statut-terminee {
  background-color: #4caf50;
}

.statut-abandonnee {
  background-color: #f44336;
}

.session-stats {
  display: flex;
  gap: 16px;
  font-size: 14px;
}

.toggle-icon {
  cursor: pointer;
  transition: transform 0.3s;
}

.session-details {
  padding: 16px;
  background-color: #fafafa;
  border-top: 1px solid #eee;
}

.response-item {
  border-left: 4px solid #ddd;
  padding: 12px;
  margin-bottom: 8px;
  background-color: white;
  border-radius: 4px;
}

.response-item.correct {
  border-left-color: #4caf50;
  background-color: #f1f8f4;
}

.response-item.incorrect {
  border-left-color: #f44336;
  background-color: #fce4ec;
}

.response-question {
  font-weight: bold;
  margin-bottom: 8px;
}

.question-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.response-answer {
  margin: 8px 0;
  padding: 8px;
  background-color: white;
  border-radius: 4px;
}

.answer-label {
  font-weight: bold;
  font-size: 13px;
  color: #666;
}

.answer-content {
  margin-top: 4px;
  padding: 8px;
  background-color: #f0f0f0;
  border-radius: 3px;
  font-family: monospace;
}
</style>
```

---

## 🔄 Intégration - Tableau Utilisateurs

```vue
<!-- Dans AdminUsers.vue -->

<template>
  <div class="users-table">
    <table>
      <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.nom }} {{ user.prenom }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.role }}</td>
          <td>
            <!-- Bouton historique -->
            <button 
              @click="showHistory(user.id)"
              class="btn-view-history"
            >
              📊 Historique
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- Modal historique -->
  <UserHistoryModal 
    v-if="selectedUserId"
    :userId="selectedUserId"
    @close="selectedUserId = null"
  />
</template>

<script setup>
import { ref } from 'vue';
import UserHistoryModal from '@/components/admin/UserHistoryModal.vue';

const selectedUserId = ref(null);

function showHistory(userId) {
  selectedUserId.value = userId;
  // Le modal se chargera automatiquement via le watcher
}
</script>
```

---

## ✅ Validation/Sécurité

```java
// Dans AdminUserController.java

@GetMapping("/{userId}/historique")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ApiResponse<UserHistoryDTO>> getUserHistory(
    @PathVariable Long userId,
    @RequestHeader("Authorization") String authHeader) {
  
  // 1. Vérifier que l'admin ne demande pas TROP de données
  if (userId == null || userId <= 0) {
    return ResponseEntity.badRequest().body(new ApiResponse<>(
        false,
        400,
        "User ID invalid",
        null
    ));
  }

  // 2. Récupérer l'utilisateur
  try {
    UserHistoryDTO history = dashboardService.getUserHistory(userId);
    
    return ResponseEntity.ok(new ApiResponse<>(
        "User history retrieved successfully",
        history
    ));
  } catch (RuntimeException e) {
    return ResponseEntity.status(404).body(new ApiResponse<>(
        false,
        404,
        "User not found",
        null
    ));
  }
}
```

---

## 📊 Points clés

✅ **Données retournées:**
- Info utilisateur (nom, email, rôle)
- Statistiques globales (total sessions, score moyen, taux réussite)
- Pour chaque session:
  - Date, durée, statut, score
  - Scores par compétence
  - Détails CHAQUE réponse (question, réponse user, bonne réponse, points)
  - Pour REPONSE_LIBRE: statut correction (EN_ATTENTE, VALIDEE, etc.)

✅ **Frontend:**
- Modal avec historique collapsible
- Stats visuelles
- Détail complet de chaque session
- Voir réponses exactes de l'utilisateur

---

**C'est un endpoint puissant pour audit/suivi! 🚀**
