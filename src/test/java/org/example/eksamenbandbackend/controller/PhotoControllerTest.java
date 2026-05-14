package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.entity.Photo;
import org.example.eksamenbandbackend.repository.PhotoRepository;
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
class PhotoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PhotoRepository photoRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        photoRepository.deleteAll();

        Photo older = new Photo();
        older.setUrl("/sample-photos/photo1.jpg");
        older.setCaption("Older photo");
        older.setDateTaken(LocalDate.of(2025, 1, 1));

        Photo newer = new Photo();
        newer.setUrl("/sample-photos/photo2.jpg");
        newer.setCaption("Newer photo");
        newer.setDateTaken(LocalDate.of(2026, 1, 1));

        photoRepository.save(older);
        photoRepository.save(newer);
    }

    @Test
    void shouldReturnPhotosWithStatus200() throws Exception {
        mockMvc.perform(get("/api/photos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].caption").value("Newer photo"))
                .andExpect(jsonPath("$[1].caption").value("Older photo"));
    }

    @Test
    void shouldReturnEmptyListWhenNoPhotos() throws Exception {
        photoRepository.deleteAll();

        mockMvc.perform(get("/api/photos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
