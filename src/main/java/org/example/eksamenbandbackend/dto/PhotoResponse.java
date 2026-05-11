package org.example.eksamenbandbackend.dto;

import java.time.LocalDate;

import org.example.eksamenbandbackend.entity.Photo;

public record PhotoResponse(
        Long photoId,
        String url,
        String caption,
        LocalDate dateTaken,
        String photographer) {

    public static PhotoResponse fromEntity(Photo photo) {
        return new PhotoResponse(
                photo.getPhotoId(),
                photo.getUrl(),
                photo.getCaption(),
                photo.getDateTaken(),
                photo.getPhotographer());
    }
}