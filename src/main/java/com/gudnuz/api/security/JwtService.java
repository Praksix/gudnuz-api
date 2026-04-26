package com.gudnuz.api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;           // Classe principale JJWT pour créer/parser les tokens
import io.jsonwebtoken.io.Decoders;   // Utilitaire pour décoder la clé secrète depuis Base64
import io.jsonwebtoken.security.Keys; // Fabrique de clés cryptographiques
import javax.crypto.SecretKey;        // Type de la clé symétrique HMAC
import java.util.Date;                // Utilisé pour les dates d'émission et d'expiration

@Service // Spring gère cette classe comme un bean singleton injecté partout où besoin
public class JwtService {

    @Value("${application.security.jwt.secret-key}") // Injecte la valeur depuis application.yml
    private String secretKey; // Clé secrète en Base64 — sert à signer et vérifier les tokens

    @Value("${application.security.jwt.expiration}") // Injecte la durée de vie en millisecondes
    private long expiration; // Ex: 86400000 = 24h

    // Génère un token JWT signé pour un utilisateur authentifié
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())                            // "sub" = identifiant principal (email ou username)
                .issuedAt(new Date(System.currentTimeMillis()))                // "iat" = date d'émission = maintenant
                .expiration(new Date(System.currentTimeMillis() + expiration)) // "exp" = date d'expiration = maintenant + durée configurée
                .signWith(getSigningKey())                                     // Signe le token avec HMAC-SHA (algorithme déduit de la taille de la clé)
                .compact();                                                    // Sérialise en String : "xxxxx.yyyyy.zzzzz" (header.payload.signature)
    }

    // Extrait le username (claim "sub") depuis un token — sans vérifier la validité complète
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Configure le parser pour vérifier la signature avec notre clé
                .build()                    // Construit le parser
                .parseSignedClaims(token)   // Parse le token ET vérifie la signature — lève une exception si invalide ou expiré
                .getPayload()               // Récupère le corps du token (les claims)
                .getSubject();              // Retourne le claim "sub", soit le username
    }

    // Vérifie qu'un token est valide pour un utilisateur donné
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);                        // Extrait le username du token
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token); // Valide si : username correspond ET token non expiré
    }

    // Vérifie si la date d'expiration du token est passée
    private boolean isTokenExpired(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Même setup que extractUsername
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()            // Récupère le claim "exp"
                .before(new Date());        // Retourne true si "exp" est avant maintenant → token expiré
    }

    // Reconstruit la clé cryptographique à partir de la clé secrète Base64 stockée en config
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Décode la String Base64 en tableau d'octets bruts
        return Keys.hmacShaKeyFor(keyBytes);                 // Crée une clé HMAC-SHA (HS256/384/512 selon la longueur)
    }

}