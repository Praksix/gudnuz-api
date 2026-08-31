package com.gudnuz.api.controller;


import com.gudnuz.api.dto.request.CreatePostRequest;
import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.service.FileStorageService;
import com.gudnuz.api.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final FileStorageService fileStorageService;



    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<PostResponse> createPost(
            @Valid @ModelAttribute CreatePostRequest request,
            @RequestParam(required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        return ResponseEntity.ok(postService.createPost(
                request.getTitre(),
                request.getContenu(),
                request.getCategorie(),
                request.getSource(),
                image,
                userDetails.getUsername()));
    }



    @GetMapping
    public ResponseEntity<List<PostResponse>> getFeed(@RequestParam(required = false) String categorie, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.getFeed(categorie, userDetails.getUsername()));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PostResponse>> getMyPosts(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.getMyPosts(userDetails.getUsername()));
    }

}
