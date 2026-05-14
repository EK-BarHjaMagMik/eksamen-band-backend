package org.example.eksamenbandbackend.dto;

import org.example.eksamenbandbackend.entity.BandBio;

public record BandBioResponse(Long id, String content) {
    public static BandBioResponse fromEntity(BandBio bandBio) {
        return new BandBioResponse(bandBio.getId(), bandBio.getContent());
    }
}
