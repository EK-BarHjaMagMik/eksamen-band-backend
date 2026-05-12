package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.dto.ContactInfoResponse;
import org.example.eksamenbandbackend.service.ContactInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contactinfo")
public class ContactInfoController {

    private ContactInfoService contactInfoService;

    public ContactInfoController(ContactInfoService contactInfoService){
        this.contactInfoService = contactInfoService;
    }

    /*
    @GetMapping
    public ResponseEntity<ContactInfoResponse> getContactInfo(Long id){
    }*/
}
