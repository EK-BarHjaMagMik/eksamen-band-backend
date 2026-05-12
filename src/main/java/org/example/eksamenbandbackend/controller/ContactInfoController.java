package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.dto.ContactInfoResponse;
import org.example.eksamenbandbackend.entity.ContactInfo;
import org.example.eksamenbandbackend.service.ContactInfoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactInfoController {

    private ContactInfoService contactInfoService;

    public ContactInfoController(ContactInfoService contactInfoService){
        this.contactInfoService = contactInfoService;
    }


    @GetMapping
    public ContactInfoResponse getContactInfo(){
        return contactInfoService.get();
    }

    @PutMapping
    public ContactInfoResponse updateContactInfo(@RequestBody ContactInfo dto){ //TODO: Refactor RequestBody to an actual DTO
        return contactInfoService.update(dto);
    }
}
