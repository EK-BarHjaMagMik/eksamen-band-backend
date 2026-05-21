package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.entity.Show;
import org.example.eksamenbandbackend.repository.ShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Loads the full application context — tests the whole stack from HTTP to database
@SpringBootTest
@ActiveProfiles("test") // activates application-test.properties, which swaps MySQL for H2 in-memory
public class AdminShowIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ShowRepository showRepository;

    @BeforeEach
    void setUp() {
        // apply(springSecurity()) is required for the Spring Security filter chain
        // to actually run — webAppContextSetup alone does not include it
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        showRepository.deleteAll(); // reset DB state between tests to avoid interference
    }

    @Test
    void shouldPersistShowInDatabase() throws Exception {
        mockMvc.perform(post("/api/admin/shows")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2026-06-01\",\"city\":\"Køge\",\"venue\":\"Tapperiet\",\"ticketLink\":\"https://www.tapperiet.nu\"}"))
                .andExpect(status().is2xxSuccessful());

        List<Show> shows = showRepository.findAll();
        assertThat(shows).hasSize(1);
        assertThat(shows.get(0).getCity()).isEqualTo("Køge");
        assertThat(shows.get(0).getVenue()).isEqualTo("Tapperiet");
        assertThat(shows.get(0).getTicketLink()).isEqualTo("https://www.tapperiet.nu");
    }

    @Test
    void shouldRejectUnauthenticatedRequestWithForbidden() throws Exception {
        mockMvc.perform(post("/api/admin/shows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2026-06-01\",\"city\":\"Køge\",\"venue\":\"Tapperiet\",\"ticketLink\":\"https://www.tapperiet.nu\"}"))
                .andExpect(status().isForbidden());

        assertThat(showRepository.findAll()).isEmpty();
    }
}