package com.gudnuz.api.service;

import com.gudnuz.api.dto.request.DeleteAccountRequest;
import com.gudnuz.api.dto.request.UpdateProfileRequest;
import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.dto.response.UserResponse;
import com.gudnuz.api.exception.InvalidCredentialsException;
import com.gudnuz.api.exception.ResourceNotFoundException;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.enums.PostStatut;
import com.gudnuz.api.repository.PostRepository;
import com.gudnuz.api.repository.UserPostSparkRepository;
import com.gudnuz.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostService postService;
    private final PostRepository postRepository;
    private final UserPostSparkRepository userPostSparkRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public UserResponse getMyProfile (String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + email));
        return UserResponse.builder()
                .id(user.getId())
                .pseudo(user.getPseudo())
                .email(user.getEmail())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .userLevel(user.getUserLevel().name())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .postsCount(postRepository.countByUserId(user.getId()))
                .sparksCount(userPostSparkRepository.countSparksByUserId(user.getId()))
                .flaresCount(postRepository.countByUserIdAndStatut(user.getId(),PostStatut.ELECTED))
                .build();

    }

    public UserResponse getUserProfile (UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + userId));
        return UserResponse.builder()
                .id(user.getId())
                .pseudo(user.getPseudo())
                .email(user.getEmail())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
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

    public UserResponse updateUserProfile(String email, UpdateProfileRequest request, MultipartFile avatar ) throws IOException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + email));


        //MDP
        //-----------------------
        if (request.getNewPassword() != null) {

            if (request.getCurrentPassword() == null || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new InvalidCredentialsException("Mot de passe actuel incorrect");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        //BIO
        //-----------------------
        if (request.getBio() != null) {

            user.setBio(request.getBio());
        }

        //AVATAR
        //-----------------------
        String imageUrl = null;
        if (avatar != null && !avatar.isEmpty()) {
            String fileName = fileStorageService.storeFile(avatar);
            imageUrl = "/uploads/" + fileName;
            user.setAvatarUrl(imageUrl);
        }

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .pseudo(user.getPseudo())
                .email(user.getEmail())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .userLevel(user.getUserLevel().name())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .postsCount(postRepository.countByUserId(user.getId()))
                .sparksCount(userPostSparkRepository.countSparksByUserId(user.getId()))
                .flaresCount(postRepository.countByUserIdAndStatut(user.getId(),PostStatut.ELECTED))
                .build();

    }


    public void deleteAccount(String email, DeleteAccountRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + email));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Mot de passe actuel incorrect");
        }

        user.setPseudo("UtilisateurSupprimé"+user.getId());
        user.setEmail(user.getId()+"@gudnuz.com");
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setBio(null);
        user.setAvatarUrl(null);
        user.setDeleted(true);

        userRepository.save(user);

    }
}

