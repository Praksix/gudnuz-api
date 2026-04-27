package com.gudnuz.api.controller;

import com.gudnuz.api.dto.response.ReportResponse;
import com.gudnuz.api.service.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;

    @GetMapping("/reports")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(moderationService.getAllReports());
    }

    @PatchMapping("/posts/{postId}/remove")
    public ResponseEntity<Void> removePost(@PathVariable UUID postId) {
        moderationService.removePost(postId);
        return ResponseEntity.ok().build();
    }
}