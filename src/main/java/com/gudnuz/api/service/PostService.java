package com.gudnuz.api.service;

import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.enums.Categorie;
import com.gudnuz.api.exception.BusinessException;
import com.gudnuz.api.exception.ResourceNotFoundException;
import com.gudnuz.api.model.enums.PostStatut;
import com.gudnuz.api.repository.PostRepository;
import com.gudnuz.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.gudnuz.api.model.enums.PostStatut.REMOVED;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final OgImageService ogImageService;
    private final PostMapper postMapper;


    public PostResponse createPost(String titre, String contenu, String categorie, String source, MultipartFile image, String email) throws IOException {
        User user = userRepository.findByEmail(email).orElseThrow();
        Categorie cat = Categorie.valueOf(categorie.toUpperCase());

        if (cat == Categorie.ACTU && (source == null || source.isBlank())) {
            throw new BusinessException("La source est obligatoire pour les posts de catégorie ACTU");
        }

        // Si une image est envoyée, on la sauvegarde et on récupère le nom du fichier
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            String fileName = fileStorageService.storeFile(image);
            imageUrl = "/uploads/" + fileName;
        }
        else if (cat == Categorie.ACTU && (image == null || image.isEmpty()) && source != null) {

            imageUrl = ogImageService.extractImageUrl(source);
        }

        Post post = Post.builder()
                .titre(titre)
                .contenu(contenu)
                .source(source)
                .categorie(cat)
                .imageUrl(imageUrl)
                .user(user)
                .build();

        postRepository.save(post);

        return postMapper.mapToResponse(post, user);
    }


    public List<PostResponse> getFeed(String categorie, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        LocalDateTime since = LocalDateTime.now().minusHours(24);

        List<Post> posts = (categorie == null || "TOP".equals(categorie))
                ? postRepository.findTopPostsSince(since)
                : postRepository.findByCreatedAtAfterAndCategorieAndStatutNotOrderByCreatedAtDesc(since, Categorie.valueOf(categorie.toUpperCase()), PostStatut.REMOVED);

        return posts.stream()
                .map(post -> postMapper.mapToResponse(post, user))
                .toList();
    }


    public List<PostResponse> getPostsByUser(UUID userId, String viewerEmail) {
        User viewer = userRepository.findByEmail(viewerEmail).orElseThrow();
        List<Post> posts = postRepository.findByUserIdAndStatutNotOrderByCreatedAtDesc(userId, PostStatut.REMOVED);
        return posts.stream()
                .map(post -> postMapper.mapToResponse(post, viewer))
                .toList();
    }


    public List<PostResponse> getMyPosts(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return getPostsByUser(user.getId(), email);
    }


    // Utilisé à l'ouverture d'une notification Flare (l'app ne reçoit que le postId)
    public PostResponse getPostById(UUID postId, String email) {
        User viewer = userRepository.findByEmail(email).orElseThrow();
        Post post = postRepository.findById(postId)
                .filter(p -> p.getStatut() != REMOVED)
                .orElseThrow(() -> new ResourceNotFoundException("Post introuvable"));
        return postMapper.mapToResponse(post, viewer);
    }











}
