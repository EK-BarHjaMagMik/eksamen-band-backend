package org.example.eksamenbandbackend.controller;

import org.example.eksamenbandbackend.dto.ShowResponse;
import org.example.eksamenbandbackend.service.ShowService;
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
    public List<ShowResponse> getUpcomingShows() {
        return showService.getUpcomingShows();
    }
}
