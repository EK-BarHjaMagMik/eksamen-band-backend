package org.example.eksamenbandbackend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import jakarta.validation.constraints.Size;

public record UpdatePhotoRequest(
        @Size(max = 255, message = "Caption must not exceed 255 characters") String caption,
        @NotNull(message = "Date taken cannot be null") @PastOrPresent(message = "Date taken must be a past or present date") LocalDate dateTaken,
        @Size(max = 255, message = "Photographer name must not exceed 255 characters") String photographer,
        Long showId) {
}
