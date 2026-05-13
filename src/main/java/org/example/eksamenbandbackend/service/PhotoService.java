package org.example.eksamenbandbackend.service;

import java.util.Comparator;
import java.util.List;
import org.example.eksamenbandbackend.dto.PhotoResponse;
import org.example.eksamenbandbackend.repository.PhotoRepository;
import org.springframework.stereotype.Service;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public List<PhotoResponse> getPhotos() {
        return photoRepository.findAll().stream()
                .sorted(Comparator.comparing(p -> p.getDateTaken(), Comparator.nullsLast(Comparator.reverseOrder())))
                .map(PhotoResponse::fromEntity)
                .toList();
    }
}
