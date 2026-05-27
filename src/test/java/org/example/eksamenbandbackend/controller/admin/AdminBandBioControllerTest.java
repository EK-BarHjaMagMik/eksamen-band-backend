package org.example.eksamenbandbackend.controller.admin;

import org.example.eksamenbandbackend.dto.BandBioResponse;
import org.example.eksamenbandbackend.dto.UpdateBandBioRequest;
import org.example.eksamenbandbackend.security.JwtUtil;
import org.example.eksamenbandbackend.service.BandBioService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBandBioController.class)
class AdminBandBioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BandBioService bandBioService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateBandBio() throws Exception {
        BandBioResponse response = new BandBioResponse(1L, "Updated bio text.\n\nSecond paragraph.");
        when(bandBioService.update(any(UpdateBandBioRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/admin/band-bio")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "content": "Updated bio text.\\n\\nSecond paragraph."
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Updated bio text.\n\nSecond paragraph."));

        ArgumentCaptor<UpdateBandBioRequest> captor = ArgumentCaptor.forClass(UpdateBandBioRequest.class);
        verify(bandBioService).update(captor.capture());
        assertThat(captor.getValue().content()).isEqualTo("Updated bio text.\n\nSecond paragraph.");
    }
}
