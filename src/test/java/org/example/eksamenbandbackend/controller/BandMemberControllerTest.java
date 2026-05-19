package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.entity.BandMember;
import org.example.eksamenbandbackend.repository.BandMemberRepository;
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
class BandMemberControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BandMemberRepository bandMemberRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        bandMemberRepository.deleteAll();

        // Saved out of displayOrder on purpose so the ordering test is meaningful.
        BandMember second = new BandMember();
        second.setName("Mads");
        second.setRole("Guitar");
        second.setBio("Guitarist.");
        second.setDisplayOrder(2);

        BandMember first = new BandMember();
        first.setName("Kinnie");
        first.setRole("Vocals");
        first.setBio("Lead vocalist.");
        first.setDisplayOrder(1);

        bandMemberRepository.save(second);
        bandMemberRepository.save(first);
    }

    @Test
    void shouldReturnBandMembersWithStatus200() throws Exception {
        mockMvc.perform(get("/api/band-members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Kinnie"))
                .andExpect(jsonPath("$[0].role").value("Vocals"))
                .andExpect(jsonPath("$[0].bio").value("Lead vocalist."));
    }

    @Test
    void shouldReturnBandMembersSortedByDisplayOrderAsc() throws Exception {
        // Inserted as Mads(2) then Kinnie(1); response must be ordered by displayOrder.
        mockMvc.perform(get("/api/band-members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Kinnie"))
                .andExpect(jsonPath("$[1].name").value("Mads"));
    }

    @Test
    void shouldReturnEmptyListWhenNoBandMembers() throws Exception {
        bandMemberRepository.deleteAll();

        mockMvc.perform(get("/api/band-members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
