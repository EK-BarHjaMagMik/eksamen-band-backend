package org.example.eksamenbandbackend.repository;

import org.example.eksamenbandbackend.entity.ContactInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ContactInfoRepositoryTest {

    @Autowired
    private ContactInfoRepository contactInfoRepository;

    @BeforeEach
    void setUp() {
        contactInfoRepository.deleteAll();

        ContactInfo contact = new ContactInfo();
        contact.setEmail("test@example.com");
        contact.setBookingNote("For bookings, contact us.");

        contactInfoRepository.save(contact);
    }

    @Test
    void shouldReturnContactInfoWhenPresent() {
        Optional<ContactInfo> result = contactInfoRepository.findTopByOrderByIdAsc();

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldReturnEmptyWhenNoContactInfo() {
        contactInfoRepository.deleteAll();

        Optional<ContactInfo> result = contactInfoRepository.findTopByOrderByIdAsc();

        assertThat(result).isEmpty();
    }
}
