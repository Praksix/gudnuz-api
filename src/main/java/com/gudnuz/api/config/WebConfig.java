package com.gudnuz.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Même propriété que FileStorageService (app.upload-dir / UPLOAD_DIR)
    // pour être sûr de toujours servir le dossier réellement utilisé pour l'écriture
    @Value("${app.upload-dir}")
    private String uploadDirPath;

    /**
     * Configure Spring pour servir les fichiers du dossier d'upload
     * quand une requête arrive sur /uploads/**
     *
     * Exemple : GET /uploads/a3f2b1c4.jpg → Spring renvoie le fichier <uploadDir>/a3f2b1c4.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDirPath + "/");
    }
}