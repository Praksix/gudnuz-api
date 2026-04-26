package com.gudnuz.api.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    // Le dossier où les images seront sauvegardées
    private final Path uploadDir = Paths.get("uploads");

    /**
     * Exécuté au démarrage de l'appli.
     * Crée le dossier "uploads" s'il n'existe pas encore.
     */
    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadDir);
    }

    /**
     * Sauvegarde un fichier sur le disque avec un nom unique.
     *
     * @param file le fichier envoyé par le client (image)
     * @return le nom unique du fichier sauvegardé (ex: "a3f2b1c4.jpg")
     */
    public String storeFile(MultipartFile file) throws IOException {
        // Récupère le nom original (ex: "photo.jpg")
        String originalFilename = file.getOriginalFilename();

        // Extrait l'extension (ex: ".jpg")
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // Génère un nom unique pour éviter les collisions
        String fileName = UUID.randomUUID() + extension;

        // Construit le chemin complet (ex: "uploads/a3f2b1c4.jpg")
        Path filePath = uploadDir.resolve(fileName);

        // Copie le contenu du fichier vers le disque
        Files.copy(file.getInputStream(), filePath);

        // Renvoie le nom du fichier (pas le chemin complet)
        return fileName;
    }
}