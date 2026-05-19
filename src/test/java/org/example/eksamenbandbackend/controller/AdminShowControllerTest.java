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

@SpringBootTest
@ActiveProfiles("test")
public class AdminShowControllerTest {

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
    void shouldCreateShow() throws Exception{

    }
}
