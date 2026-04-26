package com.gudnuz.api.service;

import com.gudnuz.api.dto.request.CreatePostRequest;
import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.enums.Categorie;
import com.gudnuz.api.repository.PostRepository;
import com.gudnuz.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public PostResponse createPost(String titre, String contenu, String categorie, String source, MultipartFile image, String email) throws IOException, IOException {
        User user = userRepository.findByEmail(email).orElseThrow();
        Categorie cat = Categorie.valueOf(categorie.toUpperCase());

        if (cat == Categorie.ACTU && (source == null || source.isBlank())) {
            throw new RuntimeException("La source est obligatoire pour les posts de catégorie ACTU");
        }

        // Si une image est envoyée, on la sauvegarde et on récupère le nom du fichier
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            String fileName = fileStorageService.storeFile(image);
            imageUrl = "/uploads/" + fileName;
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

        return mapToResponse(post);
    }


    public List<PostResponse> getFeed (String categorie) {

        LocalDateTime since = LocalDateTime.now().minusHours(24);

        if (categorie == null){
            List<Post> posts = postRepository.findByCreatedAtAfterOrderByCreatedAtDesc(since);
            return posts.stream()
                    .map(this::mapToResponse)
                    .toList();

        }

        Categorie cat = Categorie.valueOf(categorie.toUpperCase());
        List<Post> posts = postRepository.findByCreatedAtAfterAndCategorieOrderByCreatedAtDesc(since, cat);
        return posts.stream()
                .map(this::mapToResponse)
                .toList();









    }

    public List<PostResponse> getPostsByUser (UUID userId) {

        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return posts.stream()
                .map(this::mapToResponse)
                .toList();

    }


    public List<PostResponse> getMyPosts(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return getPostsByUser(user.getId());
    }




    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .titre(post.getTitre())
                .contenu(post.getContenu())
                .source(post.getSource())
                .imageUrl(post.getImageUrl())
                .categorie(post.getCategorie().name())
                .sparksCount(post.getSparksCount())
                .pseudo(post.getUser().getPseudo())
                .createdAt(post.getCreatedAt())
                .build();
    }




}
