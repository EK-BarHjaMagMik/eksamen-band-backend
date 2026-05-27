package org.example.eksamenbandbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateShowRequest(
        @NotNull LocalDate date,
        @NotBlank String city,
        @NotBlank String venue,
        @Size(max = 2048) String ticketLink
) {
}
