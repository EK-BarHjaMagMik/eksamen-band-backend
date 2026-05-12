package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.dto.ContactInfoResponse;
import org.example.eksamenbandbackend.entity.ContactInfo;
import org.example.eksamenbandbackend.service.ContactInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactInfoController {

    private ContactInfoService contactInfoService;

    public ContactInfoController(ContactInfoService contactInfoService){
        this.contactInfoService = contactInfoService;
    }


    @GetMapping
    public ContactInfo getContactInfo(){
        return contactInfoService.get();
    }

    @PutMapping
    public ContactInfo updateContactInfo(@RequestBody ContactInfo dto){
        return contactInfoService.update(dto);
    }
}
