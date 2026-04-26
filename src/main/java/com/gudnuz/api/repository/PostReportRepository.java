package com.gudnuz.api.repository;

import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.PostReport;
import com.gudnuz.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, UUID> {
    boolean existsByUserAndPost(User user, Post post);
}
