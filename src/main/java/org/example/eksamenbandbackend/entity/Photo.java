package org.example.eksamenbandbackend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "photo",
    indexes = {
        @Index(name = "idx_photo_date_taken", columnList = "date_taken DESC")
    }
)
@Getter
@Setter
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long id;

    private String url;

    private String caption;

    @Column(nullable = false)
    private LocalDate dateTaken;

    private String photographer;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "show_id")
    private Show show;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
