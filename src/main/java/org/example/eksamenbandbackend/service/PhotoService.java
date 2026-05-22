package org.example.eksamenbandbackend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.example.eksamenbandbackend.dto.PhotoResponse;
import org.example.eksamenbandbackend.dto.UploadPhotosResponse;
import org.example.eksamenbandbackend.dto.UploadPhotosResponse.UploadError;
import org.example.eksamenbandbackend.dto.UploadPhotosResponse.UploadedPhoto;
import org.example.eksamenbandbackend.entity.Photo;
import org.example.eksamenbandbackend.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PhotoService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp");

    private static final long MAX_UPLOAD_SIZE_BYTES = 30L * 1024 * 1024; // 30 MB

    @Value("${app.upload-dir}")
    private String uploadDir;

    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public List<PhotoResponse> getPhotos() {
        return photoRepository.findAllByOrderByDateTakenDescCreatedAtDesc().stream()
                .map(PhotoResponse::fromEntity)
                .toList();
    }

    public List<PhotoResponse> getRecentPhotos(int limit) {
        return photoRepository
                .findAllByOrderByDateTakenDescCreatedAtDesc(PageRequest.of(0, Math.max(1, limit)))
                .stream()
                .map(PhotoResponse::fromEntity)
                .toList();
    }

    public List<PhotoResponse> getPhotosByShowId(Long showId) {
        return photoRepository.findAllByShowIdOrderByDateTakenDescCreatedAtDesc(showId).stream()
                .map(PhotoResponse::fromEntity)
                .toList();
    }

    public UploadPhotosResponse uploadPhotos(
            List<MultipartFile> files,
            String caption,
            String photographer,
            LocalDate dateTaken) {

        List<UploadedPhoto> uploaded = new ArrayList<>();
        List<UploadError> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            // Reject empty files
            if (file.isEmpty()) {
                errors.add(new UploadError(
                        file.getOriginalFilename(),
                        "File is empty"));
                continue;
            }

            // Enforce maximum file size
            if (file.getSize() > MAX_UPLOAD_SIZE_BYTES) {
                errors.add(new UploadError(
                        file.getOriginalFilename(),
                        "File is too large: " + file.getSize() + " bytes"));
                continue;
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
                errors.add(new UploadError(
                        file.getOriginalFilename(),
                        "Unsupported file type: " + contentType));
                continue;
            }

            try {
                // save file to disk
                Path savedPath = saveFile(file);
                String url = "/uploads/" + savedPath.getFileName();

                // Create DB entry
                Photo photo = new Photo();
                photo.setUrl(url);
                photo.setCaption(caption);
                photo.setPhotographer(photographer);
                photo.setDateTaken(dateTaken != null ? dateTaken : LocalDate.now());

                try {
                    photoRepository.save(photo);
                } catch (RuntimeException e) {
                    // DB write failed — remove the orphaned file and keep the batch going
                    Files.deleteIfExists(savedPath);
                    errors.add(new UploadError(file.getOriginalFilename(), "Failed to save photo"));
                    continue;
                }

                uploaded.add(new UploadedPhoto(photo.getId(), url));

            } catch (IOException e) {
                errors.add(new UploadError(file.getOriginalFilename(), "Failed to save file"));
            }
        }

        return new UploadPhotosResponse(uploaded, errors);
    }

    public Path saveFile(MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadDir.trim());
        Files.createDirectories(dir);

        String originalFilename = file.getOriginalFilename();
        String fileExt = "";

        // Safely extract extension
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID() + fileExt;
        Path targetPath = dir.resolve(filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, targetPath);
        }

        return targetPath;
    }

    public boolean deletePhoto(Long id) {
        Optional<Photo> optional = photoRepository.findById(id);

        if (optional.isEmpty()) {
            return false;
        }

        Photo photo = optional.get();
        String url = photo.getUrl();

        // 1. Delete DB record
        photoRepository.delete(photo);

        // 2. Delete file from storage — a failure here leaves a harmless orphan file
        deleteFileFromDisk(url);

        return true;
    }

    private void deleteFileFromDisk(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }

        String filename = Paths.get(url).getFileName().toString();
        Path filePath = Paths.get(uploadDir.trim()).resolve(filename);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + filePath + " - " + e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete photo file",
                    e);
        }
    }
}
