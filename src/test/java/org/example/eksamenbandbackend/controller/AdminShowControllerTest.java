package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.controller.admin.AdminShowController;
import org.example.eksamenbandbackend.dto.CreateShowRequest;
import org.example.eksamenbandbackend.entity.Show;
import org.example.eksamenbandbackend.security.JwtUtil;
import org.example.eksamenbandbackend.service.ShowService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Loads only the web layer (controller + security), not the full application context
@WebMvcTest(AdminShowController.class)
public class AdminShowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShowService showService;

    // SecurityConfig wires JwtFilter which depends on these two — must be mocked
    // even though the test doesn't use them, otherwise the context fails to start
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN") // endpoint requires ROLE_ADMIN; without this the request returns 403
    void shouldCreateShow() throws Exception {
        when(showService.createShow(any(CreateShowRequest.class))).thenReturn(new Show());

        mockMvc.perform(post("/api/admin/shows")
                        .contentType(MediaType.APPLICATION_JSON)

                        .content("{\"date\":\"2026-05-20\",\"city\":\"Køge\",\"venue\":\"Tapperiet\",\"ticketLink\":\"https://www.tapperiet.nu\"}"))
                .andExpect(status().is2xxSuccessful());

        // capture the exact argument passed to the service to assert field-level mapping
        ArgumentCaptor<CreateShowRequest> captor = ArgumentCaptor.forClass(CreateShowRequest.class);
        verify(showService).createShow(captor.capture());

        CreateShowRequest captured = captor.getValue();
        assertThat(captured.date()).isEqualTo(LocalDate.of(2026, 5, 20));
        assertThat(captured.city()).isEqualTo("Køge");
        assertThat(captured.venue()).isEqualTo("Tapperiet");
        assertThat(captured.ticketLink()).isEqualTo("https://www.tapperiet.nu");
    }
}