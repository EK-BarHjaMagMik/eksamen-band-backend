package org.example.eksamenbandbackend.controller.admin;

import org.example.eksamenbandbackend.dto.BandMemberResponse;
import org.example.eksamenbandbackend.dto.UpdateBandMemberRequest;
import org.example.eksamenbandbackend.security.JwtUtil;
import org.example.eksamenbandbackend.service.BandMemberService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBandMemberController.class)
class AdminBandMemberControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private BandMemberService bandMemberService;

        @MockitoBean
        private JwtUtil jwtUtil;

        @MockitoBean
        private UserDetailsService userDetailsService;

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldUpdateBandMemberTextFields() throws Exception {
                BandMemberResponse response = new BandMemberResponse(
                                1L, "Kasper", "Vocals", "Updated bio.", "/uploads/members/abc.jpg");
                when(bandMemberService.editBandMemberById(eq(1L), any(UpdateBandMemberRequest.class)))
                                .thenReturn(response);

                mockMvc.perform(put("/api/admin/band-members/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                        "name": "Kasper",
                                                        "role": "Vocals",
                                                        "bio": "Updated bio."
                                                }
                                                """))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Kasper"))
                                .andExpect(jsonPath("$.role").value("Vocals"))
                                .andExpect(jsonPath("$.bio").value("Updated bio."))
                                .andExpect(jsonPath("$.photoUrl").value("/uploads/members/abc.jpg"));

                ArgumentCaptor<UpdateBandMemberRequest> captor = ArgumentCaptor.forClass(UpdateBandMemberRequest.class);
                verify(bandMemberService).editBandMemberById(eq(1L), captor.capture());

                UpdateBandMemberRequest captured = captor.getValue();
                assertThat(captured.name()).isEqualTo("Kasper");
                assertThat(captured.role()).isEqualTo("Vocals");
                assertThat(captured.bio()).isEqualTo("Updated bio.");
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldRejectBlankBioWith400() throws Exception {
                mockMvc.perform(put("/api/admin/band-members/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                        "name": "Kasper",
                                                        "role": "Vocals",
                                                        "bio": "   "
                                                }
                                                """))
                                .andExpect(status().isBadRequest());

                verify(bandMemberService, never())
                                .editBandMemberById(any(Long.class), any(UpdateBandMemberRequest.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturn404WhenMemberNotFound() throws Exception {
                when(bandMemberService.editBandMemberById(eq(999L), any(UpdateBandMemberRequest.class)))
                                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Band member not found"));

                mockMvc.perform(put("/api/admin/band-members/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                        "name": "Ghost",
                                                        "role": "Vocals",
                                                        "bio": "Not real."
                                                }
                                                """))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldUploadBandMemberPhoto() throws Exception {
                BandMemberResponse response = new BandMemberResponse(
                                1L, "Kasper", "Vocals", "Bio.", "/uploads/members/new.jpg");
                when(bandMemberService.uploadBandMemberPhoto(eq(1L), any(MultipartFile.class)))
                                .thenReturn(response);

                MockMultipartFile file = new MockMultipartFile(
                                "file", "photo.jpg", "image/jpeg", "img-bytes".getBytes());

                mockMvc.perform(multipart("/api/admin/band-members/1/photo").file(file))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.photoUrl").value("/uploads/members/new.jpg"));

                verify(bandMemberService).uploadBandMemberPhoto(eq(1L), any(MultipartFile.class));
        }
}
