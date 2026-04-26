package com.gudnuz.api.service;

import com.gudnuz.api.dto.request.ReportRequest;
import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.PostReport;
import com.gudnuz.api.model.User;
import com.gudnuz.api.repository.PostReportRepository;
import com.gudnuz.api.repository.PostRepository;
import com.gudnuz.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostReportRepository postReportRepository;

    public void reportPost(UUID postId, ReportRequest request, String email){
        User user = userRepository.findByEmail(email).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        if (postReportRepository.existsByUserAndPost(user, post)){
           throw new RuntimeException("Vous avez déjà signalé ce post");

        }

            PostReport postReport = PostReport.builder()
                    .user(user)
                    .post(post)
                    .motif(request.getMotif())
                    .build();


        postReportRepository.save(postReport);
    }

}
