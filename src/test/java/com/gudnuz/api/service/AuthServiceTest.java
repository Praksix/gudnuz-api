package com.gudnuz.api.service;

import com.gudnuz.api.dto.request.LoginRequest;
import com.gudnuz.api.dto.request.RegisterRequest;
import com.gudnuz.api.dto.response.AuthResponse;
import com.gudnuz.api.exception.DuplicateResourceException;
import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.enums.Role;
import com.gudnuz.api.repository.UserRepository;
import com.gudnuz.api.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerSuccess() {

        RegisterRequest request = RegisterRequest.builder()
                .email("email")
                .pseudo("pseudo")
                .build();

        when(jwtService.generateToken(any(User.class))).thenReturn("fake-token");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPseudo(request.getPseudo())).thenReturn(false);

        AuthResponse authResponse = authService.register(request);


        assertThat(authResponse.getToken()).isEqualTo("fake-token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerFailEmailTaken() {

        RegisterRequest request = RegisterRequest.builder()
                .email("email")
                .pseudo("pseudo")
                .build();


        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);


        assertThrows(DuplicateResourceException.class, () -> {
            authService.register(request);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerFailPseudoTaken() {
        RegisterRequest request = RegisterRequest.builder()
                .email("email")
                .pseudo("pseudo")
                .build();


        when(userRepository.existsByPseudo(request.getPseudo())).thenReturn(true);


        assertThrows(DuplicateResourceException.class, () -> {
            authService.register(request);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void loginSuccess() {

        User user = User.builder()
                .email("email")
                .build();
        LoginRequest request = LoginRequest.builder()
                .email("email")
                .build();



        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("fake-token");

        AuthResponse authResponse = authService.login(request);

        assertThat(authResponse.getToken()).isEqualTo("fake-token");
    }

    @Test
    void loginFail() {

        LoginRequest request = LoginRequest.builder()
                .email("email")
                .build();

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(request);
        });
    }
}