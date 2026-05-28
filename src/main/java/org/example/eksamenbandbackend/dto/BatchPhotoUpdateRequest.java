package org.example.eksamenbandbackend.dto;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PastOrPresent;

public record BatchPhotoUpdateRequest(
        @NotNull(message = "Photo IDs cannot be null") Set<Long> photoIds,

        Optional<@Size(max = 255) String> caption,

        Optional<@PastOrPresent LocalDate> dateTaken,

        Optional<@Size(max = 255) String> photographer,

        Optional<Long> showId) {
}