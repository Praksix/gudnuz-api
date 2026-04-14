# ╔══════════════════════════════════════════════════════════════╗
# ║                      ÉTAPE 1 : BUILD                        ║
# ║  On utilise une image avec le JDK complet pour compiler      ║
# ╚══════════════════════════════════════════════════════════════╝

# Image de base : Eclipse Temurin = distribution Java open-source fiable
# "21-jdk" = Java 21 avec le compilateur (JDK), nécessaire pour Maven
# "AS build" = on nomme cette étape "build" pour y faire référence plus bas
FROM eclipse-temurin:21-jdk AS build

# Définit /app comme répertoire de travail dans ce container
# Toutes les commandes suivantes s'exécutent depuis /app
WORKDIR /app

# Copie tout le contenu du projet (code source, pom.xml, mvnw, etc.)
# "." source = répertoire local où tu lances `docker build`
# "." destination = /app dans le container (WORKDIR défini juste au-dessus)
COPY . .

# Lance Maven pour compiler le projet et créer le fichier .jar dans /app/target/
# "clean" = supprime les fichiers compilés précédents
# "package" = compile + génère le .jar
# "-DskipTests" = on ignore les tests pour accélérer le build Docker
RUN ./mvnw clean package -DskipTests


# ╔══════════════════════════════════════════════════════════════╗
# ║                      ÉTAPE 2 : RUN                          ║
# ║  Image finale allégée — contient uniquement le .jar          ║
# ╚══════════════════════════════════════════════════════════════╝

# Nouvelle image de base, plus légère : JRE seulement (pas de compilateur)
# Le JRE suffit pour exécuter le .jar — inutile d'embarquer tout le JDK en prod
# C'est le principe du "multi-stage build" : image finale sans le code source ni Maven
FROM eclipse-temurin:21-jre

# Répertoire de travail du container final (même convention que l'étape build)
WORKDIR /app

# Copie uniquement le .jar depuis l'étape "build" — rien d'autre
# "--from=build" = pioche dans le container de l'étape 1 nommée "build"
# "/app/target/*.jar" = le .jar généré par Maven (le * évite de hardcoder le nom)
# "app.jar" = nom simplifié donné au .jar dans l'image finale
COPY --from=build /app/target/*.jar app.jar

# Documente que l'application écoute sur le port 8080
# Purement informatif : ne publie pas le port (c'est docker-compose qui s'en charge)
EXPOSE 8080

# Commande exécutée au démarrage du container
# Forme JSON (recommandée) : évite que la commande passe par un shell intermédiaire
ENTRYPOINT ["java", "-jar", "app.jar"]