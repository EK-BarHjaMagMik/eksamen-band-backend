package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.dto.ContactInfoResponse;
import org.example.eksamenbandbackend.entity.ContactInfo;
import org.example.eksamenbandbackend.repository.ContactInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactInfoService {
    private final ContactInfoRepository contactInfoRepository;

    public ContactInfoService(ContactInfoRepository contactInfoRepository){
        this.contactInfoRepository = contactInfoRepository;
    }

    public ContactInfo get(){
        return contactInfoRepository.findFirst().orElseThrow(() ->
                new RuntimeException("Contact info not initialized"));
    }

    public ContactInfo update(ContactInfo updated){
        ContactInfo existing = contactInfoRepository.findFirst().orElse(new ContactInfo());

        existing.setEmail(updated.getEmail());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setBookingNote(updated.getBookingNote());

        return contactInfoRepository.save(existing);
    }

    /*public ContactInfoResponse getResponse(){

    }*/
}
