package org.example.eksamenbandbackend.dto;

import java.time.LocalDate;

public record UpdatePhotoRequest(
        String caption,
        LocalDate dateTaken,
        String photographer,
        Long showId) {
}
