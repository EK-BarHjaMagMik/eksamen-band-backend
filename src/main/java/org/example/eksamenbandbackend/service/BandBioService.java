package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.dto.BandBioResponse;
import org.example.eksamenbandbackend.dto.UpdateBandBioRequest;
import org.example.eksamenbandbackend.entity.BandBio;
import org.example.eksamenbandbackend.repository.BandBioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BandBioService {

    private final BandBioRepository bandBioRepository;

    public BandBioService(BandBioRepository bandBioRepository) {
        this.bandBioRepository = bandBioRepository;
    }

    public BandBioResponse get() {
        return bandBioRepository.findTopByOrderByIdAsc()
                .map(BandBioResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Band bio not initialized"));
    }

    public BandBioResponse update(UpdateBandBioRequest request) {
        BandBio existing = bandBioRepository.findTopByOrderByIdAsc().orElse(new BandBio());
        existing.setContent(request.content());
        BandBio saved = bandBioRepository.save(existing);
        return BandBioResponse.fromEntity(saved);
    }
}
