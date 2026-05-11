package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.entity.Show;
import org.example.eksamenbandbackend.repository.ShowRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShowService {

    private final ShowRepository showRepository;

    public ShowService(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    public List<Show> getUpcomingShows() {
        return showRepository.findByDateAfterOrderByDateAsc(LocalDate.now());
    }

}
