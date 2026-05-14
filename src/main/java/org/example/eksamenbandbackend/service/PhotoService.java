package org.example.eksamenbandbackend.service;

import java.util.List;
import org.example.eksamenbandbackend.dto.PhotoResponse;
import org.example.eksamenbandbackend.repository.PhotoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public List<PhotoResponse> getPhotos() {
        return photoRepository.findAllByOrderByDateTakenDesc().stream()
                .map(PhotoResponse::fromEntity)
                .toList();
    }

    public List<PhotoResponse> getRecentPhotos(int limit) {
        return photoRepository
            .findAllByOrderByDateTakenDesc(PageRequest.of(0, Math.max(1, limit)))
            .stream()
            .map(PhotoResponse::fromEntity)
            .toList();
    }
}
