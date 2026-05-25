package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.entity.Show;
import org.example.eksamenbandbackend.repository.PhotoRepository;
import org.example.eksamenbandbackend.repository.ShowRepository;
import org.example.eksamenbandbackend.dto.ShowResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowServiceTest {

    @Mock
    private ShowRepository showRepository;

    @Mock
    private PhotoRepository photoRepository;

    private ShowService showService;

    @BeforeEach
    void setUp() {
        showService = new ShowService(showRepository, photoRepository);
    }

    // -------------------------
    // Helper method
    // -------------------------
    private Show show(long id, LocalDate date, String city, String venue) {
        Show s = new Show();
        s.setId(id);
        s.setDate(date);
        s.setCity(city);
        s.setVenue(venue);
        return s;
    }

    // -------------------------
    // Upcoming shows
    // -------------------------
    @Test
    void getUpcomingShows_returnsOrderedAndHasPhotosFlagSet() {
        LocalDate today = LocalDate.now();

        Show s1 = show(1L, today.plusDays(1), "City A", "Venue A");
        Show s2 = show(2L, today.plusDays(2), "City B", "Venue B");

        when(showRepository.findByDateAfterOrderByDateAsc(today))
                .thenReturn(List.of(s1, s2));

        when(photoRepository.existsByShowId(1L)).thenReturn(true);
        when(photoRepository.existsByShowId(2L)).thenReturn(false);

        List<ShowResponse> responses = showService.getUpcomingShows();

        // Verify repository interactions
        verify(photoRepository).existsByShowId(1L);
        verify(photoRepository).existsByShowId(2L);

        assertEquals(2, responses.size());

        // First show
        ShowResponse r1 = responses.get(0);
        assertEquals(1L, r1.id());
        assertEquals(s1.getDate(), r1.date());
        assertEquals("City A", r1.city());
        assertEquals("Venue A", r1.venue());
        assertTrue(r1.hasPhotos());

        // Second show
        ShowResponse r2 = responses.get(1);
        assertEquals(2L, r2.id());
        assertEquals(s2.getDate(), r2.date());
        assertEquals("City B", r2.city());
        assertEquals("Venue B", r2.venue());
        assertFalse(r2.hasPhotos());
    }

    @Test
    void getUpcomingShows_returnsEmptyListWhenNoShows() {
        when(showRepository.findByDateAfterOrderByDateAsc(any(LocalDate.class)))
                .thenReturn(List.of());

        List<ShowResponse> responses = showService.getUpcomingShows();

        assertTrue(responses.isEmpty());
        verify(photoRepository, never()).existsByShowId(anyLong());
    }

    // -------------------------
    // Past shows
    // -------------------------
    @Test
    void getPastShows_returnsOrderedAndHasPhotosFlagSet() {
        LocalDate today = LocalDate.now();

        Show s1 = show(10L, today.minusDays(1), "City X", "Venue X");
        Show s2 = show(11L, today.minusDays(5), "City Y", "Venue Y");

        when(showRepository.findByDateLessThanEqualOrderByDateDesc(today))
                .thenReturn(List.of(s1, s2));

        when(photoRepository.existsByShowId(10L)).thenReturn(false);
        when(photoRepository.existsByShowId(11L)).thenReturn(true);

        List<ShowResponse> responses = showService.getPastShows();

        assertEquals(2, responses.size());

        // First show
        ShowResponse r1 = responses.get(0);
        assertEquals(10L, r1.id());
        assertEquals(s1.getDate(), r1.date());
        assertEquals("City X", r1.city());
        assertEquals("Venue X", r1.venue());
        assertFalse(r1.hasPhotos());

        // Second show
        ShowResponse r2 = responses.get(1);
        assertEquals(11L, r2.id());
        assertEquals(s2.getDate(), r2.date());
        assertEquals("City Y", r2.city());
        assertEquals("Venue Y", r2.venue());
        assertTrue(r2.hasPhotos());
    }

    @Test
    void getPastShows_returnsEmptyListWhenNoShows() {
        when(showRepository.findByDateLessThanEqualOrderByDateDesc(any(LocalDate.class)))
                .thenReturn(List.of());

        List<ShowResponse> responses = showService.getPastShows();

        assertTrue(responses.isEmpty());
        verify(photoRepository, never()).existsByShowId(anyLong());
    }

    // -------------------------
    // All shows (getShows)
    // -------------------------
    @Test
    void getShows_returnsOrderedAndHasPhotosFlagSet() {
        LocalDate today = LocalDate.now();

        Show s1 = show(100L, today.plusDays(3), "City One", "Venue One");
        Show s2 = show(101L, today.minusDays(2), "City Two", "Venue Two");

        when(showRepository.findAllByOrderByDateDesc()).thenReturn(List.of(s1, s2));
        when(photoRepository.existsByShowId(100L)).thenReturn(true);
        when(photoRepository.existsByShowId(101L)).thenReturn(false);

        List<ShowResponse> responses = showService.getShows();

        verify(photoRepository).existsByShowId(100L);
        verify(photoRepository).existsByShowId(101L);

        assertEquals(2, responses.size());

        ShowResponse r1 = responses.get(0);
        assertEquals(100L, r1.id());
        assertEquals(s1.getDate(), r1.date());
        assertEquals("City One", r1.city());
        assertTrue(r1.hasPhotos());

        ShowResponse r2 = responses.get(1);
        assertEquals(101L, r2.id());
        assertEquals(s2.getDate(), r2.date());
        assertEquals("City Two", r2.city());
        assertFalse(r2.hasPhotos());
    }

    @Test
    void getShows_returnsEmptyListWhenNoShows() {
        when(showRepository.findAllByOrderByDateDesc()).thenReturn(List.of());

        List<ShowResponse> responses = showService.getShows();

        assertTrue(responses.isEmpty());
        verify(photoRepository, never()).existsByShowId(anyLong());
    }

    // -------------------------
    // getShowById
    // -------------------------
    @Test
    void getShowById_returnsMappedResponseWhenFound() {
        LocalDate date = LocalDate.now();
        Show s = show(42L, date, "TestCity", "TestVenue");

        when(showRepository.findById(42L)).thenReturn(Optional.of(s));
        when(photoRepository.existsByShowId(42L)).thenReturn(true);

        ShowResponse resp = showService.getShowById(42L);

        verify(photoRepository).existsByShowId(42L);
        assertEquals(42L, resp.id());
        assertEquals(date, resp.date());
        assertTrue(resp.hasPhotos());
    }

    @Test
    void getShowById_throwsWhenNotFound() {
        when(showRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> showService.getShowById(99L));
        assertTrue(ex.getMessage().contains("Show not found"));
    }
}
