package org.example.eksamenbandbackend.repository;

import org.example.eksamenbandbackend.entity.BandBio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BandBioRepository extends JpaRepository<BandBio, Long> {
    Optional<BandBio> findTopByOrderByIdAsc();
}
