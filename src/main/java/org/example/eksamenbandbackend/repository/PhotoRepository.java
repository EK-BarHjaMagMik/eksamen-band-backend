package org.example.eksamenbandbackend.repository;

import org.example.eksamenbandbackend.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

	java.util.List<Photo> findAllByOrderByDateTakenDesc();

}
