package org.example.eksamenbandbackend.controller.admin;

import jakarta.validation.Valid;
import org.example.eksamenbandbackend.dto.ContactInfoResponse;
import org.example.eksamenbandbackend.dto.UpdateContactInfoRequest;
import org.example.eksamenbandbackend.service.ContactInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/contact-info")
public class AdminContactInfoController {

    private final ContactInfoService contactInfoService;

    public AdminContactInfoController(ContactInfoService contactInfoService) {
        this.contactInfoService = contactInfoService;
    }

    @PutMapping
    public ResponseEntity<ContactInfoResponse> updateContactInfo(@Valid @RequestBody UpdateContactInfoRequest request) {
        return ResponseEntity.ok(contactInfoService.update(request));
    }
}
