package org.example.eksamenbandbackend.dto;

import org.example.eksamenbandbackend.entity.ContactInfo;

public record ContactInfoResponse(
        Long id,
        String email,
        String phoneNumber,
        String bookingNote
) {
    public static ContactInfoResponse fromEntity(ContactInfo contactInfo){
        return new ContactInfoResponse(
                contactInfo.getId(),
                contactInfo.getEmail(),
                contactInfo.getPhoneNumber(),
                contactInfo.getBookingNote()
        );
    }
}
