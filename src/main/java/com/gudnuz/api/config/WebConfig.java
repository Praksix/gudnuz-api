package com.gudnuz.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure Spring pour servir les fichiers du dossier "uploads/"
     * quand une requête arrive sur /uploads/**
     *
     * Exemple : GET /uploads/a3f2b1c4.jpg → Spring renvoie le fichier uploads/a3f2b1c4.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}