package org.example.eksamenbandbackend.dto;

public record CreateContactInfoRequest(
        String email,
        String phoneNumber,
        String bookingNote
) {

}
