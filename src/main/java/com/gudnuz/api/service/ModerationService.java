package com.gudnuz.api.service;


import com.gudnuz.api.dto.response.ReportResponse;
import com.gudnuz.api.exception.ResourceNotFoundException;
import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.PostReport;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.enums.PostStatut;
import com.gudnuz.api.repository.PostReportRepository;
import com.gudnuz.api.repository.PostRepository;
import com.gudnuz.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final PostReportRepository postReportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    public List<ReportResponse> getAllReports(String email){
        User admin = userRepository.findByEmail(email).orElseThrow();
        List<PostReport> reports = postReportRepository.findAll();
        return reports.stream()
                .map(report -> ReportResponse.builder()
                        .id(report.getId())
                        .post(postMapper.mapToResponse(report.getPost(), admin))
                        .motif(report.getMotif())
                        .signalePar(report.getUser().getPseudo())
                        .createdAt(report.getCreatedAt())
                        .build())
                .toList();
    }

    @Transactional
    public void removePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post introuvable"));
        post.setStatut(PostStatut.REMOVED);
        postRepository.save(post);
        postReportRepository.deleteByPost(post);
    }


    public void ignoreReport(UUID reportId) {
        PostReport report = postReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("signalement inexistant"));

        postReportRepository.delete(report);
    }

}
