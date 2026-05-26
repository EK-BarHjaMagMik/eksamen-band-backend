package org.example.eksamenbandbackend.controller.admin;

import org.example.eksamenbandbackend.dto.ContactInfoResponse;
import org.example.eksamenbandbackend.dto.UpdateContactInfoRequest;
import org.example.eksamenbandbackend.security.JwtUtil;
import org.example.eksamenbandbackend.service.ContactInfoService;
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

@WebMvcTest(AdminContactInfoController.class)
class AdminContactInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactInfoService contactInfoService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateContactInfo() throws Exception {
        ContactInfoResponse response = new ContactInfoResponse(
                1L,
                "stuggofficial@gmail.com",
                "General inquiries",
                "booking@stugg.dk",
                "For bookings, contact us.");
        when(contactInfoService.update(any(UpdateContactInfoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/admin/contact-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "stuggofficial@gmail.com",
                            "emailNote": "General inquiries",
                            "bookingEmail": "booking@stugg.dk",
                            "bookingNote": "For bookings, contact us."
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("stuggofficial@gmail.com"))
                .andExpect(jsonPath("$.bookingEmail").value("booking@stugg.dk"))
                .andExpect(jsonPath("$.bookingNote").value("For bookings, contact us."));

        ArgumentCaptor<UpdateContactInfoRequest> captor = ArgumentCaptor.forClass(UpdateContactInfoRequest.class);
        verify(contactInfoService).update(captor.capture());

        UpdateContactInfoRequest captured = captor.getValue();
        assertThat(captured.email()).isEqualTo("stuggofficial@gmail.com");
        assertThat(captured.emailNote()).isEqualTo("General inquiries");
        assertThat(captured.bookingEmail()).isEqualTo("booking@stugg.dk");
        assertThat(captured.bookingNote()).isEqualTo("For bookings, contact us.");
    }
}
