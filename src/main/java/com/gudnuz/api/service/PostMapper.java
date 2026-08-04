package com.gudnuz.api.service;

import com.gudnuz.api.repository.UserPostSparkRepository;
import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.User;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Component
public class PostMapper {


    private final UserPostSparkRepository userPostSparkRepository;

    public PostResponse mapToResponse(Post post, User user) {
        boolean isSparked = userPostSparkRepository.existsByUserAndPost(user, post);
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUser().getId().toString())
                .titre(post.getTitre())
                .contenu(post.getContenu())
                .source(post.getSource())
                .imageUrl(post.getImageUrl())
                .categorie(post.getCategorie().name())
                .sparksCount(post.getSparksCount())
                .pseudo(post.getUser().getPseudo())
                .avatarUrl(post.getUser().getAvatarUrl())
                .createdAt(post.getCreatedAt())
                .isSparked(isSparked)
                .build();
    }
}
