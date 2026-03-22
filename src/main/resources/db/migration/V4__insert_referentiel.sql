-- V4__insert_referentiel.sql
-- Données initiales du Référentiel FIE3

-- ============================================================================
-- ÉTAPE 1 : Insertion des Unités d'Enseignement (9 lignes)
-- ============================================================================

-- Semestre 5
INSERT INTO unites_enseignement (code, libelle, ects, semestre) VALUES 
    ('E3-1-IN', 'Ingénierie numérique', 8, 'S5'),
    ('E3-1-ID', 'Ingénierie de données', 8, 'S5'),
    ('E3-1-IS', 'Ingénierie et Santé', 6, 'S5'),
    ('E3-1-DI', 'Devenir Ingénieur', 8, 'S5');

-- Semestre 6
INSERT INTO unites_enseignement (code, libelle, ects, semestre) VALUES 
    ('E3-2-IN', 'Ingénierie numérique', 7, 'S6'),
    ('E3-2-IS', 'Ingénierie et Santé', 6, 'S6'),
    ('E3-2-DI', 'Devenir Ingénieur', 7, 'S6'),
    ('E3-2-PROJET', 'Projet tuteuré', 4, 'S6'),
    ('E3-2-STAGE', 'Stage', 6, 'S6');

-- ============================================================================
-- ÉTAPE 2 : Insertion des Modules FIE (11 évaluables)
-- ============================================================================

-- Semestre 5
INSERT INTO modules_fie (code, nom, semestre, ue_id, heures_cm, heures_td, heures_tp, heures_projet, heures_total, evaluable, prerequis_texte) VALUES 
    ('E3-1-IN-1', 'Programmation orientée objet', 'S5', 1, 10, 10, 20, 0, 40, true, 'Bases algorithmique et programmation'),
    ('E3-1-IN-2', 'Génie Logiciel', 'S5', 1, 10, 10, 20, 0, 40, true, 'Programmation orientée objet'),
    ('E3-1-IN-3', 'Gestion de projet', 'S5', 1, 10, 10, 0, 0, 20, true, 'Aucun'),
    ('E3-1-ID-1', 'Systèmes d''information', 'S5', 2, 10, 10, 6, 0, 26, true, 'Aucun'),
    ('E3-1-ID-2', 'Base de données', 'S5', 2, 10, 16, 20, 0, 46, true, 'Données structurées, modèle relationnel'),
    ('E3-1-ID-3', 'Épidémiologie & santé des données', 'S5', 2, 14, 14, 0, 0, 28, true, 'Aucun'),
    ('E3-1-IS-2', 'Fondamentaux IA', 'S5', 3, 12, 8, 0, 0, 20, true, 'Mathématiques, algorithmique'),
    
-- Semestre 6
    ('E3-2-IN-1', 'Technologies Web', 'S6', 5, 16, 16, 18, 0, 50, true, 'Programmation orientée objet, Base de données'),
    ('E3-2-IN-2', 'DevOps et réseaux', 'S6', 5, 14, 16, 16, 0, 46, true, 'Génie Logiciel, Programmation'),
    ('E3-2-IN-3', 'Conception centrée utilisateur', 'S6', 5, 8, 8, 4, 0, 20, true, 'Gestion de projet'),
    ('E3-2-IS-4', 'Imagerie médicale', 'S6', 6, 12, 8, 0, 0, 20, true, 'Fondamentaux IA, connaissance du domaine médical');

-- ============================================================================
-- ÉTAPE 3 : Insertion des Compétences (42 total)
-- ============================================================================

-- Module E3-1-IN-1 : POO Java (Compétences 1-6)
INSERT INTO competences (intitule, description, numero_ordre, niveau_attendu, poids, module_id) VALUES 
    ('Maîtriser les types de données et structures de contrôle', 'int, double, boolean, String, conditions if/else, boucles for/while', 1, 1, 0.15, 1),
    ('Utiliser les collections (List, Set, Map)', 'ArrayList, HashSet, HashMap en Java Collections Framework', 2, 1, 0.20, 1),
    ('Appliquer les concepts Constructeurs, Accesseurs, Encapsulation', 'Getters/Setters, modificateurs private/public, this', 3, 1, 0.15, 1),
    ('Maîtriser l''Héritage, Composition, Interfaces', 'extends, implements, super(), polymorphisme', 4, 2, 0.25, 1),
    ('Appliquer les principes SOLID', 'Single Responsibility, Open/Closed, Liskov, Interface Segregation, Dependency Inversion', 5, 2, 0.15, 1),
    ('Gérer les exceptions', 'try/catch/finally, throw, exception handling, custom exceptions', 6, 1, 0.10, 1);

-- Module E3-1-IN-2 : Génie Logiciel (Compétences 7-10)
INSERT INTO competences (intitule, description, numero_ordre, niveau_attendu, poids, module_id) VALUES 
    ('Concevoir un modèle de classes UML (MVC)', 'Pattern Model-View-Control, diagrammes de classes', 7, 2, 0.30, 2),
    ('Implémenter une conception OO en Java', 'Design patterns (Factory, Observer, Strategy)', 8, 2, 0.25, 2),
    ('Produire des tests unitaires', 'JUnit, assertions, mocks, coverage', 9, 2, 0.25, 2),
    ('Utiliser les outils collaboratifs (Git, IDE, bug tracker)', 'Git flow, commit messages, branches, GitHub, code review', 10, 1, 0.20, 2);

-- Module E3-1-IN-3 : Gestion de projet (Compétences 11-14)
INSERT INTO competences (intitule, description, numero_ordre, niveau_attendu, poids, module_id) VALUES 
    ('Identifier les parties prenantes et leurs rôles', 'Identification des stakeholders, responsabilités, zones d''influence', 11, 1, 0.15, 3),
    ('Planifier un projet (WBS, Gantt, chemin critique)', 'Work Breakdown Structure, diagrammes de Gantt, CPM', 12, 2, 0.35, 3),
    ('Appliquer les principes Agile/Scrum', 'Sprint, backlog, daily standup, retrospectives', 13, 2, 0.25, 3),
    ('Rédiger un cahier des charges', 'Spécifications fonctionnelles, non-fonctionnelles, critères acceptation', 14, 1, 0.25, 3);

-- Module E3-1-ID-1 : Systèmes d''information (Compétences 15-17)
INSERT INTO competences (intitule, description, numero_ordre, niveau_attendu, poids, module_id) VALUES 
    ('Comprendre l''architecture d''un SI (4 niveaux)', 'Niveau métier, données, applicatif, technique', 15, 1, 0.25, 4),
    ('Modéliser avec SADT et BPMN', 'Diagrammes d''activité, processus métier, notation', 16, 2, 0.40, 4),
    ('Analyser un dossier patient informatisé (DPI, DMP)', 'Structures de données médicales, interopérabilité HL7', 17, 2, 0.35, 4);

-- Module E3-1-ID-2 : Base de données (Compétences 18-21)
INSERT INTO competences (intitule, description, numero_ordre, niveau_attendu, poids, module_id) VALUES 
    ('Concevoir un modèle entité-association', 'E-A diagram, normalisation, relations 1-1 / 1-N / N-N', 18, 2, 0.25, 5),
    ('Normaliser un schéma relationnel', '1NF, 2NF, 3NF, BCNF, dépendances fonctionnelles', 19, 2, 0.20, 5),
    ('Écrire des requêtes SQL complexes', 'SELECT avec JOIN, GROUP BY, HAVING, sous-requêtes, window functions', 20, 2, 0.35, 5),
    ('Créer des procédures stockées et triggers PL/SQL', 'Fonctions, triggers, curseurs, transactions', 21, 3, 0.20, 5);

-- Module E3-1-ID-3 : Épidémiologie & santé données (Compétences 22-25)
INSERT INTO competences (intitule, description, numero_ordre, niveau_attendu, poids, module_id) VALUES 
    ('Comprendre les indicateurs épidémiologiques', 'Prévalence, incidence, RR, OR, sensibilité, spécificité', 22, 1, 0.20, 6),
    ('Analyser les biais dans les enquêtes', 'Biais de sélection, information, confusion, échantillonnage', 23, 2, 0.25, 6),
    ('Nettoyer et préparer un jeu de données (data cleaning)', 'Détection de valeurs manquantes, aberrantes, déduplication', 24, 2, 0.30, 6),
    ('Appliquer l''encodage et la normalisation des variables', 'One-hot encoding, standardisation, normalisation min-max', 25, 2, 0.25, 6);

-- Module E3-1-IS-2 : Fondamentaux IA (Compétences 26-29)
INSERT INTO competences (intitule, description, numero_ordre, niveau_attendu, poids, module_id) VALUES 
    ('Comprendre le neurone formel et le perceptron', 'Fonction d''activation, backpropagation, limite du perceptron', 26, 1, 0.15, 7),
    ('Maîtriser l''apprentissage supervisé (descente du gradient)', 'Descente de gradient, loss functions, regularisation', 27, 2, 0.30, 7),
    ('Implémenter un modèle de classification simple', 'Regression logistique, SVM, Decision Trees en Python/Sklearn', 28, 2, 0.35, 7),
    ('Comprendre les réseaux convolutifs (CNN)', 'Convolutions, pooling, architecture (AlexNet, ResNet)', 29, 2, 0.20, 7);

-- Module E3-2-IN-1 : Technologies Web (Compétences 30-33)
INSERT INTO competences (intitule, description, numero_ordre, niveau_attendu, poids, module_id) VALUES 
    ('Comprendre l''architecture client-serveur', 'Requêtes HTTP, sessions, cookies, WebSockets', 30, 1, 0.15, 8),
    ('Développer un backend Spring Boot', 'Controllers, Services, Repos, ORM, RESTful APIs', 31, 2, 0.40, 8),
    ('Développer un frontend Vue.js', 'Templates, directives, composants, state management (Vuex)', 32, 2, 0.30, 8),
    ('Consommer une API REST avec AJAX', 'Fetch API, axios, promises, async/await', 33, 1, 0.15, 8);

-- Module E3-2-IN-2 : DevOps & réseaux (Compétences 34-37)
INSERT INTO competences (intitule, description, numero_ordre, niveau_attendu, poids, module_id) VALUES 
    ('Utiliser Git, Docker, Kubernetes', 'Containerisation, orchestration, versioning', 34, 2, 0.30, 9),
    ('Mettre en place un pipeline CI/CD', 'GitHub Actions, Jenkins, automatisation tests/déploiement', 35, 2, 0.30, 9),
    ('Comprendre le modèle OSI et les protocoles réseau', 'TCP/IP, DNS, DHCP, subnetting, routage', 36, 2, 0.25, 9),
    ('Configurer un réseau et déployer en HTTPS', 'Certificats SSL, firewall, load balancing, VPN', 37, 2, 0.15, 9);

-- Module E3-2-IN-3 : Conception centrée utilisateur (Compétences 38-39)
INSERT INTO competences (intitule, description, numero_ordre, niveau_attendu, poids, module_id) VALUES 
    ('Appliquer les étapes du processus CCU (ISO 9241-210)', 'User research, personas, wireframes, prototyping, testing', 38, 2, 0.50, 10),
    ('Utiliser les méthodes de design UX', 'Figma, user interviews, A/B testing, design thinking', 39, 2, 0.50, 10);

-- Module E3-2-IS-4 : Imagerie médicale (Compétences 40-42)
INSERT INTO competences (intitule, description, numero_ordre, niveau_attendu, poids, module_id) VALUES 
    ('Identifier les modalités d''imagerie (IRM, Scanner)', 'Principes physiques IRM/CT/PET, résolutions, artefacts', 40, 1, 0.20, 11),
    ('Utiliser les formats DICOM et NIfTI', 'Structure DICOM, tags, méta-données, NIfTI pour imagerie cérébrale', 41, 2, 0.30, 11),
    ('Appliquer les traitements d''image (filtrage, segmentation)', 'Convolution, edge detection, clustering, masking', 42, 2, 0.50, 11);

-- ============================================================================
-- ÉTAPE 4 : Insertion des prérequis entre modules
-- ============================================================================

-- POO Java → prérequis de Génie Logiciel et Technologies Web
INSERT INTO modules_prerequis (module_id, prerequis_id) VALUES 
    (2, 1),      -- Génie Logiciel prérequis POO Java
    (8, 1);      -- Technologies Web prérequis POO Java

-- Génie Logiciel → prérequis de Technologies Web
INSERT INTO modules_prerequis (module_id, prerequis_id) VALUES 
    (8, 2);      -- Technologies Web prérequis Génie Logiciel

-- Base de données → prérequis Technologies Web
INSERT INTO modules_prerequis (module_id, prerequis_id) VALUES 
    (8, 5);      -- Technologies Web prérequis Base de données

-- Technologies Web → prérequis DevOps
INSERT INTO modules_prerequis (module_id, prerequis_id) VALUES 
    (9, 8);      -- DevOps prérequis Technologies Web

-- Gestion de projet → prérequis CCU
INSERT INTO modules_prerequis (module_id, prerequis_id) VALUES 
    (10, 3);     -- CCU prérequis Gestion de projet

-- Fondamentaux IA → prérequis Imagerie médicale
INSERT INTO modules_prerequis (module_id, prerequis_id) VALUES 
    (11, 7);     -- Imagerie médicale prérequis Fondamentaux IA

-- Fondamentaux IA → prérequis Épidémiologie & data
INSERT INTO modules_prerequis (module_id, prerequis_id) VALUES 
    (6, 7);      -- Épidémiologie prérequis Fondamentaux IA
