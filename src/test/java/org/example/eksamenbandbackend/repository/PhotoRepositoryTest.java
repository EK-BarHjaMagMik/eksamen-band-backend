package org.example.eksamenbandbackend.repository;

import org.example.eksamenbandbackend.entity.Photo;
import org.example.eksamenbandbackend.entity.Show;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PhotoRepositoryTest {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private ShowRepository showRepository;

    @BeforeEach
    void setUp() {
        photoRepository.deleteAll();
        showRepository.deleteAll();
    }

    @Test
    void findAllByOrderByDateTakenDescCreatedAtDesc_returnsPhotosSortedByDateDesc() {
        Photo p1 = new Photo();
        p1.setUrl("/a.jpg");
        p1.setCaption("A");
        p1.setDateTaken(LocalDate.of(2026, 3, 1));

        Photo p2 = new Photo();
        p2.setUrl("/b.jpg");
        p2.setCaption("B");
        p2.setDateTaken(LocalDate.of(2026, 1, 1));

        Photo p3 = new Photo();
        p3.setUrl("/c.jpg");
        p3.setCaption("C");
        p3.setDateTaken(LocalDate.of(2026, 2, 1));

        photoRepository.saveAll(List.of(p1, p2, p3));

        List<Photo> all = photoRepository.findAllByOrderByDateTakenDescCreatedAtDesc();

        assertThat(all).hasSize(3);
        assertThat(all.get(0).getDateTaken()).isAfter(all.get(1).getDateTaken());
        assertThat(all.get(1).getDateTaken()).isAfter(all.get(2).getDateTaken());
    }

    @Test
    void findAllByOrderByDateTakenDescCreatedAtDesc_withPageable_appliesLimit() {
        Photo p1 = new Photo();
        p1.setUrl("/a.jpg");
        p1.setCaption("A");
        p1.setDateTaken(LocalDate.of(2026, 3, 1));

        Photo p2 = new Photo();
        p2.setUrl("/b.jpg");
        p2.setCaption("B");
        p2.setDateTaken(LocalDate.of(2026, 1, 1));

        Photo p3 = new Photo();
        p3.setUrl("/c.jpg");
        p3.setCaption("C");
        p3.setDateTaken(LocalDate.of(2026, 2, 1));

        photoRepository.saveAll(List.of(p1, p2, p3));

        List<Photo> top2 = photoRepository.findAllByOrderByDateTakenDescCreatedAtDesc(PageRequest.of(0, 2));

        assertThat(top2).hasSize(2);
        assertThat(top2.get(0).getDateTaken()).isAfter(top2.get(1).getDateTaken());
    }

    @Test
    void existsByShowId_returnsTrueWhenPhotoLinkedToShow() {
        Show s = new Show();
        s.setDate(LocalDate.of(2026, 6, 1));
        s.setCity("City");
        s.setVenue("Venue");
        showRepository.save(s);

        Photo p = new Photo();
        p.setUrl("/show.jpg");
        p.setCaption("WithShow");
        p.setDateTaken(LocalDate.of(2026, 4, 1));
        p.setShow(s);

        photoRepository.save(p);

        assertThat(photoRepository.existsByShowId(s.getId())).isTrue();
        assertThat(photoRepository.existsByShowId(s.getId() + 999)).isFalse();
    }
}
