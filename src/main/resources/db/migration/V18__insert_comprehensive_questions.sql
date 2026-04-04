-- ══════════════════════════════════════════════════════════════════════════════════
-- V18__insert_comprehensive_questions.sql
-- Banque complète de questions pour tous les modules et compétences
-- ══════════════════════════════════════════════════════════════════════════════════

-- Q1: POO Java - Quel type stocke vrai ou faux
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Quel type de donnée peut stocker vrai ou faux ?', 'QCM_SIMPLE', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'int', false, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'boolean', true, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'double', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'String', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 1;

-- Q2: POO Java - Boucles
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Une boucle for peut itérer exactement 10 fois', 'VRAI_FAUX', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 1;

-- Q3: POO Java - Collections
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Quelles collections Java autorisent les doublons ?', 'QCM_MULTIPLE', 'MOYEN', 60, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'ArrayList', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'HashSet', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'TreeSet', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'LinkedList', true, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 2;

-- Q4: POO Java - Héritage
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('En Java, une classe peut hériter de combien de classes ?', 'QCM_SIMPLE', 'MOYEN', 45, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), '0', false, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), '1', true, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Plusieurs', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Illimité', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 3;

-- Q5: POO Java - Encapsulation
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Un accesseur (getter) peut modifier l''état de l''objet', 'VRAI_FAUX', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', false, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', true, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 4;

-- Q6: POO Java - Polymorphisme
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Le polymorphisme permet à une méthode d''avoir plusieurs implémentations', 'QCM_SIMPLE', 'MOYEN', 45, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Seulement en interface', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Jamais en Java', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 5;

-- Q7: POO Java - Exceptions
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Quelles exceptions doivent être déclarées avec throws ?', 'QCM_MULTIPLE', 'DIFFICILE', 75, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Checked Exception', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'IOException', true, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'NullPointerException', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'SQLException', true, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 6;

-- Q8: Génie Logiciel - Design Patterns
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Le pattern Singleton garantit une seule instance ?', 'QCM_SIMPLE', 'MOYEN', 45, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux - toujours plusieurs', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Dépend du contexte', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Jamais', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 7;

-- Q9: Génie Logiciel - SOLID
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Quels principes SOLID favorisent la modularité ?', 'QCM_MULTIPLE', 'DIFFICILE', 90, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Single Responsibility', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Interface Segregation', true, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Dependency Inversion', true, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Test Driven Development', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 8;

-- Q10: Génie Logiciel - Refactoring
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Le refactoring change le comportement externe du code', 'VRAI_FAUX', 'MOYEN', 40, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', false, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', true, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 9;

-- Q11: Génie Logiciel - Unit Testing
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('JUnit est un framework pour les tests unitaires ?', 'QCM_SIMPLE', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Unitaires', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Intégration uniquement', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Performance', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Sécurité', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 10;

-- Q12: Base de Données - SQL SELECT
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Quelle clause SQL sélectionne les colonnes ?', 'QCM_SIMPLE', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'SELECT', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'FROM', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'WHERE', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'JOIN', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 19;

-- Q13: Base de Données - Clés primaires
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Une clé primaire peut contenir NULL', 'VRAI_FAUX', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', false, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', true, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 20;

-- Q14: Base de Données - Jointures
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Quels types de JOIN existent en SQL ?', 'QCM_MULTIPLE', 'MOYEN', 60, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'INNER JOIN', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'LEFT JOIN', true, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'CROSS JOIN', true, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'DEEP JOIN', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 21;

-- Q15: Base de Données - Agrégation
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Quelle fonction SQL calcule la somme ?', 'QCM_SIMPLE', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'SUM()', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'TOTAL()', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'ADD()', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'AGGREGATE()', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 22;

-- Q16: Technologies Web - HTML
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('HTML signifie ?', 'QCM_SIMPLE', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Hyper Text Markup Language', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'High Tech Mark Language', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Home Tool Mark Language', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Hyperlinks and Text Mark List', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 29;

-- Q17: Technologies Web - CSS
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('CSS est un langage de programmation', 'VRAI_FAUX', 'MOYEN', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', false, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux - langage de style', true, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 30;

-- Q18: Technologies Web - JavaScript
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Quelles variables existent en JavaScript ?', 'QCM_MULTIPLE', 'MOYEN', 60, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'var', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'let', true, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'const', true, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'define', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 31;

-- Q19: Technologies Web - DOM
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Le DOM représente ?', 'QCM_SIMPLE', 'MOYEN', 45, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'La structure HTML en arbre', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Un fichier CSS', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Le serveur', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'La base de données', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 32;

-- Q20: Technologies Web - Responsive
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Mobile-first est une approche web moderne', 'VRAI_FAUX', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 33;

-- Q21: DevOps - Git
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Git est un système de contrôle de version ?', 'QCM_SIMPLE', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux - serveur FTP', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux - base données', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Seulement pour Windows', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 34;

-- Q22: DevOps - Docker
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Docker utilise la virtualisation complète', 'VRAI_FAUX', 'MOYEN', 40, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', false, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux - conteneurs légers', true, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 35;

-- Q23: DevOps - Kubernetes
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Quels objets Kubernetes existent ?', 'QCM_MULTIPLE', 'DIFFICILE', 75, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Pod', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Service', true, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Deployment', true, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Container', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 36;

-- Q24: DevOps - TCP/IP
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('TCP/IP est un modèle en couches ?', 'QCM_SIMPLE', 'MOYEN', 45, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai - 4 ou 5 couches', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux - pas de couches', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux - OSI seulement', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Seulement 2 couches', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 37;

-- Q25: DevOps - CI/CD
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('CI/CD automatise le déploiement ?', 'VRAI_FAUX', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 38;

-- Q26: Gestion de Projet - Agile
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Agile valorise les individus plus que les processus ?', 'QCM_SIMPLE', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Partiellement', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Seulement Waterfall', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 11;

-- Q27: Gestion de Projet - Scrum
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Un sprint Scrum dure généralement 2 semaines', 'VRAI_FAUX', 'MOYEN', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux - 1 semaine toujours', false, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 12;

-- Q28: Gestion de Projet - WBS
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('WBS signifie ?', 'QCM_SIMPLE', 'MOYEN', 45, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Work Breakdown Structure', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Web Based System', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Windows Basic Service', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Work Balance System', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 13;

-- Q29: Gestion de Projet - Risques
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('L''analyse de risques se fait une fois au début', 'VRAI_FAUX', 'MOYEN', 40, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', false, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux - continue', true, 2;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 14;

-- Q30: AI - Machine Learning
INSERT INTO questions (enonce, type, difficulte, duree_secondes, actif, date_creation) 
VALUES ('Le Machine Learning fait partie de l''IA ?', 'QCM_SIMPLE', 'FACILE', 30, true, NOW());

INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Vrai', true, 1;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Faux', false, 2;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Seulement Deep Learning', false, 3;
INSERT INTO choix (question_id, contenu, est_correct, ordre) 
SELECT (SELECT MAX(id) FROM questions), 'Inverse', false, 4;

INSERT INTO questions_competences (question_id, competence_id) 
SELECT (SELECT MAX(id) FROM questions), 25;

-- Verification - affiche le nombre de questions après cette migration
SELECT 'Migration V18 complétée. Questions insérées: ' || COUNT(*) as info FROM questions;
