package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.dto.BandMemberResponse;
import org.example.eksamenbandbackend.repository.BandMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BandMemberService {

    private final BandMemberRepository bandMemberRepository;

    public BandMemberService(BandMemberRepository bandMemberRepository) {
        this.bandMemberRepository = bandMemberRepository;
    }

    public List<BandMemberResponse> getBandMembers() {
        return bandMemberRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(BandMemberResponse::fromEntity)
                .toList();
    }
}
