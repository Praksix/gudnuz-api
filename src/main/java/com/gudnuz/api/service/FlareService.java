package com.gudnuz.api.service;

import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.User;
import com.gudnuz.api.model.enums.PostStatut;
import com.gudnuz.api.repository.PostRepository;
import com.gudnuz.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlareService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final NotificationService notificationService;

    public void electFlare(){
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<Post> posts = postRepository.findNextEligiblePost(since);
        if (posts.isEmpty()){
            return;
        }else {
            Post flare = posts.get(0);
            flare.setStatut(PostStatut.ELECTED);
            flare.setElectedAt(LocalDateTime.now());
            postRepository.save(flare);
            notificationService.notifyNewFlare(flare);
        }
    }

    public List<PostResponse> getGudBox(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Post> posts = postRepository.findByStatutOrderByElectedAtDesc(PostStatut.ELECTED);

        return posts.stream()
                .map(post -> postMapper.mapToResponse(post, user))
                .toList();
    }

    public PostResponse getCurrentFlare(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return postRepository.findFirstByStatutOrderByElectedAtDesc(PostStatut.ELECTED)
                .map(post -> postMapper.mapToResponse(post, user))
                .orElse(null);
    }

}
