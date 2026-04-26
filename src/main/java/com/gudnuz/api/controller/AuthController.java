package com.gudnuz.api.controller;

import com.gudnuz.api.dto.request.LoginRequest;
import com.gudnuz.api.dto.request.RegisterRequest;
import com.gudnuz.api.dto.response.AuthResponse;
import com.gudnuz.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller d'authentification.
 *
 * @RestController = cette classe gère des requêtes HTTP et renvoie du JSON directement
 * @RequestMapping("/api/auth") = toutes les routes de cette classe commencent par /api/auth
 *
 * Deux endpoints, tous les deux publics (configuré dans SecurityConfig avec permitAll) :
 * - POST /api/auth/register → inscription
 * - POST /api/auth/login    → connexion
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Inscription.
     *
     * @Valid déclenche la validation des annotations du DTO (@NotBlank, @Email, @Size)
     *        → si un champ est invalide, Spring renvoie automatiquement une erreur 400
     * @RequestBody dit à Spring de lire le corps JSON de la requête
     *        et de le transformer en objet RegisterRequest
     *
     * Exemple de requête :
     * POST /api/auth/register
     * {
     *   "pseudo": "nathan",
     *   "email": "nathan@example.com",
     *   "password": "monmotdepasse"
     * }
     *
     * Réponse : { "token": "eyJhbGciOiJIUzI1NiJ9..." }
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Connexion.
     *
     * Exemple de requête :
     * POST /api/auth/login
     * {
     *   "email": "nathan@example.com",
     *   "password": "monmotdepasse"
     * }
     *
     * Réponse : { "token": "eyJhbGciOiJIUzI1NiJ9..." }
     * Erreur : 403 si les credentials sont mauvais
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}