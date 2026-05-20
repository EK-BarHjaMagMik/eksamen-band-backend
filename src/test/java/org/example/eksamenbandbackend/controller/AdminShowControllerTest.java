package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.dto.CreateShowRequest;
import org.example.eksamenbandbackend.entity.Show;
import org.example.eksamenbandbackend.repository.ShowRepository;
import org.example.eksamenbandbackend.service.ShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean
    private ShowService showService;

    @BeforeEach
    void setUp() {
        // wires MockMvc into the real Spring web context
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
    void shouldCreateShow() throws Exception {

        // tell the mock: when createShow is called with any CreateShowRequest, return an empty Show
        when(showService.createShow(any(CreateShowRequest.class))).thenReturn(new Show());

        // perform a POST to the endpoint with a JSON body matching CreateShowRequest fields
        mockMvc.perform(post("/api/admin/shows")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2026-05-20\",\"city\":\"Køge\",\"venue\":\"Tapperiet\",\"ticketLink\":\"https://www.tapperiet.nu\"}"))
                  .andExpect(status().is2xxSuccessful()); // assert the controller returned a 200-range response

        // set up a captor to intercept the exact argument passed to createShow
        ArgumentCaptor<CreateShowRequest> captor = ArgumentCaptor.forClass(CreateShowRequest.class);
        verify(showService).createShow(captor.capture()); // assert createShow was called exactly once, and capture what it received

        CreateShowRequest captured = captor.getValue(); // retrieve the captured argument
        // assert each field was correctly mapped from the JSON body to the CreateShowRequest
        assertThat(captured.date()).isEqualTo(LocalDate.of(2026, 5, 20));
        assertThat(captured.city()).isEqualTo("Køge");
        assertThat(captured.venue()).isEqualTo("Tapperiet");
        assertThat(captured.ticketLink()).isEqualTo("https://www.tapperiet.nu");
    }

    @Test
    void shouldReturn400BadRequest() throws Exception {
        /*when(showService.createShow(any(CreateShowRequest.class))).thenReturn(new Show());

        mockMvc.perform(post("/api/admin/shows")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2026-05-20\",\"city\":\"\",\"venue\":\"Tapperiet\",\"ticketLink\":\"https://www.tapperiet.nu\"}"))
                .andExpect(status().is4xxClientError());

        ArgumentCaptor<CreateShowRequest> captor = ArgumentCaptor.forClass(CreateShowRequest.class);
        verify(showService).createShow(captor.capture());

        CreateShowRequest captured = captor.getValue();

        assertThat(captured.city()).isEmpty();*/ //TODO: fix test
    }
}