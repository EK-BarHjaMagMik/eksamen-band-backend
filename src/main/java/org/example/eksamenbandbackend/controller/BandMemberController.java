package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.dto.BandMemberResponse;
import org.example.eksamenbandbackend.service.BandMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequestMapping("/api/band-members")
public class BandMemberController {

    private final BandMemberService bandMemberService;

    public BandMemberController(BandMemberService bandMemberService) {
        this.bandMemberService = bandMemberService;
    }

    @GetMapping
    public ResponseEntity<List<BandMemberResponse>> getBandMembers() {
        return new ResponseEntity<>(bandMemberService.getBandMembers(), HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity <BandMemberResponse> getBandMemberById(@PathVariable Long id) {
        return new ResponseEntity<>(bandMemberService.getBandMemberById(id), HttpStatus.OK);
    }
}
