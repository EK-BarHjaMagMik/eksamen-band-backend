package org.example.eksamenbandbackend.controller.admin;

import jakarta.validation.Valid;
import org.example.eksamenbandbackend.dto.BandMemberResponse;
import org.example.eksamenbandbackend.dto.UpdateBandMemberRequest;
import org.example.eksamenbandbackend.service.BandMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/band-members")
public class AdminBandMemberController {

    private final BandMemberService bandMemberService;

    public AdminBandMemberController(BandMemberService bandMemberService) {
        this.bandMemberService = bandMemberService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<BandMemberResponse> editBandMemberById(@PathVariable Long id, @Valid  @RequestBody UpdateBandMemberRequest request) {
        return new ResponseEntity<>(bandMemberService.editBandMemberById(id, request), HttpStatus.OK);
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<BandMemberResponse> uploadBandMemberPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(bandMemberService.uploadBandMemberPhoto(id, file), HttpStatus.OK);
    }
}