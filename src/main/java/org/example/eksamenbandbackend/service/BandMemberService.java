package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.dto.BandMemberResponse;
import org.example.eksamenbandbackend.repository.BandMemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public BandMemberResponse getBandMemberById(Long bandMemberId) {
        return bandMemberRepository.findById(bandMemberId)
                .map(BandMemberResponse::fromEntity)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "Band member not found with id: " + bandMemberId)
                );
    }
}