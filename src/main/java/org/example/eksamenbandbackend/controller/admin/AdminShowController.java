package org.example.eksamenbandbackend.controller.admin;

import org.example.eksamenbandbackend.dto.CreateShowRequest;
import org.example.eksamenbandbackend.dto.ShowResponse;
import org.example.eksamenbandbackend.entity.Show;
import org.example.eksamenbandbackend.service.ShowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/shows")
public class AdminShowController {

    private final ShowService showService;

    public AdminShowController(ShowService showService) {
        this.showService = showService;
    }

    @PostMapping
    public ResponseEntity<ShowResponse> createShow(@RequestBody CreateShowRequest request) {
        Show show = showService.createShow(request);
        return ResponseEntity.ok(ShowResponse.fromEntity(show, false));
    }

    @PutMapping("/{showId}")
    public ResponseEntity<ShowResponse> updateShow(@PathVariable Long showId, @RequestBody CreateShowRequest request) {
    ShowResponse showResponse = showService.editShowById(showId, request);
        return ResponseEntity.ok(showResponse);
    }

    @DeleteMapping("/{showId}")
    public ResponseEntity<Void> deleteShow(@PathVariable Long showId) {
        showService.deleteShowById(showId);
        return ResponseEntity.noContent().build();
    }

}
