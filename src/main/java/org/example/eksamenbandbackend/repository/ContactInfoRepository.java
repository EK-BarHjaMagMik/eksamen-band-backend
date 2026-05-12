package org.example.eksamenbandbackend.repository;

import org.example.eksamenbandbackend.entity.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactInfoRepository extends JpaRepository<ContactInfo, Long> {
    Optional<ContactInfo> findByEmail(String email);
    Optional<ContactInfo> findByPhoneNumber(String phoneNumber);
}
