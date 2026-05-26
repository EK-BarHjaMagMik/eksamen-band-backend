package org.example.eksamenbandbackend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record UpdatePhotoRequest(
        String caption,
        @NotNull(message = "Date taken cannot be null") @PastOrPresent(message = "Date taken must be a past or present date") LocalDate dateTaken,
        String photographer,
        Long showId) {
}
