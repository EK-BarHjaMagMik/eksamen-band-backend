package org.example.eksamenbandbackend.dto;



import jakarta.validation.constraints.NotBlank;

public record UpdateBandMemberRequest(
        @NotBlank String name,
        @NotBlank String role,
        String bio,
        String photoUrl
) {}
