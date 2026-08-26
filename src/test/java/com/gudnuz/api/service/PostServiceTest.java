package com.gudnuz.api.service;

import com.gudnuz.api.exception.BusinessException;
import com.gudnuz.api.model.enums.Categorie;
import com.gudnuz.api.model.enums.PostStatut;
import com.gudnuz.api.repository.PostRepository;
import com.gudnuz.api.model.User;
import static org.mockito.ArgumentMatchers.eq;
import com.gudnuz.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private UserRepository userRepository;


    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;


    @Mock
    private PostMapper postMapper;

    @Mock
    private OgImageService ogImageService;


    @Test
    void createPostSourceFail() {

        User user = User.builder().build();
        String titre = "Titre";
        String contenu = "contenu";
        String categorie = "ACTU";
        String source = null;
        MultipartFile image = null;
        String email = "nathan@gudnuz.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));


        //Assert
        assertThrows(BusinessException.class, () -> {
            postService.createPost(titre, contenu,categorie, source, image, email);
        });
    }


    @Test
    void getTopFeedTest() {
        User user = User.builder().build();
        String email = "nathan@gudnuz.com";
        String categorie =  null;

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        postService.getFeed(categorie, email);

        verify(postRepository).findTopPostsSince(any(LocalDateTime.class));

    }

    @Test
    void getActuFeedTest() {
        User user = User.builder().build();
        String email = "nathan@gudnuz.com";
        String categorie =  "ACTU";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        postService.getFeed(categorie, email);

        verify(postRepository).findByCreatedAtAfterAndCategorieAndStatutNotOrderByCreatedAtDesc(
                any(LocalDateTime.class),
                eq(Categorie.ACTU),
                eq(PostStatut.REMOVED)
        );

    }
}
