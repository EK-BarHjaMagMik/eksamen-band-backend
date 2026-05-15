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

    private static final LocalDate FIXED_TODAY = LocalDate.of(2026, 1, 1);

    @BeforeEach
    void setUp() {
        showRepository.deleteAll();

        Show past1 = new Show();
        past1.setDate(FIXED_TODAY.minusDays(10));
        past1.setCity("Copenhagen");
        past1.setVenue("Vega");

        Show past2 = new Show();
        past2.setDate(FIXED_TODAY.minusDays(5));
        past2.setCity("Odense");
        past2.setVenue("Fyn");

        Show upcoming1 = new Show();
        upcoming1.setDate(FIXED_TODAY.plusDays(5));
        upcoming1.setCity("Aarhus");
        upcoming1.setVenue("Train");

        Show upcoming2 = new Show();
        upcoming2.setDate(FIXED_TODAY.plusDays(30));
        upcoming2.setCity("Køge");
        upcoming2.setVenue("Tapperiet");
        upcoming2.setTicketLink("https://www.tapperiet.nu");

        showRepository.saveAll(List.of(past1, past2, upcoming1, upcoming2));
    }

    @Test
    void findByDateAfterOrderByDateAsc_returnsOnlyUpcomingShows() {
        List<Show> upcoming = showRepository.findByDateAfterOrderByDateAsc(FIXED_TODAY);

        assertThat(upcoming).hasSize(2);
    }

    @Test
    void findByDateAfterOrderByDateAsc_doesNotReturnPastShows() {
        List<Show> upcoming = showRepository.findByDateAfterOrderByDateAsc(FIXED_TODAY);

        assertThat(upcoming).noneMatch(show -> show.getDate().isBefore(FIXED_TODAY));
    }

    @Test
    void findByDateAfterOrderByDateAsc_returnsShowsSortedByDateAscending() {
        List<Show> upcoming = showRepository.findByDateAfterOrderByDateAsc(FIXED_TODAY);

        assertThat(upcoming.get(0).getDate()).isBefore(upcoming.get(1).getDate());
    }

    @Test
    void findByDateAfterOrderByDateAsc_returnsEmptyListWhenNoUpcomingShows() {
        showRepository.deleteAll();

        List<Show> upcoming = showRepository.findByDateAfterOrderByDateAsc(FIXED_TODAY);

        assertThat(upcoming).isEmpty();
    }

    @Test
    void findByDateLessThanEqualOrderByDateDesc_returnsOnlyPastShows() {
        List<Show> pastShows = showRepository.findByDateLessThanEqualOrderByDateDesc(FIXED_TODAY);

        assertThat(pastShows).hasSize(2);
        assertThat(pastShows.get(0).getDate()).isBefore(FIXED_TODAY);
    }

    @Test
    void findByDateLessThanEqualOrderByDateDesc_returnsShowsSortedByDateDescending() {
        List<Show> pastShows = showRepository.findByDateLessThanEqualOrderByDateDesc(FIXED_TODAY);

        assertThat(pastShows).hasSize(2);
        assertThat(pastShows.get(0).getDate()).isAfter(pastShows.get(1).getDate());
    }

    @Test
    void boundaryBehavior_inclusiveForPastAndExclusiveForUpcoming() {
        Show equalDay = new Show();
        equalDay.setDate(FIXED_TODAY);
        equalDay.setCity("EqualCity");
        equalDay.setVenue("EqualVenue");

        showRepository.save(equalDay);

        List<Show> pastOrEqual = showRepository.findByDateLessThanEqualOrderByDateDesc(FIXED_TODAY);
        assertThat(pastOrEqual).anyMatch(s -> s.getDate().isEqual(FIXED_TODAY));

        List<Show> upcoming = showRepository.findByDateAfterOrderByDateAsc(FIXED_TODAY);
        assertThat(upcoming).noneMatch(s -> s.getDate().isEqual(FIXED_TODAY));
    }
}
