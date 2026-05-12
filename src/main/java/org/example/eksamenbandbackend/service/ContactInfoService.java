package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.dto.ContactInfoResponse;
import org.example.eksamenbandbackend.dto.CreateContactInfoRequest;
import org.example.eksamenbandbackend.entity.ContactInfo;
import org.example.eksamenbandbackend.repository.ContactInfoRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactInfoService {
    private final ContactInfoRepository contactInfoRepository;

    public ContactInfoService(ContactInfoRepository contactInfoRepository){
        this.contactInfoRepository = contactInfoRepository;
    }

    public ContactInfo createContactInfo(CreateContactInfoRequest request){
        //Ensure that duplicate contact info is not created
        if (existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already used");
        }else if (existsByPhoneNumber(request.phoneNumber())){
            throw new IllegalArgumentException("Phone number already used");
        }

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail(request.email());
        contactInfo.setPhoneNumber(request.phoneNumber());
        contactInfo.setBookingNote(request.bookingNote());

        return contactInfoRepository.save(contactInfo);
    }

    /*public ContactInfoResponse getContactInfo(Long id){
        return contactInfoRepository.findAll().getFirst().
    }*/

    public boolean existsByEmail(String email){
        return contactInfoRepository.findByEmail(email).isPresent();
    }

    public boolean existsByPhoneNumber(String phoneNumber){
        return contactInfoRepository.findByPhoneNumber(phoneNumber).isPresent();
    }
}
