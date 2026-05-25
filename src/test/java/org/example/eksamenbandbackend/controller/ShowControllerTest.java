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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

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
    void getUpcomingShows_returnsUpcomingShows_status200() throws Exception {
        mockMvc.perform(get("/api/shows/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Køge"))
                .andExpect(jsonPath("$[0].venue").value("Tapperiet"))
                .andExpect(jsonPath("$[0].ticketLink").value("https://www.tapperiet.nu"));
    }

    @Test
    void getUpcomingShows_returnsEmptyList_whenNoUpcomingShows() throws Exception {
        showRepository.deleteAll();

        mockMvc.perform(get("/api/shows/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getShowById_returnsShow_status200() throws Exception {
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
    void getShowById_returns404_whenNotFound() throws Exception {
        showRepository.deleteAll();

        mockMvc.perform(get("/api/shows/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getShows_returnsAllShows_status200() throws Exception {
        showRepository.deleteAll();

        Show show1 = new Show();
        show1.setDate(LocalDate.now().plusDays(5));
        show1.setCity("Roskilde");
        show1.setVenue("Pumpehuset");
        show1.setTicketLink("https://example.com/tickets");

        Show show2 = new Show();
        show2.setDate(LocalDate.now().minusDays(10));
        show2.setCity("Køge");
        show2.setVenue("Tapperiet");
        show2.setTicketLink("https://www.tapperiet.nu");

        showRepository.save(show1);
        showRepository.save(show2);

        mockMvc.perform(get("/api/shows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].city", containsInAnyOrder("Roskilde", "Køge")));
    }
}
