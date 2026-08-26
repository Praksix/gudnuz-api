package com.gudnuz.api.service;

import com.gudnuz.api.dto.request.DeleteAccountRequest;
import com.gudnuz.api.dto.request.UpdateProfileRequest;
import com.gudnuz.api.dto.response.UserResponse;
import com.gudnuz.api.exception.InvalidCredentialsException;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.enums.Role;
import com.gudnuz.api.repository.PostRepository;
import com.gudnuz.api.repository.UserPostSparkRepository;
import com.gudnuz.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.assertj.core.api.Assertions.assertThat;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserPostSparkRepository userPostSparkRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UserService userService;

    @Test
    void updateUserBio() throws IOException {
        // Arrange
        String email = "test@gudnuz.com";

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .pseudo("nathan")
                .role(Role.USER)
                .bio("ancienne bio")
                .build();

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .bio("nouvelle bio")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        MultipartFile avatar = null;

        // Act (= j'appelle la méthode que je teste)
        UserResponse result = userService.updateUserProfile(email, request, avatar);

        // Assert (= je vérifie que le résultat est celui attendu)
        assertThat(result.getBio()).isEqualTo("nouvelle bio");
    }


    @Test
    void updateUserPasswordShouldFail() {
        // Arrange
        String email = "test@gudnuz.com";

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .pseudo("nathan")
                .role(Role.USER)
                .bio("ancienne bio")
                .password("azertyuiop")
                .build();

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .bio("nouvelle bio")
                .currentPassword("oupsmauvaispassword")
                .newPassword("qsdfghjklm")
                .build();

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        MultipartFile avatar = null;

        // Act (= j'appelle la méthode que je teste)
        assertThrows(InvalidCredentialsException.class, () -> {
            userService.updateUserProfile(email, request, avatar);
        });

    }


    @Test
    void updateUserPasswordShouldPass() throws IOException {
        // Arrange
        String email = "test@gudnuz.com";

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .pseudo("nathan")
                .role(Role.USER)
                .bio("ancienne bio")
                .password("azertyuiop")
                .build();

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .bio("nouvelle bio")
                .currentPassword("azertyuiop")
                .newPassword("qsdfghjklm")
                .build();

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("motDePasseEncode");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        MultipartFile avatar = null;

        // Act (= j'appelle la méthode que je teste)
        UserResponse result = userService.updateUserProfile(email, request, avatar);


        //Assert
        assertThat(existingUser.getPassword()).isEqualTo("motDePasseEncode");

    }

    @Test
    void updateUserBioStaysIfNull() throws IOException {
        // Arrange
        String email = "test@gudnuz.com";

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .pseudo("nathan")
                .role(Role.USER)
                .bio("ancienne bio")
                .build();

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .bio(null)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        MultipartFile avatar = null;

        // Act (= j'appelle la méthode que je teste)
        UserResponse result = userService.updateUserProfile(email, request, avatar);


        //Assert
        assertThat(existingUser.getBio()).isEqualTo("ancienne bio");

    }


    @Test
    void deleteUserTest() {
        // Arrange
        String email = "test@gudnuz.com";

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .pseudo("nathan")
                .password("azertyuiop")
                .role(Role.USER)
                .bio("ancienne bio")
                .build();

        DeleteAccountRequest request = DeleteAccountRequest.builder()
                .currentPassword("azertyuiop")
                .build();

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));


        // Act (= j'appelle la méthode que je teste)
        userService.deleteAccount(email, request);


        //Assert
        assertThat(existingUser.getBio()).isEqualTo(null);
        assertThat(existingUser.getAvatarUrl()).isEqualTo(null);
        assertThat(existingUser.getDeleted()).isEqualTo(true);
        assertThat(existingUser.getPseudo()).isEqualTo("UtilisateurSupprimé"+existingUser.getId());
        assertThat(existingUser.getEmail()).isEqualTo(existingUser.getId()+"@gudnuz.com");

    }

    @Test
    void deleteUserTestShouldFail() {
        // Arrange
        String email = "test@gudnuz.com";

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .pseudo("nathan")
                .password("azertyuiop")
                .role(Role.USER)
                .bio("ancienne bio")
                .build();

        DeleteAccountRequest request = DeleteAccountRequest.builder()
                .currentPassword("azertyuiop")
                .build();

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));


        //Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            userService.deleteAccount(email, request);
        });

    }
}
