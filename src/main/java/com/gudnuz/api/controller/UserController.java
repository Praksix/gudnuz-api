package com.gudnuz.api.controller;

import com.gudnuz.api.dto.request.DeleteAccountRequest;
import com.gudnuz.api.dto.request.UpdateProfileRequest;
import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.dto.response.TopUserResponse;
import com.gudnuz.api.dto.response.UserResponse;
import com.gudnuz.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getMyProfile(userDetails.getUsername()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @GetMapping("/{userId}/posts")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable UUID userId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserPosts(userId, userDetails.getUsername()));
    }

    @PatchMapping(value = "/me", consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> updateMyProfile(
            @Valid @ModelAttribute UpdateProfileRequest request,
            @RequestParam(required = false) MultipartFile avatar,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        return ResponseEntity.ok(userService.updateUserProfile(userDetails.getUsername(), request,avatar));
    }


    @PatchMapping(value = "/me/delete")
    public ResponseEntity<Void> deleteMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DeleteAccountRequest request) {
        userService.deleteAccount(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/top")
    public ResponseEntity<List<TopUserResponse>> getTopUsers() {
        return ResponseEntity.ok(userService.getTopUsers());
    }



}
