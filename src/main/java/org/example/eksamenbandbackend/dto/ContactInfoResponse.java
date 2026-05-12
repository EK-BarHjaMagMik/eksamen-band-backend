package org.example.eksamenbandbackend.dto;

public record ContactInfoResponse(
        Long id,
        String email,
        String phoneNumber,
        String bookingNote
) {
    public static ContactInfoResponse fromEntity(org.example.eksamenbandbackend.entity.ContactInfo contactInfo){
        return new ContactInfoResponse(
                contactInfo.getId(),
                contactInfo.getEmail(),
                contactInfo.getPhoneNumber(),
                contactInfo.getBookingNote()
        );
    }
}
