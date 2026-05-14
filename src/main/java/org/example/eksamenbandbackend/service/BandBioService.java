package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.dto.BandBioResponse;
import org.example.eksamenbandbackend.repository.BandBioRepository;
import org.springframework.stereotype.Service;

@Service
public class BandBioService {

    private final BandBioRepository bandBioRepository;

    public BandBioService(BandBioRepository bandBioRepository) {
        this.bandBioRepository = bandBioRepository;
    }

    public BandBioResponse get() {
        return bandBioRepository.findTopByOrderByIdAsc()
                .map(BandBioResponse::fromEntity)
                .orElseThrow(() -> new RuntimeException("Band bio not initialized"));
    }
}
