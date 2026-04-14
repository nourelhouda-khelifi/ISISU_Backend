# 📝 GUIDE - CRÉER CHAQUE TYPE DE QUESTION

> **Comment créer et personnaliser l'interface pour chaque type de question**
> Incluant: Structure de données, UI, Validation, Marquer réponses justes

---

## 🎯 Les 4 Types de Questions

```javascript
const TypesQuestion = {
  QCM_SIMPLE: 'QCM_SIMPLE',         // 1 seule bonne réponse
  QCM_MULTIPLE: 'QCM_MULTIPLE',     // Plusieurs bonnes réponses
  VRAI_FAUX: 'VRAI_FAUX',           // Vrai ou Faux (2 choix)
  REPONSE_LIBRE: 'REPONSE_LIBRE'    // Texte libre (réponse attendue)
};
```

---

## 1️⃣ QCM SIMPLE - Une seule bonne réponse

### 📋 Cas d'usage réel
**Question:** "Quel est le type de données pour un nombre entier en Java?"
```
☐ string
☑ int          ← Bonne réponse
☐ boolean
☐ double
```

### 📊 Structure JSON

```json
{
  "enonce": "Quel est le type de données pour un nombre entier en Java?",
  "type": "QCM_SIMPLE",
  "difficulte": "FACILE",
  "dureeSecondes": 30,
  "competenceIds": [1, 2],
  "choix": [
    {
      "contenu": "string",
      "estCorrect": false,    ← Fausse
      "ordre": 1
    },
    {
      "contenu": "int",
      "estCorrect": true,     ← Bonne réponse
      "ordre": 2
    },
    {
      "contenu": "boolean",
      "estCorrect": false,    ← Fausse
      "ordre": 3
    },
    {
      "contenu": "double",
      "estCorrect": false,    ← Fausse
      "ordre": 4
    }
  ]
}
```

### 🎨 UI Frontend (Création)

```vue
<template>
  <div v-if="formData.type === 'QCM_SIMPLE'" class="qcm-simple-form">
    <!-- Énoncé -->
    <div class="form-group">
      <label>Énoncé de la question *</label>
      <textarea 
        v-model="formData.enonce" 
        placeholder="Posez votre question..."
        rows="4"
      ></textarea>
    </div>

    <!-- Choix des réponses -->
    <div class="form-group">
      <label>Réponses possibles *</label>
      <div class="responses-list">
        <div 
          v-for="(answer, idx) in formData.choix" 
          :key="idx"
          class="response-item"
          :class="{ 'is-correct': answer.estCorrect }"
        >
          <!-- Champ texte -->
          <input 
            v-model="answer.contenu"
            type="text"
            placeholder="Entrez la réponse..."
            class="response-input"
          />

          <!-- Radio button - Bonne réponse (QCM_SIMPLE = 1 seule) -->
          <div class="radio-group">
            <input 
              type="radio"
              :value="idx"
              v-model="correctAnswerIndex"
              @change="markAsCorrect(idx)"
              class="radio-input"
            />
            <label class="radio-label">✓ Bonne réponse</label>
          </div>

          <!-- Bouton supprimer -->
          <button 
            @click="removeAnswer(idx)"
            class="btn-remove"
            v-if="formData.choix.length > 2"
          >
            🗑️ Supprimer
          </button>
        </div>
      </div>

      <!-- Ajouter réponse -->
      <button @click="addAnswer" class="btn-secondary">
        + Ajouter une réponse
      </button>

      <!-- Validation -->
      <p v-if="!hasCorrectAnswer" class="error-message">
        ❌ Vous devez marquer une réponse comme correcte!
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const formData = ref({
  type: 'QCM_SIMPLE',
  enonce: '',
  choix: [
    { contenu: '', estCorrect: true, ordre: 1 },  ← 1ère réponse (bonne par défaut)
    { contenu: '', estCorrect: false, ordre: 2 },
    { contenu: '', estCorrect: false, ordre: 3 },
    { contenu: '', estCorrect: false, ordre: 4 }
  ]
});

const correctAnswerIndex = ref(0);

// Fonction: Marquer comme correcte (QCM_SIMPLE = 1 seule)
function markAsCorrect(index) {
  formData.value.choix.forEach((choice, i) => {
    choice.estCorrect = (i === index);  ← Décocher les autres
  });
}

// Ajouter réponse
function addAnswer() {
  formData.value.choix.push({
    contenu: '',
    estCorrect: false,
    ordre: formData.value.choix.length + 1
  });
}

// Supprimer réponse
function removeAnswer(index) {
  formData.value.choix.splice(index, 1);
}

// Vérification: Au moins 1 bonne réponse
const hasCorrectAnswer = computed(() => {
  return formData.value.choix.some(c => c.estCorrect);
});
</script>

<style scoped>
.response-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  margin-bottom: 8px;
  transition: all 0.3s;
}

.response-item.is-correct {
  border-color: #4caf50;
  background-color: #f1f8f4;
}

.response-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
}

.radio-group {
  display: flex;
  align-items: center;
  gap: 6px;
}

.radio-label {
  cursor: pointer;
  color: #555;
  font-size: 13px;
}

.error-message {
  color: #f44336;
  font-size: 13px;
  margin-top: 8px;
}
</style>
```

### ✅ Validation QCM_SIMPLE
- ✅ Au moins 2 réponses
- ✅ Exactement 1 réponse correcte
- ✅ Au moins 1 réponse fausse
- ✅ Tous les énoncés remplis

---

## 2️⃣ QCM MULTIPLE - Plusieurs bonnes réponses

### 📋 Cas d'usage réel
**Question:** "Quels sont les avantages de Spring Boot?" (Pluriel = plusieurs réponses)
```
☑ Configuration automatique     ← Bonne
☐ Code très verbeux
☑ Embedded server              ← Bonne
☑ Maven dependency management   ← Bonne
☐ Lent au démarrage
```

### 📊 Structure JSON

```json
{
  "enonce": "Quels sont les avantages de Spring Boot?",
  "type": "QCM_MULTIPLE",
  "difficulte": "MOYEN",
  "dureeSecondes": 60,
  "competenceIds": [2, 5],
  "choix": [
    {
      "contenu": "Configuration automatique",
      "estCorrect": true,    ← Bonne
      "ordre": 1
    },
    {
      "contenu": "Code très verbeux",
      "estCorrect": false,   ← Fausse
      "ordre": 2
    },
    {
      "contenu": "Embedded server",
      "estCorrect": true,    ← Bonne
      "ordre": 3
    },
    {
      "contenu": "Maven dependency management",
      "estCorrect": true,    ← Bonne
      "ordre": 4
    },
    {
      "contenu": "Lent au démarrage",
      "estCorrect": false,   ← Fausse
      "ordre": 5
    }
  ]
}
```

### 🎨 UI Frontend (Création)

```vue
<template>
  <div v-if="formData.type === 'QCM_MULTIPLE'" class="qcm-multiple-form">
    <!-- Énoncé -->
    <div class="form-group">
      <label>Énoncé de la question *</label>
      <textarea 
        v-model="formData.enonce" 
        placeholder="Posez votre question (au pluriel)..."
        rows="4"
      ></textarea>
      <small>💡 Utilisez le pluriel: "Quels sont...", "Sélectionnez..."</small>
    </div>

    <!-- Choix des réponses -->
    <div class="form-group">
      <label>Réponses possibles * (Sélectionnez ≥2 correctes)</label>
      <div class="responses-list">
        <div 
          v-for="(answer, idx) in formData.choix" 
          :key="idx"
          class="response-item"
          :class="{ 'is-correct': answer.estCorrect }"
        >
          <!-- Champ texte -->
          <input 
            v-model="answer.contenu"
            type="text"
            placeholder="Entrez la réponse..."
            class="response-input"
          />

          <!-- Checkbox - Bonnes réponses (QCM_MULTIPLE = ≥2) -->
          <div class="checkbox-group">
            <input 
              type="checkbox"
              :checked="answer.estCorrect"
              @change="(e) => answer.estCorrect = e.target.checked"
              class="checkbox-input"
            />
            <label class="checkbox-label">✓ Correcte</label>
          </div>

          <!-- Bouton supprimer -->
          <button 
            @click="removeAnswer(idx)"
            class="btn-remove"
            v-if="formData.choix.length > 2"
          >
            🗑️
          </button>
        </div>
      </div>

      <!-- Ajouter réponse -->
      <button @click="addAnswer" class="btn-secondary">
        + Ajouter une réponse
      </button>

      <!-- Validation -->
      <div class="validation-messages">
        <p v-if="correctAnswersCount < 2" class="error-message">
          ❌ Au minimum 2 réponses correctes! (actuellement: {{correctAnswersCount}})
        </p>
        <p v-if="falseAnswersCount === 0" class="error-message">
          ❌ Au minimum 1 réponse fausse!
        </p>
        <p v-if="correctAnswersCount >= 2 && falseAnswersCount > 0" class="success-message">
          ✅ Configuration valide
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const formData = ref({
  type: 'QCM_MULTIPLE',
  enonce: '',
  choix: [
    { contenu: '', estCorrect: true, ordre: 1 },
    { contenu: '', estCorrect: true, ordre: 2 },
    { contenu: '', estCorrect: false, ordre: 3 },
    { contenu: '', estCorrect: false, ordre: 4 }
  ]
});

// Compter les correctes/fausses
const correctAnswersCount = computed(() => 
  formData.value.choix.filter(c => c.estCorrect).length
);

const falseAnswersCount = computed(() => 
  formData.value.choix.filter(c => !c.estCorrect).length
);

// Ajouter réponse
function addAnswer() {
  formData.value.choix.push({
    contenu: '',
    estCorrect: false,
    ordre: formData.value.choix.length + 1
  });
}

// Supprimer réponse
function removeAnswer(index) {
  formData.value.choix.splice(index, 1);
}
</script>

<style scoped>
.checkbox-group {
  display: flex;
  align-items: center;
  gap: 6px;
}

.checkbox-input {
  cursor: pointer;
  width: 18px;
  height: 18px;
}

.success-message {
  color: #4caf50;
  font-size: 13px;
}
</style>
```

### ✅ Validation QCM_MULTIPLE
- ✅ Au moins 4 réponses
- ✅ **Au minimum 2 réponses correctes** ← Différence clé!
- ✅ Au minimum 1 réponse fausse
- ✅ Tous les énoncés remplis

---

## 3️⃣ VRAI/FAUX - 2 choix seulement

### 📋 Cas d'usage réel
**Question:** "Java est un langage compilé"
```
☑ VRAI       ← Bonne réponse
☐ FAUX
```

### 📊 Structure JSON

```json
{
  "enonce": "Java est un langage compilé",
  "type": "VRAI_FAUX",
  "difficulte": "FACILE",
  "dureeSecondes": 15,
  "competenceIds": [1],
  "choix": [
    {
      "contenu": "VRAI",
      "estCorrect": true,    ← La bonne réponse
      "ordre": 1
    },
    {
      "contenu": "FAUX",
      "estCorrect": false,
      "ordre": 2
    }
  ]
}
```

### 🎨 UI Frontend (Création)

```vue
<template>
  <div v-if="formData.type === 'VRAI_FAUX'" class="vrai-faux-form">
    <!-- Énoncé -->
    <div class="form-group">
      <label>Énoncé (affirmation) *</label>
      <textarea 
        v-model="formData.enonce" 
        placeholder="Écrivez une affirmation à évaluer..."
        rows="3"
      ></textarea>
      <small>💡 Exemple: 'Spring Boot utilise des serveurs embarqués'</small>
    </div>

    <!-- Choix: VRAI ou FAUX -->
    <div class="form-group">
      <label>Réponse correcte *</label>
      <div class="vrai-faux-options">
        <!-- VRAI -->
        <div class="option">
          <input 
            type="radio"
            id="opt-vrai"
            value="VRAI"
            v-model="correctAnswer"
            @change="setCorrectAnswer('VRAI')"
          />
          <label for="opt-vrai" class="option-label">
            <span class="option-text">VRAI</span>
            <span class="option-icon">✓</span>
          </label>
        </div>

        <!-- FAUX -->
        <div class="option">
          <input 
            type="radio"
            id="opt-faux"
            value="FAUX"
            v-model="correctAnswer"
            @change="setCorrectAnswer('FAUX')"
          />
          <label for="opt-faux" class="option-label">
            <span class="option-text">FAUX</span>
            <span class="option-icon">✗</span>
          </label>
        </div>
      </div>
    </div>

    <!-- Visualisation -->
    <div class="preview">
      <p><strong>Affirmation:</strong> {{ formData.enonce }}</p>
      <p><strong>Réponse correcte:</strong> 
        <span v-if="correctAnswer === 'VRAI'" class="badge-vrai">VRAI ✓</span>
        <span v-else class="badge-faux">FAUX ✗</span>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const formData = ref({
  type: 'VRAI_FAUX',
  enonce: '',
  choix: [
    { contenu: 'VRAI', estCorrect: true, ordre: 1 },
    { contenu: 'FAUX', estCorrect: false, ordre: 2 }
  ]
});

const correctAnswer = ref('VRAI');

// Mettre à jour quelle réponse est correcte
function setCorrectAnswer(value) {
  formData.value.choix.forEach(choice => {
    choice.estCorrect = (choice.contenu === value);
  });
}
</script>

<style scoped>
.vrai-faux-options {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}

.option {
  position: relative;
  flex: 1;
}

.option input[type="radio"] {
  display: none;
}

.option-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  border: 2px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: bold;
  font-size: 16px;
}

.option input[type="radio"]:checked + .option-label {
  border-color: #2196f3;
  background-color: #e3f2fd;
}

.option-icon {
  font-size: 24px;
  margin-top: 8px;
}

.badge-vrai {
  background-color: #4caf50;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: bold;
}

.badge-faux {
  background-color: #f44336;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: bold;
}

.preview {
  background-color: #f5f5f5;
  padding: 12px;
  border-radius: 6px;
  margin-top: 12px;
  font-size: 14px;
}
</style>
```

### ✅ Validation VRAI/FAUX
- ✅ Énoncé fourni
- ✅ Une réponse marquée comme correcte (VRAI ou FAUX)
- ✅ Les 2 choix sont toujours "VRAI" et "FAUX"

---

## 4️⃣ RÉPONSE LIBRE - Texte libre

### 📋 Cas d'usage réel
**Question:** "Expliquez le concept de polymorphisme en Java"
```
Réponse attendue: "Le polymorphisme est..."
Réponse utilisateur: [champ texte libre]
```

### 📊 Structure JSON

```json
{
  "enonce": "Expliquez le concept de polymorphisme en Java en 3-4 lignes",
  "type": "REPONSE_LIBRE",
  "difficulte": "DIFFICILE",
  "dureeSecondes": 180,
  "competenceIds": [1, 3],
  "choix": [
    {
      "contenu": "Le polymorphisme est la capacité d'une méthode à avoir plusieurs formes. On distingue le polymorphisme de compilation (surcharge) et du polymorphisme d'exécution (héritage). Elle permet de traiter les objets de manière uniforme.",
      "estCorrect": true,    ← C'est LA réponse attendue
      "ordre": 1
    },
    {
      "contenu": "Il peut aussi être défini comme...",  ← Accepter variantes
      "estCorrect": true,
      "ordre": 2
    },
    {
      "contenu": "Ou encore...",                         ← Autre formulation
      "estCorrect": true,
      "ordre": 3
    }
  ]
}
```

### 🎨 UI Frontend (Création)

```vue
<template>
  <div v-if="formData.type === 'REPONSE_LIBRE'" class="reponse-libre-form">
    <!-- Énoncé -->
    <div class="form-group">
      <label>Question *</label>
      <textarea 
        v-model="formData.enonce" 
        placeholder="Posez une question ouverte..."
        rows="4"
      ></textarea>
      <small>💡 Exemple: 'Expliquez...', 'Décrivez...', 'Commentez...'</small>
    </div>

    <!-- Réponses acceptées -->
    <div class="form-group">
      <label>Réponses acceptées * (Variantes possibles)</label>
      <small>Ajoutez toutes les formulations acceptables</small>

      <div class="answers-list">
        <div 
          v-for="(answer, idx) in formData.choix"
          :key="idx"
          class="answer-item"
        >
          <textarea 
            v-model="answer.contenu"
            placeholder="Une réponse acceptable..."
            rows="4"
            class="answer-textarea"
          ></textarea>

          <!-- Indication: c'est une bonne réponse -->
          <div class="answer-indicator">
            <span class="badge-accepted">✓ Acceptée</span>
            <span class="order-indicator">#{{ idx + 1 }}</span>
          </div>

          <!-- Supprimer -->
          <button 
            @click="removeAnswer(idx)"
            class="btn-remove"
            v-if="formData.choix.length > 1"
          >
            🗑️ Supprimer
          </button>
        </div>
      </div>

      <!-- Ajouter une variante -->
      <button @click="addAnswer" class="btn-secondary">
        + Ajouter une autre formulation
      </button>

      <!-- Validation -->
      <p v-if="formData.choix.length === 0" class="error-message">
        ❌ Ajoutez au moins 1 réponse acceptable!
      </p>
      <p v-if="formData.choix.some(c => !c.contenu)" class="error-message">
        ❌ Complétez toutes les réponses!
      </p>
    </div>

    <!-- Note -->
    <div class="note-box">
      <strong>📌 Note importante:</strong>
      <ul>
        <li>👨‍🏫 Admin devra vérifier/corriger chaque réponse</li>
        <li>🤖 Pas de vérification automatique</li>
        <li>⭐ Correction manuelle requise</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';

const formData = ref({
  type: 'REPONSE_LIBRE',
  enonce: '',
  choix: [
    { contenu: '', estCorrect: true, ordre: 1 }
  ]
});

// Ajouter une variante de réponse
function addAnswer() {
  formData.value.choix.push({
    contenu: '',
    estCorrect: true,  ← Toutes les réponses LIBRE sont "correctes"
    ordre: formData.value.choix.length + 1
  });
}

// Supprimer une réponse
function removeAnswer(index) {
  formData.value.choix.splice(index, 1);
}

// Validation
const isValid = computed(() => {
  return formData.value.choix.length > 0 &&
         formData.value.choix.every(c => c.contenu.trim());
});
</script>

<style scoped>
.answer-textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-family: 'Segoe UI', sans-serif;
  resize: vertical;
}

.answer-indicator {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
}

.badge-accepted {
  background-color: #4caf50;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
}

.order-indicator {
  color: #999;
  font-style: italic;
}

.note-box {
  background-color: #fff3cd;
  padding: 12px;
  border-left: 4px solid #ffc107;
  border-radius: 4px;
  margin-top: 16px;
  font-size: 13px;
}

.note-box ul {
  margin: 8px 0 0 20px;
  padding: 0;
}

.note-box li {
  margin: 4px 0;
}
</style>
```

### ✅ Validation RÉPONSE_LIBRE
- ✅ Question claire et ouverte
- ✅ Au minimum 1 réponse attendue
- ✅ Peut avoir plusieurs variantes acceptables
- ✅ **Correction manuelle nécessaire** (pas d'auto-vérif)

---

## 🔄 SÉLECTIONNER LE TYPE - Interface principale

```vue
<template>
  <div class="type-selector">
    <label>Type de question *</label>
    
    <div class="type-options">
      <!-- QCM_SIMPLE -->
      <div 
        class="type-card"
        :class="{ active: formData.type === 'QCM_SIMPLE' }"
        @click="selectType('QCM_SIMPLE')"
      >
        <div class="type-icon">◯</div>
        <div class="type-name">QCM Simple</div>
        <div class="type-description">1 seule bonne réponse</div>
        <div class="type-example">Quelle est la capitale?</div>
      </div>

      <!-- QCM_MULTIPLE -->
      <div 
        class="type-card"
        :class="{ active: formData.type === 'QCM_MULTIPLE' }"
        @click="selectType('QCM_MULTIPLE')"
      >
        <div class="type-icon">☑</div>
        <div class="type-name">QCM Multiple</div>
        <div class="type-description">Plusieurs bonnes réponses</div>
        <div class="type-example">Quels sont les avantages?</div>
      </div>

      <!-- VRAI_FAUX -->
      <div 
        class="type-card"
        :class="{ active: formData.type === 'VRAI_FAUX' }"
        @click="selectType('VRAI_FAUX')"
      >
        <div class="type-icon">✓✗</div>
        <div class="type-name">Vrai/Faux</div>
        <div class="type-description">2 choix seulement</div>
        <div class="type-example">Java est compilé</div>
      </div>

      <!-- REPONSE_LIBRE -->
      <div 
        class="type-card"
        :class="{ active: formData.type === 'REPONSE_LIBRE' }"
        @click="selectType('REPONSE_LIBRE')"
      >
        <div class="type-icon">✎</div>
        <div class="type-name">Réponse libre</div>
        <div class="type-description">Texte libre</div>
        <div class="type-example">Expliquez le polymorphisme</div>
      </div>
    </div>
  </div>

  <!-- Afficher le formulaire approprié -->
  <QCMSimpleForm v-if="formData.type === 'QCM_SIMPLE'" />
  <QCMMultipleForm v-if="formData.type === 'QCM_MULTIPLE'" />
  <VraiFauxForm v-if="formData.type === 'VRAI_FAUX'" />
  <ReponseLiureForm v-if="formData.type === 'REPONSE_LIBRE'" />
</template>

<script setup>
import { ref } from 'vue';

const formData = ref({
  type: 'QCM_SIMPLE'  ← Par défaut
});

function selectType(type) {
  formData.value.type = type;
  // Réinitialiser les choix selon le type
}
</script>

<style scoped>
.type-options {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.type-card {
  padding: 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
}

.type-card:hover {
  border-color: #2196f3;
  background-color: #f0f7ff;
}

.type-card.active {
  border-color: #2196f3;
  background-color: #e3f2fd;
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.3);
}

.type-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.type-name {
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 4px;
}

.type-description {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.type-example {
  font-size: 11px;
  color: #999;
  font-style: italic;
}
</style>
```

---

## 📝 RÉSUMÉ - Tableau comparatif

| Aspect | QCM Simple | QCM Multiple | Vrai/Faux | Réponse Libre |
|--------|-----------|--------------|-----------|---------------|
| **Bonnes réponses** | ✅ Exactement 1 | ✅ ≥ 2 | ✅ 1 (VRAI ou FAUX) | ✅ 1+ variantes |
| **Mauvaises réponses** | ≥ 1 | ≥ 1 | 1 (l'opposé) | 0 (libre) |
| **Total choix** | 4-5 | 4-6 | 2 (fixe) | 1+ variantes |
| **Interface** | Radio buttons | Checkboxes | Radio buttons | Textareas |
| **Difficultés** | 🟢 🟡 🔴 | 🟢 🟡 🔴 | 🟢 🟡 🔴 | 🟢 🟡 🔴 |
| **Temps (secondes)** | 15-60 | 30-120 | 10-30 | 60-300 |
| **Correction** | Automatique | Automatique | Automatique | ⚠️ Manuelle |
| **Cas d'usage** | Factuel | Conceptuel | Affirmation simple | Explication |

---

## 🎯 WORKFLOW COMPLET - Créer une question

### Étape 1: Choisir le type
```javascript
// Utilisateur clique "Nouvelle question"
// Afficher "Type Selector"
// Utilisateur sélectionne QCM_SIMPLE
```

### Étape 2: Remplir le formulaire type-spécifique
```javascript
// Afficher QCMSimpleForm
// - Énoncé
// - 4 réponses avec radio buttons
// - Marquer 1 comme bonne (radio)
```

### Étape 3: Ajouter métadonnées
```javascript
// - Sélectionner difficulté (FACILE/MOYEN/DIFFICILE)
// - Sélectionner durée (15-300 secondes)
// - Sélectionner compétences (multiselect)
```

### Étape 4: Valider
```javascript
// Vérifier:
// - Énoncé ≥ 10 caractères
// - ≥ 2 réponses
// - ≥ 1 bonne réponse (QCM_SIMPLE = 1 exactement)
// - ≥ 1 mauvaise réponse (sauf VRAI_FAUX)
```

### Étape 5: Envoyer au serveur
```javascript
async function handleCreateQuestion(formData) {
  const payload = {
    enonce: formData.enonce,
    type: formData.type,         // QCM_SIMPLE, etc.
    difficulte: formData.difficulte,
    dureeSecondes: formData.dureeSecondes,
    competenceIds: formData.competenceIds,
    choix: formData.choix         // Array avec contenu + estCorrect
  };

  const newQuestion = await createQuestion(token, payload);
  console.log('✅ Question créée:', newQuestion.id);
}
```

---

## 🚨 VALIDATIONS ESSENTIELLES

```javascript
function validateQuestion(formData) {
  const errors = [];

  // Énoncé
  if (!formData.enonce || formData.enonce.trim().length < 10) {
    errors.push('❌ Énoncé trop court (min 10 caractères)');
  }

  // Type
  if (!['QCM_SIMPLE', 'QCM_MULTIPLE', 'VRAI_FAUX', 'REPONSE_LIBRE'].includes(formData.type)) {
    errors.push('❌ Type invalide');
  }

  // Choix
  if (formData.choix.length === 0) {
    errors.push('❌ Aucune réponse');
  }

  if (formData.choix.some(c => !c.contenu || c.contenu.trim() === '')) {
    errors.push('❌ Réponses vides');
  }

  // Bonnes réponses
  const correctCount = formData.choix.filter(c => c.estCorrect).length;
  
  if (formData.type === 'QCM_SIMPLE' && correctCount !== 1) {
    errors.push('❌ QCM_SIMPLE: Exactement 1 bonne réponse');
  }

  if (formData.type === 'QCM_MULTIPLE' && correctCount < 2) {
    errors.push('❌ QCM_MULTIPLE: Au minimum 2 bonnes réponses');
  }

  if (formData.type === 'VRAI_FAUX' && correctCount !== 1) {
    errors.push('❌ VRAI_FAUX: Exactement 1 vrai et 1 faux');
  }

  // Mauvaises réponses (pas pour RÉPONSE_LIBRE)
  if (formData.type !== 'REPONSE_LIBRE') {
    const wrongCount = formData.choix.filter(c => !c.estCorrect).length;
    if (wrongCount === 0) {
      errors.push('❌ Aucune mauvaise réponse');
    }
  }

  // Compétences
  if (!formData.competenceIds || formData.competenceIds.length === 0) {
    errors.push('❌ Sélectionnez au moins 1 compétence');
  }

  // Difficulté
  if (!['FACILE', 'MOYEN', 'DIFFICILE'].includes(formData.difficulte)) {
    errors.push('❌ Difficulté invalide');
  }

  // Durée
  if (formData.dureeSecondes < 5 || formData.dureeSecondes > 600) {
    errors.push('❌ Durée doit être entre 5 et 600 secondes');
  }

  return errors;
}

// Utilisation
const errors = validateQuestion(formData.value);
if (errors.length > 0) {
  console.error(errors);
  showErrors(errors);
  return;
}

// Sinon, créer la question
createQuestion();
```

---

## 💡 BONNES PRATIQUES

✅ **À faire:**
- 📝 Énoncés clairs et précis
- 🎯 Réponses plausibles (même les fausses)
- ⏱️ Durée réaliste (ex: QCM_SIMPLE = 30-60s)
- 🏷️ Lier à ≥1 compétence
- 🔤 Variantes de réponses pour RÉPONSE_LIBRE

❌ **À NE PAS faire:**
- ❌ Énoncés ambigus
- ❌ Réponses "trop évidentes"
- ❌ Bonnes/mauvaises mal marquées
- ❌ Pas de limite de temps
- ❌ Sans compétence associée

---

**Vous êtes prêts à créer tous les types de questions! 🚀**
