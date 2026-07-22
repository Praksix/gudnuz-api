package com.gudnuz.api.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    // Chemin injecté depuis application.yml (app.upload-dir), lui-même surchargeable
    // par la variable d'env UPLOAD_DIR (ex: /app/uploads en Docker)
    @Value("${app.upload-dir}")
    private String uploadDirPath;

    private Path uploadDir;

    /**
     * Exécuté au démarrage de l'appli, une fois uploadDirPath injecté.
     * Crée le dossier configuré s'il n'existe pas encore.
     */
    @PostConstruct
    public void init() throws IOException {
        uploadDir = Paths.get(uploadDirPath);
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