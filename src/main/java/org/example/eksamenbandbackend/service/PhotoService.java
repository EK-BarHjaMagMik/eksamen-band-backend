package org.example.eksamenbandbackend.service;

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
                .sorted((p1, p2) -> p2.getDateTaken().compareTo(p1.getDateTaken()))
                .map(PhotoResponse::fromEntity)
                .toList();
    }
}
