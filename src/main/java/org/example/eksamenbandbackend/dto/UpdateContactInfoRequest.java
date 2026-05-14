package org.example.eksamenbandbackend.dto;

public record UpdateContactInfoRequest(
        String email,
        String phoneNumber,
        String bookingNote
) {}
