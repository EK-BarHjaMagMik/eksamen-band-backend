package org.example.eksamenbandbackend.dto;

import org.example.eksamenbandbackend.entity.Show;

import java.time.LocalDate;

public record ShowResponse(
        Long id,
        LocalDate date,
        String city,
        String venue,
        String ticketLink,
        boolean hasPhotos) {
    public static ShowResponse fromEntity(Show show, boolean hasPhotos) {
        return new ShowResponse(
                show.getId(),
                show.getDate(),
                show.getCity(),
                show.getVenue(),
                show.getTicketLink(),
                hasPhotos);
    }
}
