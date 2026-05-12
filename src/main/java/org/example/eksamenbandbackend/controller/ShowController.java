package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.dto.ShowResponse;
import org.example.eksamenbandbackend.service.ShowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<ShowResponse>> getUpcomingShows() {
        return new ResponseEntity<>(showService.getUpcomingShows(), HttpStatus.OK);
    }

    @GetMapping("/past")
    public ResponseEntity<List<ShowResponse>> getPastShows() {
        return new ResponseEntity<>(showService.getPastShows(), HttpStatus.OK);
    }
}
