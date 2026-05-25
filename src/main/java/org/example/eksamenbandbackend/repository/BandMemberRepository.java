package org.example.eksamenbandbackend.repository;

import org.example.eksamenbandbackend.entity.BandMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BandMemberRepository extends JpaRepository<BandMember, Long> {

    List<BandMember> findAllByOrderByDisplayOrderAsc();
}
