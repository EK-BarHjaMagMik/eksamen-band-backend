package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.entity.ContactInfo;
import org.example.eksamenbandbackend.repository.ContactInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class ContactInfoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ContactInfoRepository contactInfoRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        contactInfoRepository.deleteAll();

        ContactInfo contact = new ContactInfo();
        contact.setEmail("stuggofficial@gmail.com");
        contact.setBookingNote("For bookings, contact us.");

        contactInfoRepository.save(contact);
    }

    @Test
    void shouldReturnContactInfoWithStatus200() throws Exception {
        mockMvc.perform(get("/api/contact"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("stuggofficial@gmail.com"))
                .andExpect(jsonPath("$.bookingNote").value("For bookings, contact us."));
    }

}
