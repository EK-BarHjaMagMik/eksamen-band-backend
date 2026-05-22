package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.dto.CreateShowRequest;
import org.example.eksamenbandbackend.dto.ShowResponse;
import org.example.eksamenbandbackend.entity.Show;
import org.example.eksamenbandbackend.repository.PhotoRepository;
import org.example.eksamenbandbackend.repository.ShowRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Show not found with id: " + showId));
    }

    public Show createShow(CreateShowRequest request){
        if (ifExistsByDate(request.date())){
            throw new IllegalArgumentException("There is already a show on this date");
        }

        Show show = new Show();
        show.setDate(request.date());
        show.setCity(request.city());
        show.setVenue(request.venue());
        show.setTicketLink(request.ticketLink());

        return showRepository.save(show);
    }

    public boolean ifExistsByDate(LocalDate date){
        return showRepository.findByDate(date).isPresent();
    }

    public ShowResponse editShowById(Long showId, CreateShowRequest request) {
        Show show = showRepository.findById(showId)
                .map(existingShow -> {
                    // if the dates are not the same and there is already a show on the new date, we throw an exception
                    // allowing us to change other fields than date without having to worry about the date conflict
                    if (!existingShow.getDate().equals(request.date()) && ifExistsByDate(request.date())){
                        throw new IllegalArgumentException("There is already a show on this date");
                    }
                    existingShow.setDate(request.date());
                    existingShow.setCity(request.city());
                    existingShow.setVenue(request.venue());
                    existingShow.setTicketLink(request.ticketLink());
                    return existingShow;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Show not found with id: " + showId));
        showRepository.save(show);
        boolean hasPhotos = photoRepository.existsByShowId(show.getId());
        return ShowResponse.fromEntity(show, hasPhotos);
    }

    public void deleteShowById(Long showId) {
        Show show = showRepository.findById(showId)
           .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Show not found with id: " + showId));
        showRepository.delete(show);
    }
}
