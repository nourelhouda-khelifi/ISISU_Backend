# ISISU Platform
# http://localhost:8080/swagger-ui.html

### Requis
- Docker 
- Docker Compose 

##  Vérifier l'installation

```bash
docker --version
docker compose version
```

## Installation et Démarrage

### 1. Cloner le projet
```bash
git clone <votre-repo>
cd demo
```
### 2. Lancer l'application
```bash
docker compose up --build
```
Cette commande va :
- Construire l'image Docker du backend
- Démarrer PostgreSQL
- Démarrer l'application Spring Boot

**Le backend sera accessible à** : `http://localhost:8080`

### � Documentation API avec Swagger

Une fois l'application démarrée, accédez à la documentation interactive :

- **Swagger UI** : [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON** : [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs)

Swagger UI permet de :
- ✅ Consulter tous les endpoints disponibles
- ✅ Voir les descriptions et paramètres
- ✅ Tester les endpoints directement depuis l'interface
- ✅ Générer du code client

### �🔐 Authentification

Vous serez invité à entrer vos identifiants. Utilisez :
- **Username** : `admin`
- **Password** : `admin123`

> **Note** : Ces identifiants sont configurés dans [application.properties](src/main/resources/application.properties). Modifiez-les avant de déployer en production!

### Services Docker

#### PostgreSQL
```yaml
Conteneur: isisu_postgres
Port: 5432
Base de données: isisu_db
Utilisateur: isisu_user
Mot de passe: isisu_password
Volume: postgres_data (persistant)
```

#### Spring Boot Backend
```yaml
Conteneur: isisu_backend
Port: 8080
Image: Eclipse Temurin 21-JDK (Alpine)
Dépendances: PostgreSQL
```


### Se connecter à PostgreSQL

Accéder au conteneur PostgreSQL :
```bash
docker exec -it isisu_postgres bash
```

Connecter via psql :
```bash
psql -U isisu_user -d isisu_db
```

### Consulter les tables
```sql
\dt
```

### Autres commandes utiles
```sql
\l              -- Lister les bases de données
\du             -- Lister les utilisateurs
\q              -- Quitter psql
```


## 🔧 Commandes utiles

```bash
# Démarrer le projet
docker compose up -d

# Arrêter le projet
docker compose down

# Voir les logs du backend
docker logs -f isisu_backend

# Voir les logs de PostgreSQL
docker logs -f isisu_postgres

# Accéder au shell du backend
docker exec -it isisu_backend bash

# Reconstruire les images
docker compose up --build --force-recreate
```


## 🐛 Dépannage

### PostgreSQL ne démarre pas
```bash
# Nettoyer les volumes
docker compose down -v
docker compose up --build
```

### Port déjà utilisé
Modifier les ports dans `docker-compose.yml` si les ports 5432 ou 8080 sont occupés.

### Erreur de connexion base de données
Vérifier que le service PostgreSQL est démarré :
```bash
docker compose ps
```


admin@isisu.fr
admin123