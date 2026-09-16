# ╔══════════════════════════════════════════════════════════════╗
# ║                      ÉTAPE 1 : BUILD                        ║
# ║  On utilise une image avec le JDK complet pour compiler      ║
# ╚══════════════════════════════════════════════════════════════╝

FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests


# ╔══════════════════════════════════════════════════════════════╗
# ║                      ÉTAPE 2 : RUN                          ║
# ║  Image finale allégée — contient uniquement le .jar          ║
# ╚══════════════════════════════════════════════════════════════╝

FROM eclipse-temurin:21-jre

WORKDIR /app

# Crée un utilisateur système dédié, sans privilèges, sans shell de connexion
# "-r" = system account (pas de home, pas de mot de passe)
# "-g spring" = rattache cet utilisateur au groupe "spring"
RUN groupadd -r spring && useradd -r -g spring spring

# Copie le .jar généré par l'étape "build"
COPY --from=build /app/target/*.jar app.jar

# Le fichier appartient désormais à l'utilisateur "spring", pas à root
RUN chown spring:spring app.jar

# À partir d'ici, tout s'exécute avec l'identité "spring" — plus jamais root
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]