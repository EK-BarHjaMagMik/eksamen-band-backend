package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.entity.Show;
import org.example.eksamenbandbackend.repository.ShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class ShowControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ShowRepository showRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        showRepository.deleteAll();

        Show upcoming = new Show();
        upcoming.setDate(LocalDate.now().plusDays(10));
        upcoming.setCity("Køge");
        upcoming.setVenue("Tapperiet");
        upcoming.setTicketLink("https://www.tapperiet.nu");

        showRepository.save(upcoming);
    }

    @Test
    void shouldReturnUpcomingShowsWithStatus200() throws Exception {
        mockMvc.perform(get("/api/shows/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Køge"))
                .andExpect(jsonPath("$[0].venue").value("Tapperiet"))
                .andExpect(jsonPath("$[0].ticketLink").value("https://www.tapperiet.nu"));
    }

    @Test
    void shouldReturnEmptyListWhenNoUpcomingShows() throws Exception {
        showRepository.deleteAll();

        mockMvc.perform(get("/api/shows/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnShowByIdWithStatus200() throws Exception {
        Show show = new Show();
        show.setDate(LocalDate.now().plusDays(5));
        show.setCity("Roskilde");
        show.setVenue("Pumpehuset");
        show.setTicketLink("https://example.com/tickets");

        Show saved = showRepository.save(show);

        mockMvc.perform(get("/api/shows/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Roskilde"))
                .andExpect(jsonPath("$.venue").value("Pumpehuset"))
                .andExpect(jsonPath("$.ticketLink").value("https://example.com/tickets"));
    }

    @Test
    void shouldReturn404WhenShowNotFound() throws Exception {
        showRepository.deleteAll();

        mockMvc.perform(get("/api/shows/{id}", 9999L))
                .andExpect(status().isNotFound());
    }
}
