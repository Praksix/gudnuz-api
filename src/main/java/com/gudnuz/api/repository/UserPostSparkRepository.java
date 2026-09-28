package com.gudnuz.api.repository;

import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.UserPostSpark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import com.gudnuz.api.dto.response.TopUserResponse;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserPostSparkRepository extends JpaRepository<UserPostSpark, UUID> {

    boolean existsByUserAndPost(User user, Post post);
    void deleteByUserAndPost(User user, Post post);

    @Query("SELECT COUNT(s) FROM UserPostSpark s WHERE s.post.user.id = :userId")
    int countSparksByUserId(UUID userId);


    @Query("SELECT new com.gudnuz.api.dto.response.TopUserResponse( u.id, u.pseudo, u.avatarUrl, COUNT(s))                                                                                                                                                                                 \n" +
            "          FROM UserPostSpark s                                                                                                                                                              \n" +
            "          JOIN s.post p                                                                                                                                                                     \n" +
            "          JOIN p.user u                                                                                                                                                                     \n" +
            "          WHERE u.deleted = false                                                                                                                                                           \n" +
            "          GROUP BY u.id, u.pseudo, u.avatarUrl                                                                                                                                              \n" +
            "          ORDER BY COUNT(s) DESC")
    List<TopUserResponse> findTopUsers(Pageable pageable);

}

