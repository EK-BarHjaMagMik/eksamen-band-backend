package org.example.eksamenbandbackend.dto;

import java.util.List;

public record UploadPhotosResponse(
        List<UploadedPhoto> uploaded,
        List<UploadError> errors
) {
    public record UploadedPhoto(Long id, String url) {}
    public record UploadError(String filename, String reason) {}
}

