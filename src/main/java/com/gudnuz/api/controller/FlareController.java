package com.gudnuz.api.controller;

import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.service.FlareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flare")
@RequiredArgsConstructor
public class FlareController {

    private final FlareService flareService;

    @GetMapping("/current")
    public ResponseEntity<PostResponse> getCurrentFlare(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(flareService.getCurrentFlare(userDetails.getUsername()));
    }

    @GetMapping("/gudbox")
    public ResponseEntity<List<PostResponse>> getGudBox(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(flareService.getGudBox(userDetails.getUsername()));
    }



}