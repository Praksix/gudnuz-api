package com.gudnuz.api.config;

import com.gudnuz.api.repository.UserRepository;
import com.gudnuz.api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration      // Indique à Spring que cette classe contient de la configuration
@EnableWebSecurity  // Active la sécurité web de Spring Security
@RequiredArgsConstructor
public class SecurityConfig {


    private final UserRepository userRepository;

    // Le filtre JWT
    // Spring l'injecte automatiquement via le constructeur (grâce à @RequiredArgsConstructor)
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configure la chaîne de sécurité HTTP.
     * C'est ici qu'on décide quels endpoints sont publics, lesquels sont protégés,
     * et comment Spring gère l'authentification.
     *
     * @Bean = Spring appelle cette méthode au démarrage et garde le résultat
     * en mémoire pour l'utiliser partout dans l'appli
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Désactive la protection CSRF (Cross-Site Request Forgery)
                // Le CSRF c'est une protection pour les formulaires web avec sessions
                // En API REST stateless avec JWT, c'est inutile
                .csrf(csrf -> csrf.disable())

                // Configure la gestion de session en STATELESS
                // Ça veut dire : Spring ne crée jamais de session HTTP côté serveur
                // Chaque requête est indépendante, authentifiée uniquement par le JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Définit les règles d'accès aux endpoints
                .authorizeHttpRequests(auth -> auth
                        // Tout ce qui commence par /api/auth/ est public (login, register)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        // Tout le reste nécessite un JWT valide
                        .anyRequest().authenticated()
                )

                // Insère notre filtre JWT AVANT le filtre par défaut de Spring
                // Comme ça, quand une requête arrive, c'est notre JwtAuthenticationFilter
                // qui s'exécute en premier pour vérifier le token
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // Construit et renvoie la chaîne de sécurité configurée
                .build();
    }

    /**
     * Fournit l'encodeur de mots de passe.
     * BCrypt est l'algorithme standard : il hash le mot de passe à l'inscription
     * et le vérifie au login. Jamais de mot de passe en clair en base.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Fournit l'AuthenticationManager de Spring.
     * C'est l'objet qui orchestre le processus de login :
     * il prend un email + mot de passe, cherche l'utilisateur en base,
     * vérifie le hash du mot de passe, et valide ou refuse.
     * On en aura besoin dans le AuthService pour le endpoint de login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    /**
     * Indique à Spring Security comment charger un utilisateur à partir de son email.
     * Spring en a besoin quand l'AuthenticationManager vérifie les credentials au login.
     * Le lambda prend un email, cherche en base, et renvoie le User (qui implémente UserDetails).
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}