package org.example.eksamenbandbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateContactInfoRequest(
        @NotBlank @Email String email,
        String emailNote,
        @Email String bookingEmail,
        String bookingNote
) {}
