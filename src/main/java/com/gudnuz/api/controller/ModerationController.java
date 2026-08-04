package com.gudnuz.api.controller;

import com.gudnuz.api.dto.response.ReportResponse;
import com.gudnuz.api.service.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<ReportResponse>> getAllReports(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(moderationService.getAllReports(userDetails.getUsername()));
    }

    @PatchMapping("/posts/{postId}/remove")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> removePost(@PathVariable UUID postId) {
        moderationService.removePost(postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reports/{reportId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> ignoreReport(@PathVariable UUID reportId) {
        moderationService.ignoreReport(reportId);
        return ResponseEntity.ok().build();
    }
}