package com.gudnuz.api.service;

import com.gudnuz.api.model.Post;
import com.gudnuz.api.repository.PostRepository;
import com.gudnuz.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.gudnuz.api.model.enums.PostStatut.ELECTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FlareServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private FlareService flareService;


    @Test
    void electFlare_elitLePremierPostDeLaListe() {
        // Arrange
        Post postActu = Post.builder()
                .id(UUID.randomUUID())
                .titre("titre Actu")
                .sparksCount(12)
                .contenu("contenu Actu")
                .build();

        Post postHumour = Post.builder()
                .id(UUID.randomUUID())
                .titre("titre Humour")
                .sparksCount(5)
                .contenu("contenu Humour")
                .build();

        Post postCulture = Post.builder()
                .id(UUID.randomUUID())
                .titre("titre Culture")
                .sparksCount(3)
                .contenu("contenu Culture")
                .build();


        when(postRepository.findNextEligiblePost(any(LocalDateTime.class)))
                .thenReturn(List.of(postActu, postHumour, postCulture));

        // Act
        flareService.electFlare();

        // Assert
        assertThat(postActu.getStatut()).isEqualTo(ELECTED);
        assertThat(postActu.getElectedAt()).isNotNull();
        assertThat(postHumour.getStatut()).isNull();
        assertThat(postCulture.getStatut()).isNull();

        verify(postRepository).save(postActu);
        verify(notificationService).notifyNewFlare(postActu);
    }

    @Test
    void electFlare_neFaitRienSiAucunPostEligible() {
        // Arrange : rien à stubber, un mock renvoie une liste vide par défaut

        // Act
        flareService.electFlare();

        // Assert
        verify(postRepository, never()).save(any());
        verify(notificationService, never()).notifyNewFlare(any());
    }
}
