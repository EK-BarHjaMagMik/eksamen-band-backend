package org.example.eksamenbandbackend.repository;

import org.example.eksamenbandbackend.entity.Show;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ShowRepositoryTest {

    @Autowired
    private ShowRepository showRepository;

    @BeforeEach
    void setUp() {
        showRepository.deleteAll();

        Show past = new Show();
        past.setDate(LocalDate.now().minusDays(10));
        past.setCity("Copenhagen");
        past.setVenue("Vega");

        Show upcoming1 = new Show();
        upcoming1.setDate(LocalDate.now().plusDays(5));
        upcoming1.setCity("Aarhus");
        upcoming1.setVenue("Train");

        Show upcoming2 = new Show();
        upcoming2.setDate(LocalDate.now().plusDays(30));
        upcoming2.setCity("Køge");
        upcoming2.setVenue("Tapperiet");
        upcoming2.setTicketLink("https://www.tapperiet.nu");

        showRepository.saveAll(List.of(past, upcoming1, upcoming2));
    }

    @Test
    void shouldReturnOnlyUpcomingShows() {
        List<Show> upcoming = showRepository.findByDateAfterOrderByDateAsc(LocalDate.now());

        assertThat(upcoming).hasSize(2);
    }

    @Test
    void shouldNotReturnPastShows() {
        List<Show> upcoming = showRepository.findByDateAfterOrderByDateAsc(LocalDate.now());

        assertThat(upcoming).noneMatch(show -> show.getDate().isBefore(LocalDate.now()));
    }

    @Test
    void shouldReturnShowsSortedByDateAscending() {
        List<Show> upcoming = showRepository.findByDateAfterOrderByDateAsc(LocalDate.now());

        assertThat(upcoming.get(0).getDate()).isBefore(upcoming.get(1).getDate());
    }

    @Test
    void shouldReturnEmptyListWhenNoUpcomingShows() {
        showRepository.deleteAll();

        List<Show> upcoming = showRepository.findByDateAfterOrderByDateAsc(LocalDate.now());

        assertThat(upcoming).isEmpty();
    }
}
