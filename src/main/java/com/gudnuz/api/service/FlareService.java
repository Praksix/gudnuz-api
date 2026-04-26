package com.gudnuz.api.service;

import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.enums.PostStatut;
import com.gudnuz.api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlareService {

    private final PostRepository postRepository;

    public void electFlare(){
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<Post> posts = postRepository.findTopPostsSince(since);
        if (posts.isEmpty()){
            return;
        }else {
            Post flare = posts.get(0);
            flare.setStatut(PostStatut.ELECTED);
            postRepository.save(flare);
        }
    }

    public List<PostResponse> getGudBox() {
        List<Post> posts = postRepository.findByStatutOrderByCreatedAtDesc(PostStatut.ELECTED);

        return posts.stream()
                .map(post -> PostResponse.builder()
                        .id(post.getId())
                        .titre(post.getTitre())
                        .contenu(post.getContenu())
                        .source(post.getSource())
                        .imageUrl(post.getImageUrl())
                        .categorie(post.getCategorie().name())
                        .sparksCount(post.getSparksCount())
                        .pseudo(post.getUser().getPseudo())
                        .createdAt(post.getCreatedAt())
                        .build())
                .toList();
    }

    public PostResponse getCurrentFlare() {
        List<PostResponse> gudBox = getGudBox();

        if (gudBox.isEmpty()) {
            return null;
        }

        return gudBox.get(0);
    }


}
