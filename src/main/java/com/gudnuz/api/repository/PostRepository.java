package com.gudnuz.api.repository;

import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.enums.Categorie;
import com.gudnuz.api.model.enums.PostStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    //List<Post> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime since);
    // Requête explicite (et pas une méthode dérivée type "...StatutNot...") car "statut <> :statut" en SQL
    // exclut aussi les lignes où statut IS NULL (logique à 3 valeurs), alors qu'un statut NULL = post normal,
    // ni élu ni supprimé, qui doit rester dans le feed. Même pattern que findTopPostsSince ci-dessous.
    @Query("SELECT p FROM Post p WHERE p.createdAt > :since AND p.categorie = :categorie AND (p.statut IS NULL OR p.statut != :statut) ORDER BY p.createdAt DESC")
    List<Post> findByCreatedAtAfterAndCategorieAndStatutNotOrderByCreatedAtDesc(@Param("since") LocalDateTime since, @Param("categorie") Categorie categorie, @Param("statut") PostStatut statut);

    // Même souci de logique à 3 valeurs que ci-dessus avec une méthode dérivée "...StatutNot...".
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND (p.statut IS NULL OR p.statut != :statut) ORDER BY p.createdAt DESC")
    List<Post> findByUserIdAndStatutNotOrderByCreatedAtDesc(@Param("userId") UUID userId, @Param("statut") PostStatut statut);


    @Query("SELECT p FROM Post p WHERE p.createdAt > :since AND (p.statut IS NULL OR p.statut != 'REMOVED') ORDER BY p.sparksCount DESC")
    List<Post> findTopPostsSince(@Param("since") LocalDateTime since);

    // NULLS LAST explicite (même remarque que ci-dessus, mais sur l'ordre plutôt que le filtre) :
    // sans ça, Postgres trie les NULL en premier sur un ORDER BY ... DESC, donc un post ELECTED
    // dont electedAt est encore null (élu avant l'ajout de ce champ) remonterait en tête devant
    // un post réellement élu plus récemment.
    @Query("SELECT p FROM Post p WHERE p.statut = :statut ORDER BY p.electedAt DESC NULLS LAST")
    List<Post> findByStatutOrderByElectedAtDesc(@Param("statut") PostStatut statut);

    // LIMIT 1 obligatoire : avec un @Query, Spring Data ignore le nom de la méthode,
    // donc le "First" de findFirstBy... ne limite plus rien (→ plusieurs résultats pour un Optional → 500)
    @Query("SELECT p FROM Post p WHERE p.statut = :statut ORDER BY p.electedAt DESC NULLS LAST LIMIT 1")
    Optional<Post> findFirstByStatutOrderByElectedAtDesc(@Param("statut") PostStatut statut);



    // Dédiée à l'élection : contrairement à findTopPostsSince (feed "TOP", qui garde les posts déjà
    // ELECTED), on exclut ici tout post déjà élu pour ne jamais réélire le même deux fois de suite.
    @Query("SELECT p FROM Post p WHERE p.createdAt > :since AND p.statut IS NULL ORDER BY p.sparksCount DESC")
    List<Post> findNextEligiblePost(@Param("since") LocalDateTime since);



    int countByUserId(UUID userId);

    int countByUserIdAndStatut(UUID userId, PostStatut statut);




}
