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
    List<Post> findByCreatedAtAfterAndCategorieOrderByCreatedAtDesc(LocalDateTime since, Categorie categorie);

    List<Post> findByUserIdOrderByCreatedAtDesc(UUID userId);


    @Query("SELECT p FROM Post p WHERE p.createdAt > :since AND (p.statut IS NULL OR p.statut != 'REMOVED') ORDER BY p.sparksCount DESC")
    List<Post> findTopPostsSince(@Param("since") LocalDateTime since);

    List<Post> findByStatutOrderByCreatedAtDesc(PostStatut statut);

    int countByUserId(UUID userId);

    int countByUserIdAndStatut(UUID userId, PostStatut statut);




}
