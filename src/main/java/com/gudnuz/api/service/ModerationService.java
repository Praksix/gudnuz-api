package com.gudnuz.api.service;

import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.dto.response.ReportResponse;
import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.PostReport;
import com.gudnuz.api.model.enums.PostStatut;
import com.gudnuz.api.repository.PostReportRepository;
import com.gudnuz.api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final PostReportRepository postReportRepository;
    private final PostRepository postRepository;

    public List<ReportResponse> getAllReports(){
        List<PostReport> reports = postReportRepository.findAll();
        return reports.stream()
                .map(report -> ReportResponse.builder()
                        .id(report.getId())
                        .postId(report.getPost().getId())
                        .postTitre(report.getPost().getTitre())
                        .motif(report.getMotif())
                        .signalePar(report.getUser().getPseudo())
                        .createdAt(report.getCreatedAt())
                        .build())
                .toList();
    }

    public void removePost(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        post.setStatut(PostStatut.REMOVED);
        postRepository.save(post);
    }



}
