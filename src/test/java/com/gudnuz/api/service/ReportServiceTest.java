package com.gudnuz.api.service;

import com.gudnuz.api.dto.request.ReportRequest;
import com.gudnuz.api.exception.DuplicateResourceException;
import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.PostReport;
import com.gudnuz.api.model.User;
import com.gudnuz.api.repository.PostReportRepository;
import com.gudnuz.api.repository.PostRepository;
import com.gudnuz.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostReportRepository postReportRepository;


    @InjectMocks
    private ReportService reportService;


    @Test
    void reportShouldFail() {
        Post post = Post.builder().build();
        User user = User.builder().build();
        ReportRequest request = ReportRequest.builder().build();

        PostReport postReport = PostReport.builder()
                .user(user)
                .post(post)
                .motif(request.getMotif())
                .build();

        UUID postId = UUID.randomUUID();


        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postReportRepository.existsByUserAndPost(user, post)).thenReturn(true);

        //Assert
        assertThrows(DuplicateResourceException.class, () -> {
            reportService.reportPost(postId, request, user.getEmail());
        });
    }

}
