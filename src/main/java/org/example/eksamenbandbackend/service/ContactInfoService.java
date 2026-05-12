package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.dto.ContactInfoResponse;
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
        ContactInfo contactInfo = contactInfoRepository.findFirst().orElseThrow(() ->
                new RuntimeException("Contact info not initialized"));

        return ContactInfoResponse.fromEntity(contactInfo);
    }

    public ContactInfoResponse update(ContactInfo updated){
        ContactInfo existing = contactInfoRepository.findFirst().orElse(new ContactInfo());

        existing.setEmail(updated.getEmail());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setBookingNote(updated.getBookingNote());

        ContactInfo saved = contactInfoRepository.save(existing);

        return ContactInfoResponse.fromEntity(saved);
    }
}
