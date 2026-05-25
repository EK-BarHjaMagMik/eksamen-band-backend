package org.example.eksamenbandbackend.dto;

import org.example.eksamenbandbackend.entity.BandMember;

public record BandMemberResponse(
        Long id,
        String name,
        String role,
        String bio,
        String photoUrl)
{
    public static BandMemberResponse fromEntity(BandMember bandMember) {
        return new BandMemberResponse(
                bandMember.getId(),
                bandMember.getName(),
                bandMember.getRole(),
                bandMember.getBio(),
                bandMember.getPhotoUrl()
        );
    }
}
