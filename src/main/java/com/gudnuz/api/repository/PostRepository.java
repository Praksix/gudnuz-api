package com.gudnuz.api.repository;

import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.enums.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime since);
    List<Post> findByCreatedAtAfterAndCategorieOrderByCreatedAtDesc(LocalDateTime since, Categorie categorie);

    List<Post> findByUserIdOrderByCreatedAtDesc(UUID userId);

}
