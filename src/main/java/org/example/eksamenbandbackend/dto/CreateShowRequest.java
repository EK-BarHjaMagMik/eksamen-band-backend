package org.example.eksamenbandbackend.dto;

import java.time.LocalDate;

public record CreateShowRequest(
        LocalDate date,
        String city,
        String venue,
        String ticketLink
) {
}
