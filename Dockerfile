# Image Java 17 légère
FROM eclipse-temurin:21-jdk-alpine
# Dossier de travail
WORKDIR /app

# Copier le jar
COPY target/*.jar app.jar

# Exposer le port
EXPOSE 8080

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
