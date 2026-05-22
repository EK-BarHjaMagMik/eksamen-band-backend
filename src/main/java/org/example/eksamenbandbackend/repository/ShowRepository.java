package org.example.eksamenbandbackend.repository;

import org.example.eksamenbandbackend.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByDateAfterOrderByDateAsc(LocalDate date);

    List<Show> findByDateLessThanEqualOrderByDateDesc(LocalDate date);

    Optional<Show> findByDate(LocalDate date);

    boolean existsByDate(LocalDate date);
}
