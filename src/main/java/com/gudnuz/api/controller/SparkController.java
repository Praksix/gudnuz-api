package com.gudnuz.api.controller;

import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.service.SparkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class SparkController {

    private final SparkService sparkService;

    @PostMapping("/{postId}/spark")
    public ResponseEntity<PostResponse> toggleSpark(@PathVariable UUID postId, @AuthenticationPrincipal UserDetails userDetails ){
      return ResponseEntity.ok(sparkService.toggleSpark(postId, userDetails.getUsername()));
    }

}
