package com.gudnuz.api.service;

import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.dto.response.UserResponse;
import com.gudnuz.api.model.User;
import com.gudnuz.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostService postService;

    public UserResponse getMyProfile (String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return UserResponse.builder()
                .id(user.getId())
                .pseudo(user.getPseudo())
                .email(user.getEmail())
                .userLevel(user.getUserLevel().name())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();

    }

    public UserResponse getUserProfile (UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return UserResponse.builder()
                .id(user.getId())
                .pseudo(user.getPseudo())
                .email(user.getEmail())
                .userLevel(user.getUserLevel().name())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();

    }


    public List<PostResponse> getUserPosts(UUID userId) {
        return postService.getPostsByUser(userId);
    }
}
