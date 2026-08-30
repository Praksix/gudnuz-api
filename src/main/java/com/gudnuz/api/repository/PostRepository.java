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

    List<Post> findByStatutOrderByCreatedAtDesc(PostStatut statut);

    int countByUserId(UUID userId);

    int countByUserIdAndStatut(UUID userId, PostStatut statut);




}
