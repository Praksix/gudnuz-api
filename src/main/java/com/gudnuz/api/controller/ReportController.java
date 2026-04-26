package com.gudnuz.api.controller;

import com.gudnuz.api.dto.request.ReportRequest;
import com.gudnuz.api.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/{postId}/report")
    public ResponseEntity<Void> reportPost(@PathVariable UUID postId, @Valid @RequestBody ReportRequest request, @AuthenticationPrincipal UserDetails userDetails ){
        reportService.reportPost(postId, request, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
