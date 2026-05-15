package org.example.eksamenbandbackend.service;

import org.example.eksamenbandbackend.entity.Photo;
import org.example.eksamenbandbackend.repository.PhotoRepository;
import org.example.eksamenbandbackend.dto.PhotoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    private PhotoService photoService;

    @BeforeEach
    void setUp() {
        photoService = new PhotoService(photoRepository);
    }

    @Test
    void getPhotos_returnsOrderedAndMapped() {
        Photo p1 = new Photo();
        p1.setId(1L);
        p1.setUrl("/p1.jpg");
        p1.setCaption("cap1");
        p1.setDateTaken(LocalDate.of(2026, 1, 2));
        p1.setPhotographer("ph1");

        Photo p2 = new Photo();
        p2.setId(2L);
        p2.setUrl("/p2.jpg");
        p2.setCaption("cap2");
        p2.setDateTaken(LocalDate.of(2026, 1, 1));
        p2.setPhotographer("ph2");

        when(photoRepository.findAllByOrderByDateTakenDesc()).thenReturn(List.of(p1, p2));

        List<PhotoResponse> res = photoService.getPhotos();

        assertEquals(2, res.size());
        assertEquals(p1.getId(), res.get(0).id());
        assertEquals(p1.getUrl(), res.get(0).url());
        assertEquals(p2.getId(), res.get(1).id());
    }

    @Test
    void getRecentPhotos_appliesLimitAndMaps() {
        int limit = 1;

        Photo p = new Photo();
        p.setId(10L);
        p.setUrl("/recent.jpg");
        p.setCaption("recent");
        p.setDateTaken(LocalDate.of(2026, 2, 1));
        p.setPhotographer("phx");

        when(photoRepository.findAllByOrderByDateTakenDesc(any(Pageable.class))).thenReturn(List.of(p));

        List<PhotoResponse> res = photoService.getRecentPhotos(limit);

        assertEquals(1, res.size());
        assertEquals(p.getId(), res.get(0).id());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(photoRepository).findAllByOrderByDateTakenDesc(captor.capture());
        Pageable used = captor.getValue();
        assertEquals(0, used.getPageNumber());
        assertEquals(limit, used.getPageSize());
    }
}
