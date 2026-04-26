# GudNuz — Backend API (Spring Boot)

## Projet

GudNuz est une application mobile anti-doom scrolling. Les utilisateurs postent du contenu exclusivement positif dans 3 catégories : Actu, Culture, Humour. Ils peuvent liker les posts (les "Sparks") et toutes les 8h, le post avec le plus de Sparks est élu "Flare" et envoyé en notification push à tous les utilisateurs.

Ce dépôt contient le **backend API REST** en Spring Boot 4 / Java 21.


## Context du projet
Ce projet est un projet étudiant pour valider un diplome de Concepteur Développeur d'Application. Tu dois toujours expliquer précsément les modifications et erreurs. L'idée c'est d'apprendre en faisant


## Stack technique

- **Java 21** / **Spring Boot 4.0.5**
- **PostgreSQL 16** (via Docker, port hôte 5433)
- **Spring Security + JWT** (librairie JJWT 0.12.6)
- **Spring Data JPA** / Hibernate
- **Lombok**
- **Spring Scheduler** (cron élection Flare toutes les 8h)
- **Firebase Cloud Messaging** (notifications push HTTP v1)
- **JavaMailSender + Thymeleaf** (newsletter — La GudLetter)
- **Docker** multi-stage build (JDK 21 build → JRE 21 runtime)

## Commandes

```bash
# Build
./mvnw clean compile
./mvnw clean package -DskipTests

# Tests
./mvnw test

# Docker
docker compose up -d          # Démarre API + PostgreSQL
docker compose down            # Arrête tout
docker compose down -v         # Arrête + supprime les volumes (reset BDD)
docker compose logs -f api     # Logs de l'API en temps réel

# Accès BDD locale
psql -h localhost -p 5433 -U gudnuz_user -d gudnuz
```

## Architecture

Architecture en couches Spring Boot :
```
Controller → Service → Repository → Model (Entity)
```

Packages sous `com.gudnuz.api` :
```
config/          # SecurityConfig, JwtFilter, CorsConfig
controller/      # REST Controllers
dto/             # Request/Response DTOs
  request/
  response/
exception/       # Exceptions custom + GlobalExceptionHandler
model/           # Entités JPA
  enums/         # Role, Categorie, PostStatut, UserLevel
repository/      # Interfaces Spring Data JPA
security/        # JwtService, JwtAuthenticationFilter
service/         # Logique métier
```

## Vocabulaire métier (IMPORTANT)

Le projet utilise un vocabulaire custom — ne jamais utiliser les termes génériques :
- **Spark** = like/upvote (étincelle). Un utilisateur donne un Spark à un post.
- **Flare** = post élu (fusée éclairante). Le post avec le plus de Sparks sur une fenêtre de 8h.
- **GudBox** = historique des Flares passés.
- **GudLetter** = newsletter hebdomadaire (top posts par catégorie, envoyée chaque lundi matin).
- **Palier / User Level** = niveau utilisateur basé sur le nombre de Sparks reçus (pas de badges, pas de streak).

## Modèle de données — Décisions clés

- **UUID** comme clé primaire sur toutes les tables.
- **Pas de table Sparks** séparée → champ `sparks_count` sur Post + table de liaison `user_post_sparks` pour le tracking (un user = un spark par post).
- **Pas de table Categories** → enum Java `Categorie` (ACTU, CULTURE, HUMOUR) stockée comme VARCHAR avec CHECK constraint.
- **Pas de badges** → seulement des **paliers utilisateur** (enum `UserLevel`) : `GRAINE_DE_SOURIRE`, `SEMEUR_DE_RIRES`, `DEALER_DE_GOOD_VIBES`, `PUSHER_DE_BONHEUR`, `CARTEL_DU_KIFF`, `PARRAIN_DES_BONNES_ONDES`, `EMPEREUR_DU_FEEL_GOOD`.
- **Pas de streak**.
- **Soft delete** pour la modération : le champ `statut` sur Post utilise `REMOVED` (pas de DELETE physique).
- **Visibilité des posts** : 24h, filtré dynamiquement via `created_at > now() - interval '24 hours'` — pas de statut ALIVE/DEAD stocké.
- **Élection Flare** : toutes les 8h via Spring Scheduler. Un post peut être candidat sur 3 fenêtres max.
- **Table `post_reports`** : motif obligatoire, contrainte UNIQUE sur `(user_id, post_id)`.

## Conventions REST (STRICTES)

- **Jamais de user_id dans les URLs** → toujours dérivé du JWT.
- Routes `/me` pour les actions self-referentielles (ex: `GET /api/users/me`).
- Noms de ressources au **pluriel** (`/posts`, `/users`, `/reports`).
- **PATCH** pour les changements de statut (soft operations), jamais DELETE.
- Filtrage par catégorie via query param : `GET /api/posts?categorie=ACTU`.
- Réponses JSON. Pas de HATEOAS.

## Rôles et sécurité

3 rôles : `USER`, `MODERATOR`, `ADMIN`.
- Authentification par **JWT** (Bearer token).
- L'identité de l'utilisateur est **toujours** extraite du token, jamais d'un paramètre URL.
- Endpoints publics : inscription, connexion.
- Endpoints protégés : tout le reste, avec contrôle de rôle.

## Règles métier importantes

- RM-01 : Source obligatoire pour les posts de catégorie ACTU.
- RM-02 : Un utilisateur ne peut donner qu'un seul Spark par post.
- RM-03 : L'élection du Flare se fait toutes les 8h sur les posts de la fenêtre.
- RM-04 : Le Flare est envoyé en notification push à tous les utilisateurs via FCM.
- RM-05 : Un post est visible dans le feed pendant 24h (filtrage dynamique sur `created_at`).
- RM-06 : Un utilisateur ne peut signaler un post qu'une seule fois.
- RM-07 : Filtre de mots appliqué à la publication.
- RM-08 : La suppression d'un post par modération = changement de statut à REMOVED (soft delete).

## Workflow de développement

Quand tu implémentes un module, suis cet ordre :
1. **Entity** (model JPA) avec les annotations Lombok et les contraintes
2. **Enum** si nécessaire (dans `model/enums/`)
3. **Repository** (interface Spring Data JPA)
4. **DTO** Request et Response (dans `dto/request/` et `dto/response/`)
5. **Service** (logique métier, validation, exceptions)
6. **Controller** (endpoints REST, annotations de sécurité)
7. **Tests** unitaires du Service

## Conventions de code

- Lombok : `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` sur les entities et DTOs.
- Validation : annotations `@Valid`, `@NotBlank`, `@NotNull`, `@Size` sur les DTOs request.
- Exceptions : handler global avec `@RestControllerAdvice`. Exceptions custom par module.
- Nommage : classes en PascalCase, méthodes/variables en camelCase, constantes en UPPER_SNAKE_CASE.
- Langue du code : **anglais** pour le code (noms de classes, méthodes, variables). Français pour les commentaires si nécessaire.
- Commits en français.

## Ce qu'il ne faut PAS faire

- Ne pas créer d'entité séparée pour les catégories — c'est un enum.
- Ne pas créer d'entité séparée pour les Sparks — c'est un compteur + table de liaison.
- Ne pas mettre de user_id dans les URLs.
- Ne pas utiliser DELETE (HTTP) — utiliser PATCH pour les changements de statut.
- Ne pas implémenter de système de badges ou de streak.
- Ne pas implémenter de commentaires — il n'y en a pas dans GudNuz.
- Ne pas implémenter la GudLetter maintenant — c'est prévu en dernier.

## État actuel

- [x] Projet Spring Boot initialisé
- [x] Dockerfile multi-stage + docker-compose.yml
- [ ] Spring Security + JWT (en cours)
- [ ] Module Auth
- [ ] Module Posts
- [ ] Module Sparks
- [ ] Module Reports
- [ ] Module Flare Election (Scheduler)
- [ ] Module Notifications FCM
- [ ] Module Profil
- [ ] La GudLetter (en dernier)