package org.example.eksamenbandbackend.dto;

import java.time.LocalDate;

import org.example.eksamenbandbackend.entity.Photo;

public record PhotoResponse(
        Long id,
        String url,
        String optimizedUrl,
        String thumbnailUrl,
        String caption,
        LocalDate dateTaken,
        String photographer,
        ShowResponse show) {

    public static PhotoResponse fromEntity(Photo photo) {
        return new PhotoResponse(
                photo.getId(),
                photo.getUrl(),
                photo.getOptimizedUrl(),
                photo.getThumbnailUrl(),
                photo.getCaption(),
                photo.getDateTaken(),
                photo.getPhotographer(),
                photo.getShow() != null
                        ? ShowResponse.fromEntity(photo.getShow(), true)
                        : null
        );
    }
}
