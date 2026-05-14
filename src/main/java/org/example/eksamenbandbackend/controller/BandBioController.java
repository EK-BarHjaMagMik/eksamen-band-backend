package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.dto.BandBioResponse;
import org.example.eksamenbandbackend.service.BandBioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/band-bio")
public class BandBioController {

    private final BandBioService bandBioService;

    public BandBioController(BandBioService bandBioService) {
        this.bandBioService = bandBioService;
    }

    @GetMapping
    public ResponseEntity<BandBioResponse> getBandBio() {
        return new ResponseEntity<>(bandBioService.get(), HttpStatus.OK);
    }
}
