package com.gudnuz.api.repository;

import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.UserPostSpark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserPostSparkRepository extends JpaRepository<UserPostSpark, UUID> {

    boolean existsByUserAndPost(User user, Post post);
    void deleteByUserAndPost(User user, Post post);

    @Query("SELECT COUNT(s) FROM UserPostSpark s WHERE s.post.user.id = :userId")
    int countSparksByUserId(UUID userId);

}
