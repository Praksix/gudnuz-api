package com.gudnuz.api.controller;

import com.gudnuz.api.config.SecurityConfig;
import com.gudnuz.api.dto.response.PostResponse;
import com.gudnuz.api.dto.response.ReportResponse;
import com.gudnuz.api.repository.UserRepository;
import com.gudnuz.api.security.JwtAuthenticationFilter;
import com.gudnuz.api.security.JwtService;
import com.gudnuz.api.service.ModerationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ModerationController.class)
// On importe le VRAI JwtAuthenticationFilter (pas un mock) : sans header Authorization
// dans nos requêtes MockMvc, son code fait juste filterChain.doFilter(...) et laisse
// passer, ce qui est inoffensif. Le mocker entièrement empêchait cet appel et bloquait
// toute la chaîne de filtres avant qu'elle n'atteigne DispatcherServlet.
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
public class ModerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModerationService moderationService;

    // Nécessaires uniquement parce qu'on importe la vraie SecurityConfig et le vrai
    // JwtAuthenticationFilter (pour que les @PreAuthorize soient réellement évalués) :
    // ils ont besoin de ces beans pour se construire, mais on ne veut pas d'une vraie
    // base ni d'un vrai JWT ici — jamais réellement appelés dans ces tests.
    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllReports_ok() throws Exception {
        ReportResponse report = ReportResponse.builder()
                .id(UUID.randomUUID())
                .motif("Contenu inapproprié")
                .signalePar("Bob")
                .build();

        when(moderationService.getAllReports(anyString())).thenReturn(List.of(report));

        mockMvc.perform(get("/api/moderation/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].motif").value("Contenu inapproprié"))
                .andExpect(jsonPath("$[0].signalePar").value("Bob"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllReports_refuseSimpleUser() throws Exception {

        mockMvc.perform(get("/api/moderation/reports"))
                .andExpect(status().is(403));

    }

    @Test
    void getAllReports_refuseNoUser() throws Exception {

        mockMvc.perform(get("/api/moderation/reports"))
                .andExpect(status().is(401));

    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void removePost_ok() throws Exception {
        PostResponse post = PostResponse.builder()
                .id(UUID.randomUUID())
                .build();

        mockMvc.perform(patch("/api/moderation/posts/"+post.getId()+"/remove"))
                .andExpect(status().is(200));

        verify(moderationService).removePost(post.getId());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ignoreReport_ok() throws Exception {

        ReportResponse report = ReportResponse.builder()
                .id(UUID.randomUUID())
                .build();

        mockMvc.perform(delete("/api/moderation/reports/"+report.getId()))
                .andExpect(status().is(200));

        verify(moderationService).ignoreReport(report.getId());
    }
}