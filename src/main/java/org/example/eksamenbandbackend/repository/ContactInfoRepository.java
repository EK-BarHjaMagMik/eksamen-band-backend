package org.example.eksamenbandbackend.repository;

import org.example.eksamenbandbackend.entity.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactInfoRepository extends JpaRepository<ContactInfo, Long> {
}
