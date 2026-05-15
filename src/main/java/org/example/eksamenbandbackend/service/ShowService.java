package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.dto.ShowResponse;
import org.example.eksamenbandbackend.repository.PhotoRepository;
import org.example.eksamenbandbackend.repository.ShowRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final PhotoRepository photoRepository;

    public ShowService(ShowRepository showRepository, PhotoRepository photoRepository) {
        this.showRepository = showRepository;
        this.photoRepository = photoRepository;
    }

    public List<ShowResponse> getUpcomingShows() {
        return showRepository.findByDateAfterOrderByDateAsc(LocalDate.now())
                .stream()
                .map(show -> {
                    boolean hasPhotos = photoRepository.existsByShowId(show.getId());
                    return ShowResponse.fromEntity(show, hasPhotos);
                })
                .toList();
    }

    public List<ShowResponse> getPastShows() {
        return showRepository.findByDateLessThanEqualOrderByDateDesc(LocalDate.now())
                .stream()
                .map(show -> {
                    boolean hasPhotos = photoRepository.existsByShowId(show.getId());
                    return ShowResponse.fromEntity(show, hasPhotos);
                })
                .toList();
    }

    public ShowResponse getShowById(Long showId) {
        return showRepository.findById(showId)
                .map(show -> {
                    boolean hasPhotos = photoRepository.existsByShowId(show.getId());
                    return ShowResponse.fromEntity(show, hasPhotos);
                })
                .orElseThrow(() -> new RuntimeException("Show not found with id: " + showId));
    }
}
