package com.gudnuz.api.service;


import com.gudnuz.api.exception.ResourceNotFoundException;
import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.PostReport;
import com.gudnuz.api.repository.PostReportRepository;
import com.gudnuz.api.repository.PostRepository;
import static com.gudnuz.api.model.enums.PostStatut.REMOVED;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ModerationServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostReportRepository postReportRepository;

    @InjectMocks
    private ModerationService moderationService;


    @Test
    void removePostSuccessTest() {
        Post post = Post.builder().build();
        UUID postId = UUID.randomUUID();
        post.setStatut(null);



        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        moderationService.removePost(postId);


        assertThat(post.getStatut()).isEqualTo(REMOVED);
        verify(postReportRepository).deleteByPost(post);

    }



    @Test
    void removePostShouldFail() {


        UUID wrongPostId = UUID.randomUUID();


        //Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            moderationService.removePost(wrongPostId);
        });
    }


    @Test
    void ignoreReportTest() {

        PostReport report = PostReport.builder().build();
        UUID reportId = UUID.randomUUID();

        when(postReportRepository.findById(reportId)).thenReturn(Optional.of(report));

        moderationService.ignoreReport(reportId);


        verify(postReportRepository).delete(report);
    }



}
