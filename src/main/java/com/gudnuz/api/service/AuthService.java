package com.gudnuz.api.service;

import com.gudnuz.api.dto.request.LoginRequest;
import com.gudnuz.api.dto.request.RegisterRequest;
import com.gudnuz.api.dto.response.AuthResponse;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.enums.Role;
import com.gudnuz.api.exception.DuplicateResourceException;
import com.gudnuz.api.repository.UserRepository;
import com.gudnuz.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Inscription d'un nouvel utilisateur.
     *
     * 1. Vérifie que l'email n'est pas déjà pris
     * 2. Crée l'entité User avec le mot de passe hashé (jamais en clair)
     * 3. Sauvegarde en base
     * 4. Génère un JWT pour que l'utilisateur soit connecté immédiatement après l'inscription
     * 5. Renvoie le token au client
     */
    public AuthResponse register(RegisterRequest request) {
        // Si l'email existe déjà en base, on refuse l'inscription
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email déjà utilisé");
        }

        if (userRepository.existsByPseudo(request.getPseudo())) {
            throw new DuplicateResourceException("Pseudo déjà utilisé");
        }

        // Construction de l'entité User via le Builder Lombok
        // passwordEncoder.encode() transforme "monMotDePasse" en "$2a$10$xK3jG..." (hash BCrypt)
        User user = User.builder()
                .pseudo(request.getPseudo())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Tout nouvel inscrit est USER par défaut
                .build();

        // Sauvegarde en base — JPA génère automatiquement l'UUID et le createdAt
        userRepository.save(user);

        // Génère le JWT contenant l'email de l'utilisateur
        String token = jwtService.generateToken(user);

        // Renvoie le token dans un AuthResponse
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    /**
     * Connexion d'un utilisateur existant.
     *
     * 1. Spring Security vérifie l'email + mot de passe via l'AuthenticationManager
     *    → si les credentials sont mauvais, une exception est lancée automatiquement
     *    → on n'a pas besoin de gérer l'erreur nous-mêmes ici
     * 2. Charge l'utilisateur depuis la base
     * 3. Génère un JWT
     * 4. Renvoie le token
     */
    public AuthResponse login(LoginRequest request) {
        // authenticate() fait tout le travail :
        // - cherche l'utilisateur par email (via le UserDetailsService du SecurityConfig)
        // - compare le mot de passe fourni avec le hash BCrypt en base
        // - lance BadCredentialsException si ça ne matche pas
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Si on arrive ici, les credentials sont valides
        // On charge l'utilisateur pour pouvoir générer son token
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }
}