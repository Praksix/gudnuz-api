package com.gudnuz.api.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.gudnuz.api.model.Post;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class NotificationService {

    // Topic FCM auquel chaque app s'abonne : un seul envoi touche tous les appareils,
    // pas besoin de stocker les tokens FCM en base.
    public static final String FLARE_TOPIC = "flares";

    // Chemin du JSON du compte de service Firebase (secret, jamais commité)
    @Value("${app.firebase.credentials-path:}")
    private String credentialsPath;

    private FirebaseMessaging messaging;

    @PostConstruct
    void init() {
        // Sans credentials (dev local, tests, CI) : notifications désactivées, l'API démarre quand même
        if (credentialsPath.isBlank()) {
            log.warn("Firebase non configuré : notifications push désactivées");
            return;
        }
        try (InputStream credentials = new FileInputStream(credentialsPath)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentials))
                    .build();
            FirebaseApp app = FirebaseApp.getApps().isEmpty()
                    ? FirebaseApp.initializeApp(options)
                    : FirebaseApp.getInstance();
            messaging = FirebaseMessaging.getInstance(app);
        } catch (IOException e) {
            log.error("Impossible de lire les credentials Firebase : {}", e.getMessage());
        }
    }

    public void notifyNewFlare(Post flare) {
        if (messaging == null) {
            return;
        }
        Message message = Message.builder()
                .setTopic(FLARE_TOPIC)
                .setNotification(Notification.builder()
                        .setTitle("🔥 Nouveau Flare !")
                        .setBody(flare.getTitre())
                        .build())
                // Donnée lue par l'app au clic pour ouvrir le bon post
                .putData("postId", flare.getId().toString())
                .build();
        try {
            messaging.send(message);
        } catch (FirebaseMessagingException e) {
            // Un échec d'envoi ne doit pas annuler l'élection
            log.error("Échec de l'envoi de la notification Flare : {}", e.getMessage());
        }
    }
}
