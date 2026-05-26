package org.example.eksamenbandbackend.dto;

public record UpdateContactInfoRequest(
        String email,
        String emailNote,
        String bookingEmail,
        String bookingNote
) {}
