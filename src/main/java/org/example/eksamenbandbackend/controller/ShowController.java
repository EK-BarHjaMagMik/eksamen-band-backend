package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.dto.ShowResponse;
import org.example.eksamenbandbackend.service.ShowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public ResponseEntity<List<ShowResponse>> getShows() {
        return new ResponseEntity<>(showService.getShows(), HttpStatus.OK);
    }    

    @GetMapping("/upcoming")
    public ResponseEntity<List<ShowResponse>> getUpcomingShows() {
        return new ResponseEntity<>(showService.getUpcomingShows(), HttpStatus.OK);
    }

    @GetMapping("/past")
    public ResponseEntity<List<ShowResponse>> getPastShows() {
        return new ResponseEntity<>(showService.getPastShows(), HttpStatus.OK);
    }

    @GetMapping("/{showId}")
    public ResponseEntity<ShowResponse> getShow(@PathVariable Long showId) {
        return new ResponseEntity<>(showService.getShowById(showId), HttpStatus.OK);
    }
    
}
