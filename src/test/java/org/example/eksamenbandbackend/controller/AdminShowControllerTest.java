package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.dto.CreateShowRequest;
import org.example.eksamenbandbackend.dto.ShowResponse;
import org.example.eksamenbandbackend.entity.Show;
import org.example.eksamenbandbackend.repository.ShowRepository;
import org.example.eksamenbandbackend.service.ShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class AdminShowControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowService showService;

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
    void shouldCreateShow() throws Exception{
        /*when(showService.createShow(any(CreateShowRequest.class))).thenReturn();

        mockMvc.perform(post("/api/admin/shows")
                        .param("id", "2")
                        .param("date", "2026-05-20")
                        .param("city", "Køge")
                        .param("venue", "Tapperiet")
                        .param("ticketLink", "https://www.tapperiet.nu"))
                .andExpect(status().is2xxSuccessful());

        ArgumentCaptor<CreateShowRequest> createShowRequestArgumentCaptor = ArgumentCaptor.forClass(CreateShowRequest.class);

        verify(showService).createShow(createShowRequestArgumentCaptor.capture());

        CreateShowRequest captured =createShowRequestArgumentCaptor.getValue();
        assertThat(captured.date()).isEqualTo("2026-05-20");
        assertThat(captured.city()).isEqualTo("Køge");
        assertThat(captured.venue()).isEqualTo("Tapperiet");
        assertThat(captured.ticketLink()).isEqualTo("https://www.tapperiet.nu");*/ //TODO: fix test
    }
}
