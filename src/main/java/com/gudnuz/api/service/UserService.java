package com.gudnuz.api.service;

import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.dto.response.UserResponse;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.enums.PostStatut;
import com.gudnuz.api.repository.PostRepository;
import com.gudnuz.api.repository.UserPostSparkRepository;
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
    private final PostRepository postRepository;
    private final UserPostSparkRepository userPostSparkRepository;

    public UserResponse getMyProfile (String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return UserResponse.builder()
                .id(user.getId())
                .pseudo(user.getPseudo())
                .email(user.getEmail())
                .bio(user.getBio())
                .userLevel(user.getUserLevel().name())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .postsCount(postRepository.countByUserId(user.getId()))
                .sparksCount(userPostSparkRepository.countSparksByUserId(user.getId()))
                .flaresCount(postRepository.countByUserIdAndStatut(user.getId(),PostStatut.ELECTED))
                .build();

    }

    public UserResponse getUserProfile (UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return UserResponse.builder()
                .id(user.getId())
                .pseudo(user.getPseudo())
                .email(user.getEmail())
                .bio(user.getBio())
                .userLevel(user.getUserLevel().name())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .postsCount(postRepository.countByUserId(userId))
                .sparksCount(userPostSparkRepository.countSparksByUserId(userId))
                .flaresCount(postRepository.countByUserIdAndStatut(userId,PostStatut.ELECTED))
                .build();

    }


    public List<PostResponse> getUserPosts(UUID userId, String viewerEmail) {
        return postService.getPostsByUser(userId, viewerEmail);
    }
}
