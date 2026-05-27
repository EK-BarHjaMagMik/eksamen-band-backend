package org.example.eksamenbandbackend.controller.admin;

import jakarta.validation.Valid;
import org.example.eksamenbandbackend.dto.BandBioResponse;
import org.example.eksamenbandbackend.dto.UpdateBandBioRequest;
import org.example.eksamenbandbackend.service.BandBioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/band-bio")
public class AdminBandBioController {

    private final BandBioService bandBioService;

    public AdminBandBioController(BandBioService bandBioService) {
        this.bandBioService = bandBioService;
    }

    @PutMapping
    public ResponseEntity<BandBioResponse> updateBandBio(@Valid @RequestBody UpdateBandBioRequest request) {
        return ResponseEntity.ok(bandBioService.update(request));
    }
}
