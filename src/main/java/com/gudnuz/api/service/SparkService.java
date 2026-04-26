package com.gudnuz.api.service;

import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.UserPostSpark;
import com.gudnuz.api.repository.PostRepository;
import com.gudnuz.api.repository.UserPostSparkRepository;
import com.gudnuz.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SparkService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserPostSparkRepository userPostSparkRepository;


    @Transactional
    public PostResponse toggleSpark(UUID postId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        if (userPostSparkRepository.existsByUserAndPost(user, post)){
            userPostSparkRepository.deleteByUserAndPost(user, post);
            post.setSparksCount(post.getSparksCount() - 1);

        }else {

            UserPostSpark userPostSpark = UserPostSpark.builder()
                    .user(user)
                    .post(post)
                    .build();

        userPostSparkRepository.save(userPostSpark);
        post.setSparksCount(post.getSparksCount() + 1);
        }

        postRepository.save(post);

        // On renvoie le post mis à jour
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

