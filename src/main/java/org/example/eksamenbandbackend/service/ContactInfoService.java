package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.dto.ContactInfoResponse;
import org.example.eksamenbandbackend.dto.UpdateContactInfoRequest;
import org.example.eksamenbandbackend.entity.ContactInfo;
import org.example.eksamenbandbackend.repository.ContactInfoRepository;
import org.springframework.stereotype.Service;


@Service
public class ContactInfoService {
    private final ContactInfoRepository contactInfoRepository;

    public ContactInfoService(ContactInfoRepository contactInfoRepository){
        this.contactInfoRepository = contactInfoRepository;
    }

    public ContactInfoResponse get(){
        ContactInfo contactInfo = contactInfoRepository.findTopByOrderByIdAsc().orElseThrow(() ->
                new RuntimeException("Contact info not initialized"));

        return ContactInfoResponse.fromEntity(contactInfo);
    }

    public ContactInfoResponse update(UpdateContactInfoRequest request){
        ContactInfo existing = contactInfoRepository.findTopByOrderByIdAsc().orElse(new ContactInfo());

        existing.setEmail(request.email());
        existing.setEmailNote(request.emailNote());
        existing.setBookingEmail(request.bookingEmail());
        existing.setBookingNote(request.bookingNote());

        ContactInfo saved = contactInfoRepository.save(existing);

        return ContactInfoResponse.fromEntity(saved);
    }
}
