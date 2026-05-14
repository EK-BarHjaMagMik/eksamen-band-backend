package org.example.eksamenbandbackend.repository;

import java.util.List;

import org.example.eksamenbandbackend.entity.Photo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findAllByOrderByDateTakenDesc();

    // dynamic limit via Pageable: use PageRequest.of(0, limit)
    List<Photo> findAllByOrderByDateTakenDesc(Pageable pageable);

}
